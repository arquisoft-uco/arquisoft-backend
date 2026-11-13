package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmisionEvaluacionesCualitativasAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();

        // Act
        var omision = OmisionEvaluacionesCualitativasAsesorDomain.crear(
                evaluacionAsesor, asesor, List.of(item1, item2));

        // Assert
        assertThat(omision.getEvaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(omision.getAsesor()).isEqualTo(asesor);
        assertThat(omision.getItems()).containsExactlyInAnyOrder(item1, item2);
    }

    @Test
    void debeRechazar_cuandoLoteVacio() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var items = List.<UUID>of();

        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCualitativasAsesorDomain.crear(evaluacionAsesor, asesor, items))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.LOTE_VACIO));
    }

    @Test
    void debeRechazar_cuandoItemsRepetidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var repetido = UtilUUID.generarNuevoUUID();
        var items = List.of(repetido, UtilUUID.generarNuevoUUID(), repetido);

        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCualitativasAsesorDomain.crear(evaluacionAsesor, asesor, items))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.ITEMS_REPETIDOS));
    }

    @Test
    void debeAcumularErrores_cuandoVariosCamposInvalidos() {
        // Arrange
        var items = List.<UUID>of();

        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCualitativasAsesorDomain.crear(null, null, items))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO,
                                        EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.LOTE_VACIO));
    }
}
