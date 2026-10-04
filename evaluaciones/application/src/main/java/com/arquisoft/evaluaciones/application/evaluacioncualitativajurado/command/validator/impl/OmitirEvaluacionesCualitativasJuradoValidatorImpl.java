package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.OmitirEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoOmisionEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoAdmiteOmisionRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionesCualitativasJuradoDeEvaluacionRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoAdmiteOmisionRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionesCualitativasJuradoDeEvaluacionRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class OmitirEvaluacionesCualitativasJuradoValidatorImpl
        implements OmitirEvaluacionesCualitativasJuradoValidator {

    private final EvaluacionJuradoExistenteRule evaluacionJuradoExistenteRule;
    private final EvaluacionesCualitativasJuradoDeEvaluacionRule evaluacionesCualitativasJuradoDeEvaluacionRule;
    private final EvaluacionJuradoAdmiteOmisionRule evaluacionJuradoAdmiteOmisionRule;

    public OmitirEvaluacionesCualitativasJuradoValidatorImpl() {
        this.evaluacionJuradoExistenteRule = new EvaluacionJuradoExistenteRuleImpl();
        this.evaluacionesCualitativasJuradoDeEvaluacionRule = new EvaluacionesCualitativasJuradoDeEvaluacionRuleImpl();
        this.evaluacionJuradoAdmiteOmisionRule = new EvaluacionJuradoAdmiteOmisionRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacionJurado evaluacionJurado,
            ExistenciaEvaluacionesCualitativasJurado evaluaciones,
            EstadoOmisionEvaluacionesCualitativasJurado estado) {
        evaluacionJuradoExistenteRule.validar(evaluacionJurado);
        evaluacionesCualitativasJuradoDeEvaluacionRule.validar(evaluaciones);
        evaluacionJuradoAdmiteOmisionRule.validar(estado);
    }
}
