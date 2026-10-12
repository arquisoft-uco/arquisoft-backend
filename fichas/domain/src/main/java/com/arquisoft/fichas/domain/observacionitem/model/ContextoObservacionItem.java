package com.arquisoft.fichas.domain.observacionitem.model;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record ContextoObservacionItem(UUID revisionItem, EstadoRevision estadoRevision, UUID fichaPerfil, UUID asesorFicha) {

    public static final ContextoObservacionItem VACIO = new ContextoObservacionItem(
            UtilUUID.obtenerUUIDPorDefecto(), EstadoRevision.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto());

    public boolean esVacio() {
        return this == VACIO;
    }
}
