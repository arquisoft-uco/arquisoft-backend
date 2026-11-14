package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistroEvaluacionesCuantitativasJuradoDomainTest {

    @Test
    void debeCrearRegistro_cuandoDatosValidos() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var jurado = UtilUUID.generarNuevoUUID();
        var evaluaciones = List.of(
                EvaluacionCuantitativaJuradoDomain.crear(evaluacionJurado, UtilUUID.generarNuevoUUID(), 100),
                EvaluacionCuantitativaJuradoDomain.crear(evaluacionJurado, UtilUUID.generarNuevoUUID(), 200));

        // Act
        var registro = RegistroEvaluacionesCuantitativasJuradoDomain.crear(evaluacionJurado, jurado, evaluaciones);

        // Assert
        assertThat(registro.getEvaluacionJurado()).isEqualTo(evaluacionJurado);
        assertThat(registro.getJurado()).isEqualTo(jurado);
        assertThat(registro.getEvaluaciones()).hasSize(2);
    }

    @Test
    void debeAcumularFieldErrors_cuandoEvaluacionJuradoJuradoYLoteInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCuantitativasJuradoDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.EVALUACION_JURADO,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.EVALUACION_JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaJurado.JURADO,
                                                EvaluacionesCodes.EvaluacionCuantitativaJurado.JURADO_REQUERIDO),
                                        tuple(EvaluacionesFields.RegistroEvaluacionesCuantitativasJurado.EVALUACIONES,
                                                EvaluacionesCodes.RegistroEvaluacionesCuantitativasJurado.LOTE_VACIO)));
    }

    @Test
    void debeRechazarLoteVacio_cuandoListaEstaVacia() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCuantitativasJuradoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), List.of()))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCuantitativasJurado.LOTE_VACIO));
    }

    @Test
    void debeRechazarItemsRepetidos_cuandoDosEvaluacionesComparteItem() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var itemRepetido = UtilUUID.generarNuevoUUID();
        var evaluaciones = List.of(
                EvaluacionCuantitativaJuradoDomain.crear(evaluacionJurado, itemRepetido, 100),
                EvaluacionCuantitativaJuradoDomain.crear(evaluacionJurado, itemRepetido, 200));
        var jurado = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCuantitativasJuradoDomain.crear(
                evaluacionJurado, jurado, evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCuantitativasJurado.ITEMS_REPETIDOS));
    }
}
