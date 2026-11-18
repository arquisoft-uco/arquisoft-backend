package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.RegistrarEvaluacionesCuantitativasJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.PropiedadEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoPropiedadJuradoRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoPropiedadJuradoRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.DisponibilidadEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaItemsCuantitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajesVsValoresMaximosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionesCuantitativasJuradoUnicasRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.ItemsCuantitativosJuradoExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.PuntajesCuantitativosJuradoNoExcedenValorItemRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionesCuantitativasJuradoUnicasRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.ItemsCuantitativosJuradoExistentesRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.PuntajesCuantitativosJuradoNoExcedenValorItemRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RegistrarEvaluacionesCuantitativasJuradoValidatorImpl
        implements RegistrarEvaluacionesCuantitativasJuradoValidator {

    private final EvaluacionJuradoExistenteRule evaluacionJuradoExistenteRule;
    private final EvaluacionJuradoPropiedadJuradoRule evaluacionJuradoPropiedadJuradoRule;
    private final ItemsCuantitativosJuradoExistentesRule itemsCuantitativosJuradoExistentesRule;
    private final PuntajesCuantitativosJuradoNoExcedenValorItemRule puntajesNoExcedenValorItemRule;
    private final EvaluacionesCuantitativasJuradoUnicasRule evaluacionesCuantitativasJuradoUnicasRule;

    public RegistrarEvaluacionesCuantitativasJuradoValidatorImpl() {
        this.evaluacionJuradoExistenteRule = new EvaluacionJuradoExistenteRuleImpl();
        this.evaluacionJuradoPropiedadJuradoRule = new EvaluacionJuradoPropiedadJuradoRuleImpl();
        this.itemsCuantitativosJuradoExistentesRule = new ItemsCuantitativosJuradoExistentesRuleImpl();
        this.puntajesNoExcedenValorItemRule = new PuntajesCuantitativosJuradoNoExcedenValorItemRuleImpl();
        this.evaluacionesCuantitativasJuradoUnicasRule = new EvaluacionesCuantitativasJuradoUnicasRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacionJurado existencia,
            PropiedadEvaluacionJurado propiedad,
            ExistenciaItemsCuantitativosJurado items,
            PuntajesVsValoresMaximosJurado puntajes,
            DisponibilidadEvaluacionesCuantitativasJurado disponibilidad) {
        evaluacionJuradoExistenteRule.validar(existencia);
        evaluacionJuradoPropiedadJuradoRule.validar(propiedad);
        itemsCuantitativosJuradoExistentesRule.validar(items);
        puntajesNoExcedenValorItemRule.validar(puntajes);
        evaluacionesCuantitativasJuradoUnicasRule.validar(disponibilidad);
    }
}
