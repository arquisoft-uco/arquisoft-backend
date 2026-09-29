package com.arquisoft.fichas.application.observacionitem.query.readmodel;

import java.util.UUID;

public record ObservacionItemReadModel(
        UUID id,
        UUID revisionItem,
        String observacion,
        String estadoObservacionRevision,
        String estadoObservacionRevisionNombre
) {
}
