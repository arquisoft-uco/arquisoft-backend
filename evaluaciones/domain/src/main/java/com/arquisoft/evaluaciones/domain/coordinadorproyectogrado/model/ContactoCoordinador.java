package com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.model;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record ContactoCoordinador(UUID id, String nombre, String email) {

    public static final ContactoCoordinador VACIO = new ContactoCoordinador(
            UtilUUID.obtenerUUIDPorDefecto(), UtilTexto.VACIO, UtilTexto.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
