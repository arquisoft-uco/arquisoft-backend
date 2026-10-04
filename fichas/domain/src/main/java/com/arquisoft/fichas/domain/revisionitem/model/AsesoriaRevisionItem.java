package com.arquisoft.fichas.domain.revisionitem.model;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record AsesoriaRevisionItem(UUID fichaPerfil, UUID asesorFicha, EstadoRevision estadoRevision) {

    public static final AsesoriaRevisionItem VACIO = new AsesoriaRevisionItem(
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(), EstadoRevision.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
