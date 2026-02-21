-- Altera a sequence gerada implicitamente pelo BIGSERIAL para suportar allocationSize=1000
-- permitindo batch inserts hiper-eficientes pelo Hibernate
ALTER SEQUENCE ingresso_id_seq INCREMENT BY 1000;
