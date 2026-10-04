package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.OmitirEvaluacionesCuantitativasJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl.EvaluacionJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoOmisionEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ObservacionesEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionJuradoAdmiteOmisionRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionesCuantitativasJuradoDeEvaluacionRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionesCuantitativasJuradoSinObservacionesRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionJuradoAdmiteOmisionRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionesCuantitativasJuradoDeEvaluacionRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionesCuantitativasJuradoSinObservacionesRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class OmitirEvaluacionesCuantitativasJuradoValidatorImpl
        implements OmitirEvaluacionesCuantitativasJuradoValidator {

    private final EvaluacionJuradoExistenteRule evaluacionJuradoExistenteRule;
    private final EvaluacionesCuantitativasJuradoDeEvaluacionRule evaluacionesCuantitativasJuradoDeEvaluacionRule;
    private final EvaluacionJuradoAdmiteOmisionRule evaluacionJuradoAdmiteOmisionRule;
    private final EvaluacionesCuantitativasJuradoSinObservacionesRule evaluacionesCuantitativasJuradoSinObservacionesRule;

    public OmitirEvaluacionesCuantitativasJuradoValidatorImpl() {
        this.evaluacionJuradoExistenteRule = new EvaluacionJuradoExistenteRuleImpl();
        this.evaluacionesCuantitativasJuradoDeEvaluacionRule = new EvaluacionesCuantitativasJuradoDeEvaluacionRuleImpl();
        this.evaluacionJuradoAdmiteOmisionRule = new EvaluacionJuradoAdmiteOmisionRuleImpl();
        this.evaluacionesCuantitativasJuradoSinObservacionesRule =
                new EvaluacionesCuantitativasJuradoSinObservacionesRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacionJurado evaluacionJurado,
            ExistenciaEvaluacionesCuantitativasJurado evaluaciones,
            EstadoOmisionEvaluacionesCuantitativasJurado estado,
            ObservacionesEvaluacionesCuantitativasJurado observaciones) {
        evaluacionJuradoExistenteRule.validar(evaluacionJurado);
        evaluacionesCuantitativasJuradoDeEvaluacionRule.validar(evaluaciones);
        evaluacionJuradoAdmiteOmisionRule.validar(estado);
        evaluacionesCuantitativasJuradoSinObservacionesRule.validar(observaciones);
    }
}
