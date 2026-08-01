package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.PropietarioEvaluacionCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.port.out.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoEvaluacionFichaValidator {

    private final EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;
    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    public void validarEvaluacionExiste(UUID evaluacionFichaPerfil) {
        if (!evaluacionFichaPerfilQueryOutputPort.existePorId(evaluacionFichaPerfil)) {
            throw new EvaluacionFichaPerfilNoEncontradaException(evaluacionFichaPerfil);
        }
    }

    public void validarRepresentantePropietario(PropietarioEvaluacionCriteria criteria) {
        if (!evaluacionFichaPerfilQueryOutputPort.esRepresentantePropietario(criteria)) {
            throw new EvaluacionFichaNoPropiaException(criteria.evaluacionFichaPerfil());
        }
    }

    public void validarEstadoNoDuplicado(UUID evaluacionFichaPerfil, String estadoEvaluacion) {
        if (estadoEvaluacionFichaOutputPort.existePorEvaluacionYEstado(evaluacionFichaPerfil, estadoEvaluacion)) {
            throw new EstadoEvaluacionDuplicadoException(evaluacionFichaPerfil, estadoEvaluacion);
        }
    }

    public EstadoEvaluacion resolverEstado(String estadoEvaluacion) {
        try {
            return EstadoEvaluacion.valueOf(estadoEvaluacion);
        } catch (IllegalArgumentException ex) {
            throw new EstadoEvaluacionNoEncontradoException(estadoEvaluacion);
        }
    }
}
