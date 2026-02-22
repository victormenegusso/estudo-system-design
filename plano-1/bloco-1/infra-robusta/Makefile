.PHONY: up down down-clean downup create-ingressos clear-ingressos load-test

up:
	docker compose up -d --build

down:
	docker compose down

down-clean:
	docker compose down -v --remove-orphans

downup:
	docker compose down
	docker compose up --build -d

# Todo tráfego passa pelo Nginx (porta 8080), não diretamente para as apps
create-ingressos:
	curl -X POST "http://localhost:8080/test-admin/ingressos/bulk?quantidade=10000"

clear-ingressos:
	curl -X DELETE "http://localhost:8080/test-admin/ingressos"

# Script específico para validar comportamento do load balancer
load-test:
	K6_WEB_DASHBOARD=true k6 run ../tests/load-test-lb.js
