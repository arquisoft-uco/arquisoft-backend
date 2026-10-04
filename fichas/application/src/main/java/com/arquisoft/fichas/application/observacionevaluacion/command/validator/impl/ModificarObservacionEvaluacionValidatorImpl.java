package com.arquisoft.fichas.application.observacionevaluacion.command.validator.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.validator.ModificarObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.PropiedadEvaluacionFicha;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.RepresentantePropietarioEvaluacionRule;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl.RepresentantePropietarioEvaluacionRuleImpl;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
import com.arquisoft.fichas.domain.observacionevaluacion.model.DisponibilidadObservacionEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.model.EstadoEvaluacionObservada;
import com.arquisoft.fichas.domain.observacionevaluacion.model.ExistenciaObservacionEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.model.PertenenciaObservacionEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.EvaluacionFichaAbiertaRule;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.ObservacionEvaluacionExisteRule;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.ObservacionEvaluacionNoDuplicadaRule;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.impl.EvaluacionFichaAbiertaRuleImpl;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.impl.ObservacionEvaluacionExisteRuleImpl;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.impl.ObservacionEvaluacionNoDuplicadaRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarObservacionEvaluacionValidatorImpl implements ModificarObservacionEvaluacionValidator {

    private final ObservacionEvaluacionExisteRule observacionEvaluacionExisteRule;
    private final RepresentantePropietarioEvaluacionRule representantePropietarioEvaluacionRule;
    private final EvaluacionFichaAbiertaRule evaluacionFichaAbiertaRule;
    private final ObservacionEvaluacionNoDuplicadaRule observacionEvaluacionNoDuplicadaRule;

    public ModificarObservacionEvaluacionValidatorImpl() {
        this.observacionEvaluacionExisteRule = new ObservacionEvaluacionExisteRuleImpl();
        this.representantePropietarioEvaluacionRule = new RepresentantePropietarioEvaluacionRuleImpl();
        this.evaluacionFichaAbiertaRule = new EvaluacionFichaAbiertaRuleImpl();
        this.observacionEvaluacionNoDuplicadaRule = new ObservacionEvaluacionNoDuplicadaRuleImpl();
    }

    @Override
    public void validar(ModificacionObservacionEvaluacionDomain entrada, boolean observacionExiste,
                        PertenenciaObservacionEvaluacion pertenencia, boolean observacionYaExiste) {
        observacionEvaluacionExisteRule.validar(
                new ExistenciaObservacionEvaluacion(entrada.getObservacionEvaluacion(), observacionExiste));
        representantePropietarioEvaluacionRule.validar(new PropiedadEvaluacionFicha(
                pertenencia.evaluacionFichaPerfil(), entrada.getRepresentanteComite(), pertenencia.esPropietario()));
        evaluacionFichaAbiertaRule.validar(
                new EstadoEvaluacionObservada(pertenencia.evaluacionFichaPerfil(), pertenencia.ultimoEstado()));
        observacionEvaluacionNoDuplicadaRule.validar(new DisponibilidadObservacionEvaluacion(
                pertenencia.evaluacionFichaPerfil(), entrada.getObservacion(), observacionYaExiste));
    }
}
