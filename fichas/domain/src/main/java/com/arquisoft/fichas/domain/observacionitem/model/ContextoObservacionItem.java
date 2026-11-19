package com.arquisoft.fichas.domain.observacionitem.model;

import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.util.UtilUUID;

import java.util.UUID;

public record ContextoObservacionItem(UUID revisionItem, EstadoRevision estadoRevision, UUID fichaPerfil, UUID asesorFicha,
                                      EstadoObservacionRevision estadoObservacion) {

    public static final ContextoObservacionItem VACIO = new ContextoObservacionItem(
            UtilUUID.obtenerUUIDPorDefecto(), EstadoRevision.VACIO,
            UtilUUID.obtenerUUIDPorDefecto(), UtilUUID.obtenerUUIDPorDefecto(),
            EstadoObservacionRevision.VACIO);

    public boolean esVacio() {
        return this == VACIO;
    }
}
