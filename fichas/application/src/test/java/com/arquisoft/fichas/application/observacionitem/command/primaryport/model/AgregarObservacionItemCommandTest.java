package com.arquisoft.fichas.application.observacionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarObservacionItemCommandTest {

    @Test
    void debeCrearCommandConTrim_cuandoDatosValidos() {
        // Arrange
        var revisionItem = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();

        // Act
        var command = AgregarObservacionItemCommand.crear(revisionItem, "  Observación válida  ", asesorFicha);

        // Assert — el trim es lo que hace que unicidad y UNIQUE de BD vean el mismo texto
        assertThat(command.revisionItem()).isEqualTo(revisionItem);
        assertThat(command.observacion()).isEqualTo("Observación válida");
        assertThat(command.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act & Assert — Notification Pattern: no aborta en el primero
        assertThatThrownBy(() -> AgregarObservacionItemCommand.crear(null, "   ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            FichasCodes.ObservacionItem.REVISION_ITEM_REQUERIDO,
                            FichasCodes.ObservacionItem.OBSERVACION_REQUERIDA,
                            FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            FichasFields.ObservacionItem.REVISION_ITEM,
                            FichasFields.ObservacionItem.OBSERVACION,
                            FichasFields.ObservacionItem.ASESOR_FICHA);
                });
    }

    @Test
    void debeLanzarError_cuandoObservacionExcedeLongitudMaxima() {
        // Arrange
        var observacionDemasiadoLarga = "x".repeat(201);

        // Act & Assert
        assertThatThrownBy(() -> AgregarObservacionItemCommand.crear(
                UUID.randomUUID(), observacionDemasiadoLarga, UUID.randomUUID()))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactly(FichasCodes.ObservacionItem.OBSERVACION_DEMASIADO_LARGA);
                });
    }
}
