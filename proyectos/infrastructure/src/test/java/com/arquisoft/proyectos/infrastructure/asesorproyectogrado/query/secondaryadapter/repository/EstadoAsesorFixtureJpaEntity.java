package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_asesor")
public class EstadoAsesorFixtureJpaEntity {

    @Id
    @Column(name = "id", length = 60)
    private String id;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    protected EstadoAsesorFixtureJpaEntity() {
    }

    public EstadoAsesorFixtureJpaEntity(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
