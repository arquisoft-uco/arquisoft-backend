package com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EvaluacionFichaPerfilEntity(
        UUID id,
        UUID representanteComiteId,
        UUID fichaPerfilId,
        Instant fechaCreacion) {
}
