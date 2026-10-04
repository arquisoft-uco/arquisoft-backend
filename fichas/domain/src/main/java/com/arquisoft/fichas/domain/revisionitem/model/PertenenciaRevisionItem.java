package com.arquisoft.fichas.domain.revisionitem.model;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record PertenenciaRevisionItem(UUID fichaPerfil, boolean esPropietario, EstadoRevision estadoRevision) {

    public static final PertenenciaRevisionItem VACIO = new PertenenciaRevisionItem(
            UtilUUID.obtenerUUIDPorDefecto(), false, EstadoRevision.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
