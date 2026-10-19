package com.arquisoft.artefactos.infrastructure.revisionasesor.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_revision_asesor")
public class EstadoRevisionAsesorTestEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @Column(name = "orden")
    private Integer orden;

    protected EstadoRevisionAsesorTestEntity() {}

    public EstadoRevisionAsesorTestEntity(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
