package com.arquisoft.artefactos.infrastructure.observacion.support;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_observacion")
public class EstadoObservacionTestEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    protected EstadoObservacionTestEntity() {}

    public EstadoObservacionTestEntity(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
}
