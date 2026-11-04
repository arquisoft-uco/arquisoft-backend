package com.arquisoft.fichas.application.observacionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarObservacionItemCommandTest {

    @Test
    void debeCrearCommandConTrim_cuandoDatosValidos() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var command = ModificarObservacionItemCommand.crear(observacionItem, "  Observación válida  ", asesorFicha);

        // Assert
        assertThat(command.observacionItem()).isEqualTo(observacionItem);
        assertThat(command.observacion()).isEqualTo("Observación válida");
        assertThat(command.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act & Assert — Notification Pattern: no aborta en el primero
        assertThatThrownBy(() -> ModificarObservacionItemCommand.crear(null, "   ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO,
                            FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA,
                            FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            FichasFields.ObservacionItem.OBSERVACION_ITEM,
                            FichasFields.ObservacionItem.OBSERVACION,
                            FichasFields.ObservacionItem.ASESOR_FICHA);
                });
    }

    @Test
    void debeLanzarError_cuandoObservacionExcedeLongitudMaxima() {
        // Arrange
        var observacionDemasiadoLarga = "x".repeat(201);

        // Act & Assert
        assertThatThrownBy(() -> ModificarObservacionItemCommand.crear(
                UtilUUID.generarNuevoUUID(), observacionDemasiadoLarga, UtilUUID.generarNuevoUUID()))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactly(FichasCodes.ObservacionItem.OBSERVACION_DEMASIADO_LARGA);
                });
    }
}
