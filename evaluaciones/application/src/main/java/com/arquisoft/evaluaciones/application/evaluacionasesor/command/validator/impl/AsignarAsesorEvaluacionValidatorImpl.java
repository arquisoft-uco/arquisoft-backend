package com.arquisoft.evaluaciones.application.evaluacionasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.validator.AsignarAsesorEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.evaluaciones.domain.asesor.rules.AsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.asesor.rules.impl.AsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionAdmiteCambioAsignacionRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.EvaluacionExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionAdmiteCambioAsignacionRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacion.rules.impl.EvaluacionExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.CambioAsesorEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.EvaluacionAsesorSinRegistrosRule;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl.EvaluacionAsesorSinRegistrosRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class AsignarAsesorEvaluacionValidatorImpl implements AsignarAsesorEvaluacionValidator {

    private final EvaluacionExistenteRule evaluacionExistenteRule;
    private final AsesorExistenteRule asesorExistenteRule;
    private final EvaluacionAdmiteCambioAsignacionRule evaluacionAdmiteCambioAsignacionRule;
    private final EvaluacionAsesorSinRegistrosRule evaluacionAsesorSinRegistrosRule;

    public AsignarAsesorEvaluacionValidatorImpl() {
        this.evaluacionExistenteRule = new EvaluacionExistenteRuleImpl();
        this.asesorExistenteRule = new AsesorExistenteRuleImpl();
        this.evaluacionAdmiteCambioAsignacionRule = new EvaluacionAdmiteCambioAsignacionRuleImpl();
        this.evaluacionAsesorSinRegistrosRule = new EvaluacionAsesorSinRegistrosRuleImpl();
    }

    @Override
    public void validar(
            ExistenciaEvaluacion evaluacion,
            ExistenciaAsesor asesor,
            EstadoAsignacionEvaluacion estado,
            CambioAsesorEvaluacion cambio) {
        evaluacionExistenteRule.validar(evaluacion);
        asesorExistenteRule.validar(asesor);
        evaluacionAdmiteCambioAsignacionRule.validar(estado);
        evaluacionAsesorSinRegistrosRule.validar(cambio);
    }
}
