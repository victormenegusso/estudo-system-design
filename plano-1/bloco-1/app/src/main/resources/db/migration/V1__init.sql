CREATE TABLE ingresso (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(255) NOT NULL,
    tipo VARCHAR(255)
);

-- Popula 100.000 ingressos
INSERT INTO ingresso (status, tipo)
SELECT 'DISPONIVEL', 'GERAL'
FROM generate_series(1, 100000);
