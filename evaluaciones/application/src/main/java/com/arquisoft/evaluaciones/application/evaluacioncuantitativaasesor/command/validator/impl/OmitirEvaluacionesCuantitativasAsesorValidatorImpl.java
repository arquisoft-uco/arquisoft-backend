package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.OmitirEvaluacionesCuantitativasAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.EstadoOmisionEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaEvaluacionesCuantitativasAsesorRegistradas;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionCuantitativaAsesorAdmiteOmisionRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionesCuantitativasAsesorRegistradasRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl.EvaluacionCuantitativaAsesorAdmiteOmisionRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl.EvaluacionesCuantitativasAsesorRegistradasRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class OmitirEvaluacionesCuantitativasAsesorValidatorImpl
        implements OmitirEvaluacionesCuantitativasAsesorValidator {

    private final EvaluacionAsesorExistenteRule evaluacionAsesorExistenteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final EvaluacionCuantitativaAsesorAdmiteOmisionRule evaluacionCuantitativaAsesorAdmiteOmisionRule;
    private final EvaluacionesCuantitativasAsesorRegistradasRule evaluacionesCuantitativasAsesorRegistradasRule;

    public OmitirEvaluacionesCuantitativasAsesorValidatorImpl() {
        this.evaluacionAsesorExistenteRule = new EvaluacionAsesorExistenteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.evaluacionCuantitativaAsesorAdmiteOmisionRule = new EvaluacionCuantitativaAsesorAdmiteOmisionRuleImpl();
        this.evaluacionesCuantitativasAsesorRegistradasRule = new EvaluacionesCuantitativasAsesorRegistradasRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacionAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            EstadoOmisionEvaluacionesCuantitativasAsesor estado,
            ExistenciaEvaluacionesCuantitativasAsesorRegistradas registradas) {
        evaluacionAsesorExistenteRule.validar(existencia);
        evaluacionAsesorPropiedadAsesorRule.validar(propiedad);
        evaluacionCuantitativaAsesorAdmiteOmisionRule.validar(estado);
        evaluacionesCuantitativasAsesorRegistradasRule.validar(registradas);
    }
}
