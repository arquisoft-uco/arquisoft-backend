package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand.Par;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class RegistrarEvaluacionesCualitativasAsesorCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var criterio = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarEvaluacionesCualitativasAsesorCommand.crear(
                evaluacionAsesor.toString(), asesor.toString(),
                List.of(new Par<>(item.toString(), criterio.toString())));

        // Assert
        assertThat(command.evaluacionAsesor()).isEqualTo(evaluacionAsesor);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.evaluaciones()).hasSize(1);
        assertThat(command.evaluaciones().get(0).item()).isEqualTo(item);
        assertThat(command.evaluaciones().get(0).criterio()).isEqualTo(criterio);
    }

    @Test
    void debeRechazar_cuandoLoteVacio() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID().toString();
        var asesor = UtilUUID.generarNuevoUUID().toString();
        var pares = List.<Par<String>>of();

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasAsesorCommand.crear(evaluacionAsesor, asesor, pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.codigoError())
                                .containsExactly(EvaluacionesCodes.RegistroEvaluacionesCualitativasAsesor.LOTE_VACIO));
    }

    @Test
    void debeAcumularErroresIndexados_cuandoItemYCriterioEstanEnBlancoOSonInvalidos() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID().toString();
        var asesor = UtilUUID.generarNuevoUUID().toString();
        var pares = List.of(new Par<>("", ""), new Par<>(" ", "no-es-un-uuid"));

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasAsesorCommand.crear(evaluacionAsesor, asesor, pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactlyInAnyOrder(
                                        tuple("evaluaciones[0].item",
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEM_REQUERIDO),
                                        tuple("evaluaciones[0].criterio",
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_REQUERIDO),
                                        tuple("evaluaciones[1].item",
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEM_REQUERIDO),
                                        tuple("evaluaciones[1].criterio",
                                                EvaluacionesCodes.EvaluacionCualitativaAsesor.CRITERIO_INVALIDO)));
    }

    @Test
    void debeRechazar_cuandoItemTieneFormatoUuidInvalido() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID().toString();
        var asesor = UtilUUID.generarNuevoUUID().toString();
        var pares = List.of(new Par<>("no-es-un-uuid", UtilUUID.generarNuevoUUID().toString()));

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasAsesorCommand.crear(evaluacionAsesor, asesor, pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo(), error -> error.codigoError())
                                .containsExactly(tuple("evaluaciones[0].item",
                                        EvaluacionesCodes.EvaluacionCualitativaAsesor.ITEM_INVALIDO)));
    }

    @Test
    void debeAcumularErrores_cuandoEvaluacionAsesorYAsesorSonInvalidos() {
        // Arrange
        var pares = List.of(new Par<>(UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString()));

        // Act & Assert
        assertThatThrownBy(() -> RegistrarEvaluacionesCualitativasAsesorCommand.crear("no-uuid", " ", pares))
                .isInstanceOfSatisfying(ApplicationValidationException.class, exception ->
                        assertThat(exception.getValidationResult().getErrores())
                                .extracting(error -> error.campo())
                                .contains(EvaluacionesFields.EvaluacionCualitativaAsesor.EVALUACION_ASESOR,
                                        EvaluacionesFields.EvaluacionCualitativaAsesor.ASESOR));
    }
}
