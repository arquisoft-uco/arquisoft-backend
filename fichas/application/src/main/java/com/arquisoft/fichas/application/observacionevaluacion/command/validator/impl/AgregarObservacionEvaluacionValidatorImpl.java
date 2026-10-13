package com.arquisoft.fichas.application.observacionevaluacion.command.validator.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.validator.AgregarObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.ExistenciaEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EvaluacionFichaExisteRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.EvaluacionFichaExisteRuleImpl;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.RepresentantePropietarioEvaluacionRuleImpl;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.model.DisponibilidadObservacionEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.model.EstadoEvaluacionObservada;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.EvaluacionFichaAbiertaRule;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.ObservacionEvaluacionNoDuplicadaRule;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.impl.EvaluacionFichaAbiertaRuleImpl;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.impl.ObservacionEvaluacionNoDuplicadaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AgregarObservacionEvaluacionValidatorImpl implements AgregarObservacionEvaluacionValidator {

    private final EvaluacionFichaExisteRule evaluacionFichaExisteRule;
    private final RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule;
    private final EvaluacionFichaAbiertaRule evaluacionFichaAbiertaRule;
    private final ObservacionEvaluacionNoDuplicadaRule observacionEvaluacionNoDuplicadaRule;

    public AgregarObservacionEvaluacionValidatorImpl() {
        this.evaluacionFichaExisteRule = new EvaluacionFichaExisteRuleImpl();
        this.representantePropietarioEvaluacionRule = new RepresentantePropietarioEvaluacionRuleImpl();
        this.evaluacionFichaAbiertaRule = new EvaluacionFichaAbiertaRuleImpl();
        this.observacionEvaluacionNoDuplicadaRule = new ObservacionEvaluacionNoDuplicadaRuleImpl();
    }

    @Override
    public void validar(AgregacionObservacionEvaluacionDomain entrada, boolean evaluacionExiste, boolean esPropietario,
                        EstadoEvaluacion ultimoEstado, boolean observacionYaExiste) {
        evaluacionFichaExisteRule.validar(
                new ExistenciaEvaluacionFicha(entrada.getEvaluacionFichaPerfil(), evaluacionExiste));
        representantePropietarioEvaluacionRule.validar(new PropiedadEvaluacionFicha(
                entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite(), esPropietario));
        evaluacionFichaAbiertaRule.validar(
                new EstadoEvaluacionObservada(entrada.getEvaluacionFichaPerfil(), ultimoEstado));
        observacionEvaluacionNoDuplicadaRule.validar(new DisponibilidadObservacionEvaluacion(
                entrada.getEvaluacionFichaPerfil(), entrada.getObservacion(), observacionYaExiste));
    }
}
