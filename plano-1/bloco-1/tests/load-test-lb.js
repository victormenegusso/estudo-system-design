import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

// ── Métricas customizadas para rastrear distribuição entre instâncias ──────────
// O header X-Served-By retorna o IP interno do container (ex: 172.18.0.3:8080).
// Usamos dois contadores para verificar se o Nginx está distribuindo as requests.
const requestsApp1 = new Counter('requests_app1');
const requestsApp2 = new Counter('requests_app2');
const rateLimitedRequests = new Counter('requests_rate_limited');

// ── Cenários (mesmos da infra-ingenua) ────────────────────────────────────────
export const options = {
    scenarios: {
        smoke_test: {
            executor: 'constant-vus',
            vus: 10,
            duration: '30s',
            tags: { test_type: 'smoke' },
        },
        load_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 500 },
                { duration: '1m', target: 500 },
                { duration: '30s', target: 0 },
            ],
            startTime: '30s',
            tags: { test_type: 'load' },
        },
        stress_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 2000 },
                { duration: '1m', target: 2000 },
                { duration: '30s', target: 0 },
            ],
            startTime: '2m30s',
            tags: { test_type: 'stress' },
        },
    },
    thresholds: {
        // O rate limiting deve ser observado: esperamos algum 429 sob stress
        'http_req_failed': ['rate<0.15'],          // menos de 15% de falhas totais
        'http_req_duration': ['p(95)<2000'],        // 95% das requests abaixo de 2s
    },
};

// Todo tráfego passa pelo Nginx (L7) na porta 8080
const BASE_URL = 'http://localhost:8080';

export default function () {
    // ── Verificar disponibilidade ──────────────────────────────────────────────
    const resDisponivel = http.get(`${BASE_URL}/disponivel`);

    check(resDisponivel, {
        'disponivel: status 200': (r) => r.status === 200,
        'disponivel: tem X-Served-By': (r) => r.headers['X-Served-By'] !== undefined,
    });

    // Rastreia qual instância respondeu a partir do header X-Served-By
    trackInstance(resDisponivel.headers['X-Served-By']);

    // ── Comprar ingresso ───────────────────────────────────────────────────────
    const params = { headers: { 'Content-Type': 'application/json' } };
    const resCompra = http.post(`${BASE_URL}/compra`, JSON.stringify({}), params);

    check(resCompra, {
        'compra: status 200 (sucesso)': (r) => r.status === 200,
        'compra: status 409 (esgotado)': (r) => r.status === 409,
        'compra: status 429 (rate limited)': (r) => r.status === 429,
        'compra: status 500 (erro)': (r) => r.status === 500,
    });

    if (resCompra.status === 429) {
        rateLimitedRequests.add(1);
    }

    trackInstance(resCompra.headers['X-Served-By']);

    sleep(1);
}

// ── Função auxiliar para rastrear instâncias ───────────────────────────────────
// O header X-Served-By contém o IP:porta do container que respondeu.
// Como não sabemos os IPs de antemão, guardamos a primeira ocorrência de cada IP.
let knownInstances = {};
let instanceCount = 0;

function trackInstance(servedBy) {
    if (!servedBy) return;

    const ip = servedBy.split(':')[0]; // extrai só o IP (sem a porta)

    if (knownInstances[ip] === undefined) {
        instanceCount++;
        knownInstances[ip] = instanceCount;
    }

    if (knownInstances[ip] === 1) {
        requestsApp1.add(1);
    } else if (knownInstances[ip] === 2) {
        requestsApp2.add(1);
    }
}
