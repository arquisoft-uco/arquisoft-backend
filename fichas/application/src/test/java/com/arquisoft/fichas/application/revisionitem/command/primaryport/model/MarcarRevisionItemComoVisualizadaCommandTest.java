package com.arquisoft.fichas.application.revisionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MarcarRevisionItemComoVisualizadaCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var command = MarcarRevisionItemComoVisualizadaCommand.crear(revisionItem, estudiante);

        // Assert
        assertThat(command.revisionItem()).isEqualTo(revisionItem);
        assertThat(command.estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoRevisionItemYEstudianteNulos() {
        // Act & Assert
        assertThatThrownBy(() -> MarcarRevisionItemComoVisualizadaCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo")
                            .containsExactlyInAnyOrder(
                                    FichasFields.RevisionItem.REVISION_ITEM,
                                    FichasFields.RevisionItem.ESTUDIANTE);
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(
                                    FichasCodes.RevisionItem.REVISION_ITEM_REQUERIDO,
                                    FichasCodes.RevisionItem.ESTUDIANTE_REQUERIDO);
                });
    }
}
