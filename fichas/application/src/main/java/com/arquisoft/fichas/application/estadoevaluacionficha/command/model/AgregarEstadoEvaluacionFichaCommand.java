package com.arquisoft.fichas.application.estadoevaluacionficha.command.model;

import java.util.UUID;

public record AgregarEstadoEvaluacionFichaCommand(
        UUID evaluacionFichaPerfilId,
        String estadoEvaluacionId) {
}
