package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.RegistrarEvaluacionesCuantitativasAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.DisponibilidadEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaItemsCuantitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajesVsValoresMaximosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionesCuantitativasAsesorUnicasRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.ItemsCuantitativosAsesorExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.PuntajesCuantitativosAsesorNoExcedenValorItemRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl.EvaluacionesCuantitativasAsesorUnicasRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl.ItemsCuantitativosAsesorExistentesRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl.PuntajesCuantitativosAsesorNoExcedenValorItemRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarEvaluacionesCuantitativasAsesorValidatorImpl
        implements RegistrarEvaluacionesCuantitativasAsesorValidator {

    private final EvaluacionAsesorExistenteRule evaluacionAsesorExistenteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final ItemsCuantitativosAsesorExistentesRule itemsCuantitativosAsesorExistentesRule;
    private final PuntajesCuantitativosAsesorNoExcedenValorItemRule puntajesNoExcedenValorItemRule;
    private final EvaluacionesCuantitativasAsesorUnicasRule evaluacionesCuantitativasAsesorUnicasRule;

    public RegistrarEvaluacionesCuantitativasAsesorValidatorImpl() {
        this.evaluacionAsesorExistenteRule = new EvaluacionAsesorExistenteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.itemsCuantitativosAsesorExistentesRule = new ItemsCuantitativosAsesorExistentesRuleImpl();
        this.puntajesNoExcedenValorItemRule = new PuntajesCuantitativosAsesorNoExcedenValorItemRuleImpl();
        this.evaluacionesCuantitativasAsesorUnicasRule = new EvaluacionesCuantitativasAsesorUnicasRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacionAsesor existencia,
            PropiedadEvaluacionAsesor propiedad,
            ExistenciaItemsCuantitativosAsesor items,
            PuntajesVsValoresMaximosAsesor puntajes,
            DisponibilidadEvaluacionesCuantitativasAsesor disponibilidad) {
        evaluacionAsesorExistenteRule.validar(existencia);
        evaluacionAsesorPropiedadAsesorRule.validar(propiedad);
        itemsCuantitativosAsesorExistentesRule.validar(items);
        puntajesNoExcedenValorItemRule.validar(puntajes);
        evaluacionesCuantitativasAsesorUnicasRule.validar(disponibilidad);
    }
}
