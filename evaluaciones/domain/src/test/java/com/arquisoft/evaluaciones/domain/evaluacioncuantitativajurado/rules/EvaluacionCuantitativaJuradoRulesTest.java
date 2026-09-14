package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PropiedadEvaluacionCuantitativaJuradoJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.PuntajeVsValorMaximoItem;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionCuantitativaJuradoExistenteRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.EvaluacionJuradoFinalizadaRuleImpl;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl.PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRuleImpl;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluacionCuantitativaJuradoRulesTest {

    private static final UUID EVALUACION_CUANTITATIVA_JURADO = UUID.randomUUID();

    @Test
    void debeAceptar_cuandoEvaluacionExiste() {
        // Act & Assert
        assertThatCode(() -> new EvaluacionCuantitativaJuradoExistenteRuleImpl()
                .validar(new ExistenciaEvaluacionCuantitativaJurado(EVALUACION_CUANTITATIVA_JURADO, true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_cuandoEvaluacionNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> new EvaluacionCuantitativaJuradoExistenteRuleImpl()
                .validar(new ExistenciaEvaluacionCuantitativaJurado(EVALUACION_CUANTITATIVA_JURADO, false)))
                .isInstanceOfSatisfying(EvaluacionCuantitativaJuradoNoEncontradaException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(
                            EvaluacionesCodes.EvaluacionCuantitativaJurado.NO_ENCONTRADA);
                    assertThat(exception.getMessage()).contains(EVALUACION_CUANTITATIVA_JURADO.toString());
                });
    }

    @Test
    void debeAceptar_cuandoPerteneceAlJurado() {
        // Act & Assert
        assertThatCode(() -> new EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl()
                .validar(new PropiedadEvaluacionCuantitativaJuradoJurado(EVALUACION_CUANTITATIVA_JURADO, true)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_cuandoNoPerteneceAlJurado() {
        // Act & Assert
        assertThatThrownBy(() -> new EvaluacionCuantitativaJuradoPropiedadJuradoRuleImpl()
                .validar(new PropiedadEvaluacionCuantitativaJuradoJurado(EVALUACION_CUANTITATIVA_JURADO, false)))
                .isInstanceOfSatisfying(
                        EvaluacionCuantitativaJuradoNoPerteneceJuradoException.class, exception -> {
                            assertThat(exception.getCodigoError()).isEqualTo(
                                    EvaluacionesCodes.EvaluacionCuantitativaJurado.NO_PERTENECE_JURADO);
                            assertThat(exception.getMessage()).contains(EVALUACION_CUANTITATIVA_JURADO.toString());
                        });
    }

    @Test
    void debeAceptar_cuandoEvaluacionJuradoNoEstaFinalizada() {
        // Act & Assert
        assertThatCode(() -> new EvaluacionJuradoFinalizadaRuleImpl()
                .validar(new EstadoEvaluacionJurado(EVALUACION_CUANTITATIVA_JURADO, false)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_cuandoEvaluacionJuradoEstaFinalizada() {
        // Act & Assert
        assertThatThrownBy(() -> new EvaluacionJuradoFinalizadaRuleImpl()
                .validar(new EstadoEvaluacionJurado(EVALUACION_CUANTITATIVA_JURADO, true)))
                .isInstanceOfSatisfying(EvaluacionJuradoFinalizadaException.class, exception -> {
                    assertThat(exception.getCodigoError()).isEqualTo(
                            EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_FINALIZADA);
                    assertThat(exception.getMessage()).contains(EVALUACION_CUANTITATIVA_JURADO.toString());
                });
    }

    @Test
    void debeAceptar_cuandoPuntajeEsMenorQueValorMaximo() {
        // Act & Assert
        assertThatCode(() -> new PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRuleImpl()
                .validar(new PuntajeVsValorMaximoItem(300, 500)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeAceptar_cuandoPuntajeEsIgualAlValorMaximo() {
        // Act & Assert
        assertThatCode(() -> new PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRuleImpl()
                .validar(new PuntajeVsValorMaximoItem(500, 500)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_cuandoPuntajeExcedeValorMaximo() {
        // Act & Assert
        assertThatThrownBy(() -> new PuntajeEvaluacionCuantitativaJuradoNoExcedeValorItemRuleImpl()
                .validar(new PuntajeVsValorMaximoItem(501, 500)))
                .isInstanceOfSatisfying(
                        PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException.class, exception -> {
                            assertThat(exception.getCodigoError()).isEqualTo(
                                    EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_EXCEDE_VALOR_ITEM);
                            assertThat(exception.getMessage()).contains("501").contains("500");
                        });
    }
}
