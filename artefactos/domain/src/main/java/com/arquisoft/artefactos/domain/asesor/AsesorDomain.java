package com.arquisoft.artefactos.domain.asesor;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public final class AsesorDomain {

    public static final AsesorDomain VACIO = reconstruir(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO);

    private UUID id;
    private String nombre;
    private String email;

    private AsesorDomain() {}

    public static AsesorDomain reconstruir(UUID id, String nombre, String email) {
        var replica = new AsesorDomain();
        replica.id = id;
        replica.nombre = nombre;
        replica.email = email;
        return replica;
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

    public boolean esVacio() {
        return this == VACIO;
    }
}
