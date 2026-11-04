package com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.AsignarJuradosEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionAdmiteRegistroRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionAdmiteRegistroRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.DisponibilidadJuradosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.ExistenciaJurados;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosExistentesRule;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosSinAsignarRule;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl.JuradosExistentesRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl.JuradosSinAsignarRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AsignarJuradosEvaluacionValidatorImpl implements AsignarJuradosEvaluacionValidator {

    private final EvaluacionExistenteRule evaluacionExistenteRule;
    private final EvaluacionAdmiteRegistroRule evaluacionAdmiteRegistroRule;
    private final JuradosExistentesRule juradosExistentesRule;
    private final JuradosSinAsignarRule juradosSinAsignarRule;

    public AsignarJuradosEvaluacionValidatorImpl() {
        this.evaluacionExistenteRule = new EvaluacionExistenteRuleImpl();
        this.evaluacionAdmiteRegistroRule = new EvaluacionAdmiteRegistroRuleImpl();
        this.juradosExistentesRule = new JuradosExistentesRuleImpl();
        this.juradosSinAsignarRule = new JuradosSinAsignarRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacion evaluacion,
            EstadoRegistroEvaluacion estado,
            ExistenciaJurados jurados,
            DisponibilidadJuradosEvaluacion disponibilidad) {
        evaluacionExistenteRule.validar(evaluacion);
        evaluacionAdmiteRegistroRule.validar(estado);
        juradosExistentesRule.validar(jurados);
        juradosSinAsignarRule.validar(disponibilidad);
    }
}
