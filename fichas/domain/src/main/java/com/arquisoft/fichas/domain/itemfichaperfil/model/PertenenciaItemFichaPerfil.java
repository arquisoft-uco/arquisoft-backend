package com.arquisoft.fichas.domain.itemfichaperfil.model;

import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record PertenenciaItemFichaPerfil(UUID fichaPerfil, boolean esPropietario,
                                         EstadoFichaPerfilDomain estadoActual) {

    public static final PertenenciaItemFichaPerfil VACIO = new PertenenciaItemFichaPerfil(
            UtilUUID.obtenerUUIDPorDefecto(), false, EstadoFichaPerfilDomain.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
