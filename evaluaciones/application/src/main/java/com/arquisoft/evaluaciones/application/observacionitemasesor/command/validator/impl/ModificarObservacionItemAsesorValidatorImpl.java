package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.ModificarObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.DisponibilidadDescripcionObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.EstadoModificacionObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.DescripcionObservacionItemAsesorUnicaRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.ObservacionItemAsesorAdmiteModificacionRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.ObservacionItemAsesorExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.DescripcionObservacionItemAsesorUnicaRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.ObservacionItemAsesorAdmiteModificacionRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.ObservacionItemAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ModificarObservacionItemAsesorValidatorImpl implements ModificarObservacionItemAsesorValidator {

    private final ObservacionItemAsesorExisteRule observacionItemAsesorExisteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final ObservacionItemAsesorAdmiteModificacionRule observacionItemAsesorAdmiteModificacionRule;
    private final DescripcionObservacionItemAsesorUnicaRule descripcionUnicaRule;

    public ModificarObservacionItemAsesorValidatorImpl() {
        this.observacionItemAsesorExisteRule = new ObservacionItemAsesorExisteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.observacionItemAsesorAdmiteModificacionRule = new ObservacionItemAsesorAdmiteModificacionRuleImpl();
        this.descripcionUnicaRule = new DescripcionObservacionItemAsesorUnicaRuleImpl();
    }

    @Override
    public void validar(
            ModificacionObservacionItemAsesorDomain modificacion,
            ContextoEvaluacionCuantitativaAsesorDomain contexto,
            boolean descripcionEnOtraObservacion) {
        observacionItemAsesorExisteRule.validar(
                new ExistenciaObservacionItemAsesor(modificacion.getObservacion(), !contexto.esVacio()));
        evaluacionAsesorPropiedadAsesorRule.validar(
                new PropiedadEvaluacionAsesor(
                        contexto.getEvaluacionAsesor(), contexto.getAsesor(), modificacion.getAsesor()));
        observacionItemAsesorAdmiteModificacionRule.validar(
                new EstadoModificacionObservacionItemAsesor(contexto.getEvaluacionAsesor(), contexto.getEstado()));
        descripcionUnicaRule.validar(
                new DisponibilidadDescripcionObservacionItemAsesor(contexto.getId(), descripcionEnOtraObservacion));
    }
}
