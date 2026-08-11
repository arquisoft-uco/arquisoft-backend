package com.arquisoft.fichas.domain.estudiante;

import java.util.UUID;

public final class EstudianteDomain {

    private UUID id;
    private String identificador;
    private String nombre;
    private String email;

    private EstudianteDomain() {}

    private EstudianteDomain(UUID id, String identificador, String nombre, String email) {
        this.id = id;
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
    }

    public static EstudianteDomain reconstruir(UUID id, String identificador, String nombre, String email) {
        return new EstudianteDomain(id, identificador, nombre, email);
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
