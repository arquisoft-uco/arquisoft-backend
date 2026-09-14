package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.CambiarPuntajeEvaluacionCuantitativaJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PropiedadEvaluacionCuantitativaJuradoJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajeVsValorMaximoItem;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionCuantitativaJuradoExistenteRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionCuantitativaJuradoPropiedadJuradoRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionJuradoFinalizadaRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRule;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionCuantitativaJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionJuradoFinalizadaRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImpl
        implements CambiarPuntajeEvaluacionCuantitativaJuradoValidator {

    private final EvaluacionCuantitativaJuradoExistenteRule evaluacionCuantitativaJuradoExistenteRule;
    private final EvaluacionCuantitativaJuradoPropiedadJuradoRule evaluacionCuantitativaJuradoPropiedadJuradoRule;
    private final EvaluacionJuradoFinalizadaRule evaluacionJuradoFinalizadaRule;
    private final PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRule puntajeNoExcedeValorItemRule;

    public CambiarPuntajeEvaluacionCuantitativaJuradoValidatorImpl() {
        this.evaluacionCuantitativaJuradoExistenteRule = new EvaluacionCuantitativaJuradoExistenteRuleImpl();
        this.evaluacionCuantitativaJuradoPropiedadJuradoRule = new EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl();
        this.evaluacionJuradoFinalizadaRule = new EvaluacionJuradoFinalizadaRuleImpl();
        this.puntajeNoExcedeValorItemRule = new PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRuleImpl();
    }

    @Override
    public void validar(
            CambioPuntajeEvaluacionCuantitativaJuradoDomain cambio,
            EvaluacionCuantitativaJuradoDomain evaluacion,
            EstadoEvaluacionJuradoEntity estado,
            Integer valorMaximoItem) {
        evaluacionCuantitativaJuradoExistenteRule.validar(
                new ExistenciaEvaluacionCuantitativaJurado(
                        cambio.getEvaluacionCuantitativaJurado(), !evaluacion.esVacio()));
        evaluacionCuantitativaJuradoPropiedadJuradoRule.validar(
                new PropiedadEvaluacionCuantitativaJuradoJurado(
                        cambio.getEvaluacionCuantitativaJurado(), estado.pertenece()));
        evaluacionJuradoFinalizadaRule.validar(
                new EstadoEvaluacionJurado(cambio.getEvaluacionCuantitativaJurado(), estado.finalizada()));
        puntajeNoExcedeValorItemRule.validar(
                new PuntajeVsValorMaximoItem(cambio.getNuevoPuntaje(), valorMaximoItem));
    }
}
