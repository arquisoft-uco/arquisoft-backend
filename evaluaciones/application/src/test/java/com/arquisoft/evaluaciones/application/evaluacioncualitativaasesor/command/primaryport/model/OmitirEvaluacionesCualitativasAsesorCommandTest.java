package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class OmitirEvaluacionesCualitativasAsesorCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item1 = UtilUUID.generarNuevoUUID();
        var item2 = UtilUUID.generarNuevoUUID();

        // Act
        var command = OmitirEvaluacionesCualitativasAsesorCommand.crear(
                evaluacionAsesor, asesor, List.of(item1.toString(), item2.toString()));

        // Assert
        assertThat(command.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.items()).containsExactly(item1, item2);
    }

    @Test
    void debeLanzar400_cuandoNoHayItems() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCualitativasAsesorCommand.crear(evaluacionAsesor, asesor, null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple(
                                        EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS,
                                        EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor.LOTE_VACIO)));
    }

    @Test
    void debeAcumularErroresConIndice_cuandoVariosDatosInvalidos() {
        // Arrange
        var items = List.of(" ", UtilUUID.generarNuevoUUID().toString(), "no-es-un-uuid");
        var campo = EvaluacionesFields.OmisionEvaluacionesCualitativasAsesor.ITEMS;

        // Act & Assert
        assertThatThrownBy(() -> OmitirEvaluacionesCualitativasAsesorCommand.crear(null, null, items))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor
                                                        .EVALUACION_ASESOR_REQUERIDO),
                                        tuple(EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR,
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.ASESOR_REQUERIDO),
                                        tuple(campo + "[0]",
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor
                                                        .ITEM_REQUERIDO),
                                        tuple(campo + "[2]",
                                                EvaluacionesCodes.OmisionEvaluacionesCualitativasAsesor
                                                        .ITEM_INVALIDO)));
    }
}
