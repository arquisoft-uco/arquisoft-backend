package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator;

import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.EstadoEvaluacionCriteria;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionNoDuplicadoRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.PropietarioEvaluacionCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoEvaluacionFichaValidator {

    private final EvaluacionFichaExisteRule evaluacionFichaExisteRule;
    private final RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule;
    private final EstadoEvaluacionNoDuplicadoRule estadoEvaluacionNoDuplicadoRule;

    public void validar(UUID evaluacionFichaPerfil, UUID representanteComite, String estadoEvaluacion) {
        evaluacionFichaExisteRule.validar(evaluacionFichaPerfil);
        representantePropietarioEvaluacionRule.validar(
                new PropietarioEvaluacionCriteria(evaluacionFichaPerfil, representanteComite));
        estadoEvaluacionNoDuplicadoRule.validar(
                new EstadoEvaluacionCriteria(evaluacionFichaPerfil, estadoEvaluacion));
    }

    public EstadoEvaluacion resolverEstado(String estadoEvaluacion) {
        try {
            return EstadoEvaluacion.valueOf(estadoEvaluacion);
        } catch (IllegalArgumentException ex) {
            throw new EstadoEvaluacionNoEncontradoException(estadoEvaluacion);
        }
    }
}
