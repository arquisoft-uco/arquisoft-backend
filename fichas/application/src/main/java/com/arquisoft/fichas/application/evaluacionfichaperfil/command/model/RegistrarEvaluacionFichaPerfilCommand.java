package com.arquisoft.fichas.application.evaluacionfichaperfil.command.model;

import java.util.UUID;

public record RegistrarEvaluacionFichaPerfilCommand(
        UUID fichaPerfil,
        UUID representanteComite
) {
}
