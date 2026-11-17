package com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.RegistrarObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionAdmiteRegistroRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionAdmiteRegistroRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RegistroObservacionItemAsesorDomain;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.DisponibilidadDescripcionObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.DescripcionObservacionItemAsesorUnicaRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.EvaluacionCuantitativaAsesorExisteRule;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.DescripcionObservacionItemAsesorUnicaRuleImpl;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl.EvaluacionCuantitativaAsesorExisteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarObservacionItemAsesorValidatorImpl implements RegistrarObservacionItemAsesorValidator {

    private final EvaluacionCuantitativaAsesorExisteRule evaluacionCuantitativaAsesorExisteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final EvaluacionAdmiteRegistroRule evaluacionAdmiteRegistroRule;
    private final DescripcionObservacionItemAsesorUnicaRule descripcionUnicaRule;

    public RegistrarObservacionItemAsesorValidatorImpl() {
        this.evaluacionCuantitativaAsesorExisteRule = new EvaluacionCuantitativaAsesorExisteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.evaluacionAdmiteRegistroRule = new EvaluacionAdmiteRegistroRuleImpl();
        this.descripcionUnicaRule = new DescripcionObservacionItemAsesorUnicaRuleImpl();
    }

    @Override
    public void validar(
            RegistroObservacionItemAsesorDomain registro,
            ContextoEvaluacionCuantitativaAsesorDomain contexto,
            boolean descripcionYaExiste) {
        var evaluacionCuantitativaAsesor = registro.getObservacion().getEvaluacionCuantitativaAsesor();

        evaluacionCuantitativaAsesorExisteRule.validar(
                new ExistenciaEvaluacionCuantitativaAsesor(evaluacionCuantitativaAsesor, !contexto.esVacio()));
        evaluacionAsesorPropiedadAsesorRule.validar(
                new PropiedadEvaluacionAsesor(
                        contexto.getEvaluacionAsesor(), contexto.getAsesor(), registro.getAsesor()));
        evaluacionAdmiteRegistroRule.validar(new EstadoRegistroEvaluacion(contexto.getEstado()));
        descripcionUnicaRule.validar(
                new DisponibilidadDescripcionObservacionItemAsesor(evaluacionCuantitativaAsesor, descripcionYaExiste));
    }
}
