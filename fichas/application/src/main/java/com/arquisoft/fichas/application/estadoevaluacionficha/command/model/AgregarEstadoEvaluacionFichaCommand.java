package com.arquisoft.fichas.application.estadoevaluacionficha.command.model;

import java.util.UUID;

public record AgregarEstadoEvaluacionFichaCommand(
        UUID evaluacionFichaPerfil,
        String estadoEvaluacion,
        UUID representanteComite) {
}
