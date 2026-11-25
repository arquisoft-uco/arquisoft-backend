package com.arquisoft.fichas.application.observacionevaluacion.command.validator.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.validator.RemoverObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.RepresentantePropietarioEvaluacionRuleImpl;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.model.EstadoEvaluacionObservada;
import com.arquisoft.fichas.domain.observacionevaluacion.model.ExistenciaObservacionEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.EvaluacionFichaAbiertaRule;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.ObservacionEvaluacionExisteRule;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.impl.EvaluacionFichaAbiertaRuleImpl;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.impl.ObservacionEvaluacionExisteRuleImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.model.EstadoActualFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilDisponibleParaEvaluacionRule;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.impl.EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverObservacionEvaluacionValidatorImpl implements RemoverObservacionEvaluacionValidator {

    private final ObservacionEvaluacionExisteRule observacionEvaluacionExisteRule;
    private final RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule;
    private final EvaluacionFichaAbiertaRule evaluacionFichaAbiertaRule;
    private final EstadoFichaPerfilDisponibleParaEvaluacionRule estadoFichaDisponibleRule;

    public RemoverObservacionEvaluacionValidatorImpl() {
        this.observacionEvaluacionExisteRule = new ObservacionEvaluacionExisteRuleImpl();
        this.representantePropietarioEvaluacionRule = new RepresentantePropietarioEvaluacionRuleImpl();
        this.evaluacionFichaAbiertaRule = new EvaluacionFichaAbiertaRuleImpl();
        this.estadoFichaDisponibleRule = new EstadoFichaPerfilDisponibleParaEvaluacionRuleImpl();
    }

    @Override
    public void validar(RemocionObservacionEvaluacionDomain entrada, boolean observacionExiste,
                        PertenenciaObservacionEvaluacion pertenencia) {
        observacionEvaluacionExisteRule.validar(
                new ExistenciaObservacionEvaluacion(entrada.getObservacionEvaluacion(), observacionExiste));
        representantePropietarioEvaluacionRule.validar(new PropiedadEvaluacionFicha(
                pertenencia.evaluacionFichaPerfil(), entrada.getRepresentanteComite(), pertenencia.esPropietario()));
        evaluacionFichaAbiertaRule.validar(
                new EstadoEvaluacionObservada(pertenencia.evaluacionFichaPerfil(), pertenencia.ultimoEstado()));
        estadoFichaDisponibleRule.validar(
                new EstadoActualFicha(pertenencia.fichaPerfil(), pertenencia.estadoFicha()));
    }
}
