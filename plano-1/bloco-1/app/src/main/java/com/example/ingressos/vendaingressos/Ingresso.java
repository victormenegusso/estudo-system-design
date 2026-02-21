package com.example.ingressos.vendaingressos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

@Entity
@Table(name = "ingresso")
public class Ingresso {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingresso_seq")
    @jakarta.persistence.SequenceGenerator(name = "ingresso_seq", sequenceName = "ingresso_id_seq", allocationSize = 1000)
    private Long id;
    private String status;
    private String tipo;

    public Ingresso() {
    }

    public Ingresso(String status, String tipo) {
        this.status = status;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
