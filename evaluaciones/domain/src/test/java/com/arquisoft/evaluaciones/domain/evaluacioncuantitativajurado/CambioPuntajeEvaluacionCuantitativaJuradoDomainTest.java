package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioPuntajeEvaluacionCuantitativaJuradoDomainTest {

    private static final UUID EVALUACION_CUANTITATIVA_JURADO = UUID.randomUUID();
    private static final UUID JURADO = UUID.randomUUID();

    @Test
    void debeCrearCambio_cuandoDatosValidos() {
        // Act
        var cambio = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                EVALUACION_CUANTITATIVA_JURADO, JURADO, 350);

        // Assert
        assertThat(cambio.getEvaluacionCuantitativaJurado()).isEqualTo(EVALUACION_CUANTITATIVA_JURADO);
        assertThat(cambio.getJurado()).isEqualTo(JURADO);
        assertThat(cambio.getNuevoPuntaje()).isEqualTo(350);
    }

    @Test
    void debeAcumularErrores_cuandoTodosLosCamposSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.ID_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO));
    }

    @Test
    void debeAcumularErrorPuntajeFueraDeRango_cuandoPuntajeEsMenorQueElMinimo() {
        // Act & Assert
        assertThatThrownBy(() -> CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                EVALUACION_CUANTITATIVA_JURADO, JURADO, -1))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO));
    }

    @Test
    void debeAcumularErrorPuntajeFueraDeRango_cuandoPuntajeEsMayorQueElMaximo() {
        // Act & Assert
        assertThatThrownBy(() -> CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                EVALUACION_CUANTITATIVA_JURADO, JURADO, 501))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO));
    }

    @Test
    void debeAceptarValoresLimite_cuandoPuntajeEsCeroOQuinientos() {
        // Act
        var minimo = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                EVALUACION_CUANTITATIVA_JURADO, JURADO, 0);
        var maximo = CambioPuntajeEvaluacionCuantitativaJuradoDomain.crear(
                EVALUACION_CUANTITATIVA_JURADO, JURADO, 500);

        // Assert
        assertThat(minimo.getNuevoPuntaje()).isZero();
        assertThat(maximo.getNuevoPuntaje()).isEqualTo(500);
    }
}
