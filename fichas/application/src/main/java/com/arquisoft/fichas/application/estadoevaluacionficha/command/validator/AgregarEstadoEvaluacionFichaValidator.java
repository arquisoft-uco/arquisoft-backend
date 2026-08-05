package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;

import java.util.UUID;

public interface AgregarEstadoEvaluacionFichaValidator {

    void validar(UUID evaluacionFichaPerfil, UUID representanteComite, EstadoEvaluacion estadoEvaluacion);
}
