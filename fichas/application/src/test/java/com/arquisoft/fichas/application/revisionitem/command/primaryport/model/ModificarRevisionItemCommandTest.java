package com.arquisoft.fichas.application.revisionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarRevisionItemCommandTest {

    @Test
    void debeConstruir_cuandoDatosValidos() {
        // Arrange
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();

        // Act
        var command = ModificarRevisionItemCommand.crear(item, "  VISUALIZADA  ", asesorFicha);

        // Assert — el estado se recorta, item/asesorFicha llegan tal cual
        assertThat(command.item()).isEqualTo(item);
        assertThat(command.estadoRevision()).isEqualTo("VISUALIZADA");
        assertThat(command.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeConstruir_cuandoEstadoRevisionNoPerteneceAlCatalogo() {
        // Arrange — la pertenencia al catálogo la valida el objeto de acción, no el Command
        UUID item = UUID.randomUUID();
        UUID asesorFicha = UUID.randomUUID();

        // Act
        var command = ModificarRevisionItemCommand.crear(item, "ESTADO_DESCONOCIDO", asesorFicha);

        // Assert
        assertThat(command.estadoRevision()).isEqualTo("ESTADO_DESCONOCIDO");
    }

    @Test
    void debeLanzarExcepcion_cuandoEstadoRevisionExcedeLongitudMaxima() {
        // Arrange
        String estadoDe51Caracteres = "A".repeat(51);

        // Act & Assert
        assertThatThrownBy(() -> ModificarRevisionItemCommand.crear(
                UUID.randomUUID(), estadoDe51Caracteres, UUID.randomUUID()))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError").containsExactly(
                            FichasCodes.RevisionItem.ESTADO_REVISION_DEMASIADO_LARGO);
                });
    }

    @Test
    void debeAcumularLosTresErrores_cuandoTodosLosCamposSonInvalidos() {
        // Act & Assert — Notification Pattern: no aborta en el primero
        assertThatThrownBy(() -> ModificarRevisionItemCommand.crear(null, "", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            FichasCodes.RevisionItem.ITEM_REQUERIDO,
                            FichasCodes.RevisionItem.ESTADO_REVISION_REQUERIDO,
                            FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            FichasFields.RevisionItem.ITEM,
                            FichasFields.RevisionItem.ESTADO_REVISION,
                            FichasFields.RevisionItem.ASESOR_FICHA);
                });
    }
}
