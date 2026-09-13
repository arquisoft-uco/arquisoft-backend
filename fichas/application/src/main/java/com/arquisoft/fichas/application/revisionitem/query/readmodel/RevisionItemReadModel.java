package com.arquisoft.fichas.application.revisionitem.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record RevisionItemReadModel(
        UUID id,
        UUID item,
        String estadoRevision,
        String estadoRevisionNombre,
        Instant fechaCreacion
) {
}
