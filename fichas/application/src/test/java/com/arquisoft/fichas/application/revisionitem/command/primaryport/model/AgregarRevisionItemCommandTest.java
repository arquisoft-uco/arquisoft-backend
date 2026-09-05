package com.arquisoft.fichas.application.revisionitem.command.primaryport.model;

import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarRevisionItemCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();

        // Act
        var command = AgregarRevisionItemCommand.crear(item, asesorFicha);

        // Assert
        assertThat(command.item()).isEqualTo(item);
        assertThat(command.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeLanzarExcepcion_cuandoItemNulo() {
        // Arrange
        UUID asesorFicha = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> AgregarRevisionItemCommand.crear(null, asesorFicha))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactly(FichasCodes.RevisionItem.ITEM_REQUERIDO);
                    assertThat(errores).extracting("campo")
                            .containsExactly(FichasFields.RevisionItem.ITEM);
                });
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorFichaNulo() {
        // Arrange
        UUID item = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> AgregarRevisionItemCommand.crear(item, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError")
                            .containsExactly(FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO);
                    assertThat(errores).extracting("campo")
                            .containsExactly(FichasFields.RevisionItem.ASESOR_FICHA);
                });
    }

    @Test
    void debeAcumularLosDosErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act & Assert — Notification Pattern: no aborta en el primero
        assertThatThrownBy(() -> AgregarRevisionItemCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            FichasCodes.RevisionItem.ITEM_REQUERIDO,
                            FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            FichasFields.RevisionItem.ITEM,
                            FichasFields.RevisionItem.ASESOR_FICHA);
                });
    }
}
