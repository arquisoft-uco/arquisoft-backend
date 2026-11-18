package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OmisionEvaluacionesCuantitativasAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();

        // Act
        var omision = OmisionEvaluacionesCuantitativasAsesorDomain.crear(
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
        assertThatThrownBy(() -> OmisionEvaluacionesCuantitativasAsesorDomain.crear(evaluacionAsesor, asesor, items))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.OmisionEvaluacionesCuantitativasAsesor.LOTE_VACIO));
    }

    @Test
    void debeRechazar_cuandoItemsRepetidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var repetido = UtilUUID.generarNuevoUUID();
        var items = List.of(repetido, UtilUUID.generarNuevoUUID(), repetido);

        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCuantitativasAsesorDomain.crear(evaluacionAsesor, asesor, items))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(
                                        EvaluacionesCodes.OmisionEvaluacionesCuantitativasAsesor.ITEMS_REPETIDOS));
    }

    @Test
    void debeAcumularErrores_cuandoVariosCamposInvalidos() {
        // Arrange
        var items = List.<UUID>of();

        // Act & Assert
        assertThatThrownBy(() -> OmisionEvaluacionesCuantitativasAsesorDomain.crear(null, null, items))
                .isInstanceOfSatisfying(DomainValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        EvaluacionesCodes.EvaluacionCuantitativaAsesor.EVALUACION_ASESOR_REQUERIDO,
                                        EvaluacionesCodes.EvaluacionCuantitativaAsesor.ASESOR_REQUERIDO,
                                        EvaluacionesCodes.OmisionEvaluacionesCuantitativasAsesor.LOTE_VACIO));
    }
}
