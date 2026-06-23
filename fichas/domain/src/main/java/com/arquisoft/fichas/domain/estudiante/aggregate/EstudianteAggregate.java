package com.arquisoft.fichas.domain.estudiante.aggregate;

import java.util.UUID;

public final class EstudianteAggregate {

    private UUID id;
    private String nombre;
    private String email;

    private EstudianteAggregate() {}

    private EstudianteAggregate(UUID id, String nombre, String email) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
    }

    public static EstudianteAggregate reconstruir(UUID id, String nombre, String email) {
        return new EstudianteAggregate(id, nombre, email);
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}
