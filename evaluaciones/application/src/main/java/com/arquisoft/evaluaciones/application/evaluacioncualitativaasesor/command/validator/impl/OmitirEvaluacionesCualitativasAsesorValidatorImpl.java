package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.OmitirEvaluacionesCualitativasAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoOmisionEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionesCualitativasAsesorRegistradas;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionCualitativaAsesorAdmiteOmisionRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionesCualitativasAsesorRegistradasRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionCualitativaAsesorAdmiteOmisionRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionesCualitativasAsesorRegistradasRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class OmitirEvaluacionesCualitativasAsesorValidatorImpl
        implements OmitirEvaluacionesCualitativasAsesorValidator {

    private final EvaluacionAsesorExistenteRule evaluacionAsesorExistenteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final EvaluacionCualitativaAsesorAdmiteOmisionRule evaluacionCualitativaAsesorAdmiteOmisionRule;
    private final EvaluacionesCualitativasAsesorRegistradasRule evaluacionesCualitativasAsesorRegistradasRule;

    public OmitirEvaluacionesCualitativasAsesorValidatorImpl() {
        this.evaluacionAsesorExistenteRule = new EvaluacionAsesorExistenteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.evaluacionCualitativaAsesorAdmiteOmisionRule = new EvaluacionCualitativaAsesorAdmiteOmisionRuleImpl();
        this.evaluacionesCualitativasAsesorRegistradasRule = new EvaluacionesCualitativasAsesorRegistradasRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacionAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            EstadoOmisionEvaluacionesCualitativasAsesor estado,
            ExistenciaEvaluacionesCualitativasAsesorRegistradas registradas) {
        evaluacionAsesorExistenteRule.validar(existencia);
        evaluacionAsesorPropiedadAsesorRule.validar(propiedad);
        evaluacionCualitativaAsesorAdmiteOmisionRule.validar(estado);
        evaluacionesCualitativasAsesorRegistradasRule.validar(registradas);
    }
}
