package com.arquisoft.fichas.application.revisionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverRevisionItemCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverRevisionItemCommand.crear(revisionItem, asesorFicha);

        // Assert
        assertThat(command.revisionItem()).isEqualTo(revisionItem);
        assertThat(command.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoRevisionItemYAsesorFichaNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverRevisionItemCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo")
                            .containsExactlyInAnyOrder(
                                    FichasFields.RevisionItem.REVISION_ITEM,
                                    FichasFields.RevisionItem.ASESOR_FICHA);
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(
                                    FichasCodes.RevisionItem.REVISION_ITEM_REQUERIDO,
                                    FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO);
                });
    }
}
