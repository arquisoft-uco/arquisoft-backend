package com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel;

import java.util.UUID;

public record EvaluacionParticipanteReadModel(
        UUID id,
        String estadoId,
        String estadoNombre
) {
}
