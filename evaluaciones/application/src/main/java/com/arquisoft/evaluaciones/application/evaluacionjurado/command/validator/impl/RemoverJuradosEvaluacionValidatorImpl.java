package com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.RemoverJuradosEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionAdmiteCambioAsignacionRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionAdmiteCambioAsignacionRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.EvaluacionesRegistradasJurados;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosAsignadosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosAsignadosRule;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.JuradosSinEvaluacionesRule;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl.JuradosAsignadosRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.rules.impl.JuradosSinEvaluacionesRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class RemoverJuradosEvaluacionValidatorImpl implements RemoverJuradosEvaluacionValidator {

    private final EvaluacionExistenteRule evaluacionExistenteRule;
    private final EvaluacionAdmiteCambioAsignacionRule evaluacionAdmiteCambioAsignacionRule;
    private final JuradosAsignadosRule juradosAsignadosRule;
    private final JuradosSinEvaluacionesRule juradosSinEvaluacionesRule;

    public RemoverJuradosEvaluacionValidatorImpl() {
        this.evaluacionExistenteRule = new EvaluacionExistenteRuleImpl();
        this.evaluacionAdmiteCambioAsignacionRule = new EvaluacionAdmiteCambioAsignacionRuleImpl();
        this.juradosAsignadosRule = new JuradosAsignadosRuleImpl();
        this.juradosSinEvaluacionesRule = new JuradosSinEvaluacionesRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacion evaluacion,
            EstadoAsignacionEvaluacion estado,
            JuradosAsignadosEvaluacion jurados,
            EvaluacionesRegistradasJurados evaluaciones) {
        evaluacionExistenteRule.validar(evaluacion);
        evaluacionAdmiteCambioAsignacionRule.validar(estado);
        juradosAsignadosRule.validar(jurados);
        juradosSinEvaluacionesRule.validar(evaluaciones);
    }
}
