package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.DisponibilidadEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionNoDuplicadoRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarEstadoEvaluacionFichaValidatorImpl implements AgregarEstadoEvaluacionFichaValidator {

    private final EvaluacionFichaExisteRule evaluacionFichaExisteRule;
    private final RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule;
    private final EstadoEvaluacionNoDuplicadoRule estadoEvaluacionNoDuplicadoRule;

    @Override
    public void validar(AgregacionEstadoEvaluacionFichaDomain entrada, boolean evaluacionExiste,
                        boolean esPropietario, boolean estadoYaExiste) {

        evaluacionFichaExisteRule.validar(
                new ExistenciaEvaluacionFicha(entrada.getEvaluacionFichaPerfil(), evaluacionExiste));

        representantePropietarioEvaluacionRule.validar(new PropiedadEvaluacionFicha(
                entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite(), esPropietario));

        estadoEvaluacionNoDuplicadoRule.validar(new DisponibilidadEstadoEvaluacion(
                entrada.getEvaluacionFichaPerfil(), entrada.getEstadoEvaluacion(), estadoYaExiste));
    }
}
