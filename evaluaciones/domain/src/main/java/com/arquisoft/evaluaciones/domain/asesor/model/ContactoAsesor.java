package com.arquisoft.evaluaciones.domain.asesor.model;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record ContactoAsesor(UUID id, String nombre, String email) {

    public static final ContactoAsesor VACIO = new ContactoAsesor(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
