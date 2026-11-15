package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.CambiarPuntajeEvaluacionCuantitativaAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorPropiedadAsesorRule;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl.EvaluacionAsesorPropiedadAsesorRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.CambioPuntajeEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.ContextoEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.EstadoCambioPuntajeEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaEvaluacionCuantitativaAsesorPorId;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.PuntajeVsValorMaximoItemAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionCuantitativaAsesorExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl.EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl.EvaluacionCuantitativaAsesorExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl.PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarPuntajeEvaluacionCuantitativaAsesorValidatorImpl
        implements CambiarPuntajeEvaluacionCuantitativaAsesorValidator {

    private final EvaluacionCuantitativaAsesorExistenteRule evaluacionCuantitativaAsesorExistenteRule;
    private final EvaluacionAsesorPropiedadAsesorRule evaluacionAsesorPropiedadAsesorRule;
    private final EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRule evaluacionCuantitativaAsesorAdmiteCambioPuntajeRule;
    private final PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRule puntajeNoExcedeValorItemRule;

    public CambiarPuntajeEvaluacionCuantitativaAsesorValidatorImpl() {
        this.evaluacionCuantitativaAsesorExistenteRule = new EvaluacionCuantitativaAsesorExistenteRuleImpl();
        this.evaluacionAsesorPropiedadAsesorRule = new EvaluacionAsesorPropiedadAsesorRuleImpl();
        this.evaluacionCuantitativaAsesorAdmiteCambioPuntajeRule =
                new EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRuleImpl();
        this.puntajeNoExcedeValorItemRule = new PuntajeEvaluacionCuantitativaAsesorNoExcedeValorItemRuleImpl();
    }

    @Override
    public void validar(
            CambioPuntajeEvaluacionCuantitativaAsesorDomain cambio,
            ContextoEvaluacionCuantitativaAsesorDomain contexto,
            ItemCuantitativoAsesorDomain item) {
        evaluacionCuantitativaAsesorExistenteRule.validar(
                new ExistenciaEvaluacionCuantitativaAsesorPorId(
                        cambio.getEvaluacionCuantitativaAsesor(), !contexto.esVacio() && !item.esVacio()));
        evaluacionAsesorPropiedadAsesorRule.validar(
                new PropiedadEvaluacionAsesor(
                        contexto.getEvaluacionAsesor(), contexto.getAsesor(), cambio.getAsesor()));
        evaluacionCuantitativaAsesorAdmiteCambioPuntajeRule.validar(
                new EstadoCambioPuntajeEvaluacionCuantitativaAsesor(
                        contexto.getEvaluacionAsesor(), contexto.getEstado()));
        puntajeNoExcedeValorItemRule.validar(
                new PuntajeVsValorMaximoItemAsesor(cambio.getNuevoPuntaje(), item.getValor(), item.getNombre()));
    }
}
