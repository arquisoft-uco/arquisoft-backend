package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistroEvaluacionesCualitativasAsesorDomainTest {

    @Test
    void debeCrear_cuandoLoteValido() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var evaluaciones = List.of(
                EvaluacionCualitativaAsesorDomain.crear(
                        evaluacionAsesor, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()),
                EvaluacionCualitativaAsesorDomain.crear(
                        evaluacionAsesor, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));

        // Act
        var registro = RegistroEvaluacionesCualitativasAsesorDomain.crear(evaluacionAsesor, asesor, evaluaciones);

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
        var evaluaciones = List.<EvaluacionCualitativaAsesorDomain>of();

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasAsesorDomain.crear(
                evaluacionAsesor, asesor, evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasAsesor.LOTE_VACIO));
    }

    @Test
    void debeRechazar_cuandoItemsRepetidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var itemRepetido = UtilUUID.generarNuevoUUID();
        var evaluaciones = List.of(
                EvaluacionCualitativaAsesorDomain.crear(evaluacionAsesor, itemRepetido, UtilUUID.generarNuevoUUID()),
                EvaluacionCualitativaAsesorDomain.crear(evaluacionAsesor, itemRepetido, UtilUUID.generarNuevoUUID()));

        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasAsesorDomain.crear(
                evaluacionAsesor, asesor, evaluaciones))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasAsesor.ITEMS_REPETIDOS));
    }

    @Test
    void debeAcumularErrores_cuandoAsesorEvaluacionYLoteSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RegistroEvaluacionesCualitativasAsesorDomain.crear(null, null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.RegistroEvaluacionesCualitativasAsesor.EVALUACIONES,
                                                EvaluacionesCodes.RegistroEvaluacionesCualitativasAsesor.LOTE_VACIO)));
    }
}
