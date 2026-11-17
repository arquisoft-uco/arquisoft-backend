package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.CambiarCriterioEvaluacionCualitativaAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoCambioCriterioEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.CriteriosCualitativosAsesorExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionCualitativaAsesorAdmiteCambioCriterioRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionCualitativaAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.CriteriosCualitativosAsesorExistentesRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionCualitativaAsesorAdmiteCambioCriterioRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionCualitativaAsesorExistenteRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarCriterioEvaluacionCualitativaAsesorValidatorImpl
        implements CambiarCriterioEvaluacionCualitativaAsesorValidator {

    private final EvaluacionCualitativaAsesorExistenteRule evaluacionCualitativaAsesorExistenteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final EvaluacionCualitativaAsesorAdmiteCambioCriterioRule evaluacionCualitativaAsesorAdmiteCambioCriterioRule;
    private final CriteriosCualitativosAsesorExistentesRule criteriosCualitativosAsesorExistentesRule;

    public CambiarCriterioEvaluacionCualitativaAsesorValidatorImpl() {
        this.evaluacionCualitativaAsesorExistenteRule = new EvaluacionCualitativaAsesorExistenteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.evaluacionCualitativaAsesorAdmiteCambioCriterioRule =
                new EvaluacionCualitativaAsesorAdmiteCambioCriterioRuleImpl();
        this.criteriosCualitativosAsesorExistentesRule = new CriteriosCualitativosAsesorExistentesRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacionCualitativaAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            EstadoCambioCriterioEvaluacionCualitativaAsesor estado,
            ExistenciaCriteriosCualitativosAsesor criterio) {
        evaluacionCualitativaAsesorExistenteRule.validar(existencia);
        evaluacionAsesorPropiedadAsesorRule.validar(propiedad);
        evaluacionCualitativaAsesorAdmiteCambioCriterioRule.validar(estado);
        criteriosCualitativosAsesorExistentesRule.validar(criterio);
    }
}
