package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.RegistrarEvaluacionesCualitativasAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.DisponibilidadEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaItemsCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.CriteriosCualitativosAsesorExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionesCualitativasAsesorUnicasRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.ItemsCualitativosAsesorExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.CriteriosCualitativosAsesorExistentesRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionesCualitativasAsesorUnicasRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.ItemsCualitativosAsesorExistentesRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarEvaluacionesCualitativasAsesorValidatorImpl
        implements RegistrarEvaluacionesCualitativasAsesorValidator {

    private final EvaluacionAsesorExistenteRule evaluacionAsesorExistenteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final ItemsCualitativosAsesorExistentesRule itemsCualitativosAsesorExistentesRule;
    private final CriteriosCualitativosAsesorExistentesRule criteriosCualitativosAsesorExistentesRule;
    private final EvaluacionesCualitativasAsesorUnicasRule evaluacionesCualitativasAsesorUnicasRule;

    public RegistrarEvaluacionesCualitativasAsesorValidatorImpl() {
        this.evaluacionAsesorExistenteRule = new EvaluacionAsesorExistenteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.itemsCualitativosAsesorExistentesRule = new ItemsCualitativosAsesorExistentesRuleImpl();
        this.criteriosCualitativosAsesorExistentesRule = new CriteriosCualitativosAsesorExistentesRuleImpl();
        this.evaluacionesCualitativasAsesorUnicasRule = new EvaluacionesCualitativasAsesorUnicasRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacionAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            ExistenciaItemsCualitativosAsesor items,
            ExistenciaCriteriosCualitativosAsesor criterios,
            DisponibilidadEvaluacionesCualitativasAsesor disponibilidad) {
        evaluacionAsesorExistenteRule.validar(existencia);
        evaluacionAsesorPropiedadAsesorRule.validar(propiedad);
        itemsCualitativosAsesorExistentesRule.validar(items);
        criteriosCualitativosAsesorExistentesRule.validar(criterios);
        evaluacionesCualitativasAsesorUnicasRule.validar(disponibilidad);
    }
}
