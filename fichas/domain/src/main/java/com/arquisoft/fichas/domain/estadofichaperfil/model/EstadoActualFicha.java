package com.arquisoft.fichas.domain.estadofichaperfil.model;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record EstadoActualFicha(UUID fichaPerfil, EstadoFicha estadoActual) {

    public static final EstadoActualFicha VACIO =
            new EstadoActualFicha(UtilUUID.obtenerUUIDPorDefecto(), EstadoFicha.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
