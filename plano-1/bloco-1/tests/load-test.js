import http from 'k6/http';
import { check, sleep } from 'k6';

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
      startTime: '30s', // Waits for smoke test
      tags: { test_type: 'load' },
    },
    stress_test: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: 2000 }, // Spike to 2000
        { duration: '1m', target: 2000 },
        { duration: '30s', target: 0 },
      ],
      startTime: '2m30s', // Waits for previous tests
      tags: { test_type: 'stress' },
    },
  },
};

export default function () {
  const url = 'http://localhost:8080';

  // Check availability
  const resDisponivel = http.get(`${url}/disponivel`);
  check(resDisponivel, { 'status is 200': (r) => r.status === 200 });

  // Buy ticket
  const updatedPayload = JSON.stringify({});
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const resCompra = http.post(`${url}/compra`, updatedPayload, params);

  check(resCompra, {
    'status is 200': (r) => r.status === 200,
    'status is 409 (Sold Out)': (r) => r.status === 409,
    'status is 500 (Error)': (r) => r.status === 500
  });

  sleep(1);
}
