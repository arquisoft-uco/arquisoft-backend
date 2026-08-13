package com.arquisoft.fichas.domain.asesorficha;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class AsesorFichaDomain {

    public static final AsesorFichaDomain VACIO = new AsesorFichaDomain(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO);

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

    public boolean esVacio() {
        return this != VACIO;
    }
}
