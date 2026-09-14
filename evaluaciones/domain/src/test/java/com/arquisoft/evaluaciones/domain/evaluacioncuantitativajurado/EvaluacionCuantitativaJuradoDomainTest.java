package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class EvaluacionCuantitativaJuradoDomainTest {

    @Test
    void debeCrearEvaluacionCuantitativa_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var item = UUID.randomUUID();
        var puntaje = 350;

        // Act
        var evaluacion = EvaluacionCuantitativaJuradoDomain.crear(evaluacionJurado, item, puntaje);

        // Assert
        assertThat(evaluacion.getId()).isNotNull();
        assertThat(evaluacion.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(evaluacion.getItem()).isEqualTo(item);
        assertThat(evaluacion.getPuntaje()).isEqualTo(puntaje);
        assertThat(evaluacion.esVacio()).isFalse();
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosTresCamposSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionCuantitativaJuradoDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.ITEM,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.ITEM_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.PUNTAJE,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_REQUERIDO)));
    }

    @Test
    void debeAcumularErrorDePuntajeFueraDeRango_cuandoPuntajeExcedeElMaximo() {
        // Act & Assert
        assertThatThrownBy(() -> EvaluacionCuantitativaJuradoDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), 501))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.EvaluacionCuantitativaJurado.PUNTAJE_FUERA_DE_RANGO));
    }

    @Test
    void debeAceptarLosLimitesDelRango_cuandoPuntajeEsCeroOQuinientos() {
        // Act
        var conMinimo = EvaluacionCuantitativaJuradoDomain.crear(UUID.randomUUID(), UUID.randomUUID(), 0);
        var conMaximo = EvaluacionCuantitativaJuradoDomain.crear(UUID.randomUUID(), UUID.randomUUID(), 500);

        // Assert
        assertThat(conMinimo.getPuntaje()).isEqualTo(0);
        assertThat(conMaximo.getPuntaje()).isEqualTo(500);
    }

    @Test
    void debeReconstruirSinValidar_cuandoSeCargaDesdePersistencia() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var evaluacion = EvaluacionCuantitativaJuradoDomain.reconstruir(id, null, null, null);

        // Assert
        assertThat(evaluacion.getId()).isEqualTo(id);
        assertThat(evaluacion.getEvaluacionJurado()).isNull();
        assertThat(evaluacion.getItem()).isNull();
        assertThat(evaluacion.getPuntaje()).isNull();
    }

    @Test
    void debeIdentificarseComoVacio_cuandoEsLaConstanteVacio() {
        // Act & Assert
        assertThat(EvaluacionCuantitativaJuradoDomain.VACIO.esVacio()).isTrue();
        assertThat(EvaluacionCuantitativaJuradoDomain.crear(
                UUID.randomUUID(), UUID.randomUUID(), 100).esVacio()).isFalse();
    }
}
