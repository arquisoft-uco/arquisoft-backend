package com.arquisoft.fichas.domain.estadoficha.aggregate;

import java.util.UUID;

public final class EstadoFichaAggregate {

    private final UUID id;
    private final String nombre;
    private final String descripcion;

    private EstadoFichaAggregate(UUID id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public static EstadoFichaAggregate reconstruir(UUID id, String nombre, String descripcion) {
        return new EstadoFichaAggregate(id, nombre, descripcion);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
