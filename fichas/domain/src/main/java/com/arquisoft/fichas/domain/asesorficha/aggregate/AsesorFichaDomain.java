package com.arquisoft.fichas.domain.asesorficha.aggregate;

import java.util.UUID;

public final class AsesorFichaDomain {

    private final UUID id;
    private final String identificador;
    private final String nombre;
    private final String email;

    private AsesorFichaDomain(UUID id, String identificador, String nombre, String email) {
        this.id = id;
        this.identificador = identificador;
        this.nombre = nombre;
        this.email = email;
    }

    public static AsesorFichaDomain reconstruir(UUID id, String identificador, String nombre, String email) {
        return new AsesorFichaDomain(id, identificador, nombre, email);
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
