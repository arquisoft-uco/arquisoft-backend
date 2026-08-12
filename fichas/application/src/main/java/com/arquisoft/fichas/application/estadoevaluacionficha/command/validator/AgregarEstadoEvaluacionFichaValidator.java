package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;

public interface AgregarEstadoEvaluacionFichaValidator {

    void validar(AgregacionEstadoEvaluacionFichaDomain entrada, boolean evaluacionExiste, boolean esPropietario,
                 boolean estadoYaExiste);
}
