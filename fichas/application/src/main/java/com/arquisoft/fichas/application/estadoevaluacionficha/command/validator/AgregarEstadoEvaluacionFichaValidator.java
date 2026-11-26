package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;

public interface AgregarEstadoEvaluacionFichaValidator {

    void validar(AgregacionEstadoEvaluacionFichaDomain entrada, boolean evaluacionExiste,
                 boolean esPropietario, boolean estadoYaExiste, EstadoEvaluacionFichaDomain ultimoEstado,
                 long observaciones, EstadoActualFicha estadoFicha);
}
