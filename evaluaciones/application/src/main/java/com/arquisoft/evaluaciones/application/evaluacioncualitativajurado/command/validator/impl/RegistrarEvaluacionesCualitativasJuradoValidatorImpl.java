package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.RegistrarEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaItemsCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.CriteriosCualitativosJuradoExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoPropiedadJuradoRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionesCualitativasJuradoUnicasRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.ItemsCualitativosJuradoExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.CriteriosCualitativosJuradoExistentesRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoPropiedadJuradoRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionesCualitativasJuradoUnicasRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.ItemsCualitativosJuradoExistentesRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarEvaluacionesCualitativasJuradoValidatorImpl
        implements RegistrarEvaluacionesCualitativasJuradoValidator {

    private final EvaluacionJuradoExistenteRule evaluacionJuradoExistenteRule;
    private final EvaluacionJuradoPropiedadJuradoRule evaluacionJuradoPropiedadJuradoRule;
    private final ItemsCualitativosJuradoExistentesRule itemsCualitativosJuradoExistentesRule;
    private final CriteriosCualitativosJuradoExistentesRule criteriosCualitativosJuradoExistentesRule;
    private final EvaluacionesCualitativasJuradoUnicasRule evaluacionesCualitativasJuradoUnicasRule;

    public RegistrarEvaluacionesCualitativasJuradoValidatorImpl() {
        this.evaluacionJuradoExistenteRule = new EvaluacionJuradoExistenteRuleImpl();
        this.evaluacionJuradoPropiedadJuradoRule = new EvaluacionJuradoPropiedadJuradoRuleImpl();
        this.itemsCualitativosJuradoExistentesRule = new ItemsCualitativosJuradoExistentesRuleImpl();
        this.criteriosCualitativosJuradoExistentesRule = new CriteriosCualitativosJuradoExistentesRuleImpl();
        this.evaluacionesCualitativasJuradoUnicasRule = new EvaluacionesCualitativasJuradoUnicasRuleImpl();
    }

    @Override
    public void validarAcceso(ExistenciaEvaluacionJurado existencia, PropiedadEvaluacionJurado propiedad) {
        evaluacionJuradoExistenteRule.validar(existencia);
        evaluacionJuradoPropiedadJuradoRule.validar(propiedad);
    }

    @Override
    public void validarContenido(
            ExistenciaItemsCualitativosJurado items,
            ExistenciaCriteriosCualitativosJurado criterios,
            DisponibilidadEvaluacionesCualitativasJurado disponibilidad) {
        itemsCualitativosJuradoExistentesRule.validar(items);
        criteriosCualitativosJuradoExistentesRule.validar(criterios);
        evaluacionesCualitativasJuradoUnicasRule.validar(disponibilidad);
    }
}
