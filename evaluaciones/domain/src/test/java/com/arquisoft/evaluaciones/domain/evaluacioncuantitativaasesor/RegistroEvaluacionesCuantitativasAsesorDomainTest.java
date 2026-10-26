package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistroEvaluacionesCuantitativasAsesorDomainTest {

    @Test
    void debeCrear_cuandoLoteValido() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluaciones = List.of(
                EvaluacionCuantitativaAsesorDomain.crear(evaluacionAsesor, UtilUUID.generarNuevoUUID(), 10),
                EvaluacionCuantitativaAsesorDomain.crear(evaluacionAsesor, UtilUUID.generarNuevoUUID(), 20));

        // Act
        var registro = RegistroEvaluacionesCuantitativasAsesorDomain.crear(evaluacionAsesor, asesor, evaluaciones);

        // Assert
        assertThat(registro.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(registro.getAsesor()).isEqualTo(asesor);
        assertThat(registro.getEvaluaciones()).hasSize(2);
    }

    @Test
    void debeRechazar_cuandoLoteVacio() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluaciones = List.<EvaluacionCuantitativaAsesorDomain>of();

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCuantitativasAsesorDomain.crear(
                evaluacionAsesor, asesor, evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCuantitativasAsesor.LOTE_VACIO));
    }

    @Test
    void debeRechazar_cuandoItemsRepetidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var itemRepetido = UtilUUID.generarNuevoUUID();
        var evaluaciones = List.of(
                EvaluacionCuantitativaAsesorDomain.crear(evaluacionAsesor, itemRepetido, 10),
                EvaluacionCuantitativaAsesorDomain.crear(evaluacionAsesor, itemRepetido, 20));

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCuantitativasAsesorDomain.crear(
                evaluacionAsesor, asesor, evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCuantitativasAsesor.ITEMS_REPETIDOS));
    }

    @Test
    void debeAcumularErrores_cuandoAsesorEvaluacionYLoteSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCuantitativasAsesorDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCuantitativaAsesor.ASESOR,
                                                EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.RegistroEvaluacionesCuantitativasAsesor.EVALUACIONES,
                                                EvaluacionesCodes.RegistroEvaluacionesCuantitativasAsesor.LOTE_VACIO)));
    }
}
