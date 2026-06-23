package com.arquisoft.fichas.domain.estudiante.aggregate;

import java.util.UUID;

public final class EstudianteAggregate {

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;

    private EstudianteAggregate() {}

    private EstudianteAggregate(UUID id, String identificador, String nombre, String email) {
        this.id = id;
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
    }

    public static EstudianteAggregate reconstruir(UUID id, String identificador, String nombre, String email) {
        return new EstudianteAggregate(id, identificador, nombre, email);
    }

    public UUID getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }
}
