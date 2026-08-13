package com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.AgregacionEstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.DisponibilidadEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.SolicitudEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.UltimoEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEnEvaluacionNoManualRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionEnTerminalRule;
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
    private final EstadoEnEvaluacionNoManualRule estadoEnEvaluacionNoManualRule;
    private final EstadoEvaluacionEnTerminalRule estadoEvaluacionEnTerminalRule;

    @Override
    public void validar(AgregacionEstadoEvaluacionFichaDomain entrada, boolean evaluacionExiste,
                        boolean esPropietario, boolean estadoYaExiste,
                        EstadoEvaluacionFichaDomain ultimoEstado) {

        evaluacionFichaExisteRule.validar(
                new ExistenciaEvaluacionFicha(entrada.getEvaluacionFichaPerfil(), evaluacionExiste));

        representantePropietarioEvaluacionRule.validar(new PropiedadEvaluacionFicha(
                entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite(), esPropietario));

        estadoEvaluacionNoDuplicadoRule.validar(new DisponibilidadEstadoEvaluacion(
                entrada.getEvaluacionFichaPerfil(), entrada.getEstadoEvaluacion(), estadoYaExiste));

        estadoEnEvaluacionNoManualRule.validar(new SolicitudEstadoEvaluacion(
                entrada.getEvaluacionFichaPerfil(), entrada.getEstadoEvaluacion()));

        estadoEvaluacionEnTerminalRule.validar(new UltimoEstadoEvaluacion(
                entrada.getEvaluacionFichaPerfil(), ultimoEstado.getEstadoEvaluacion()));
    }
}
