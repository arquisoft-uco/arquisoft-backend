package com.arquisoft.fichas.application.observacionitem.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverObservacionItemCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var observacionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverObservacionItemCommand.crear(observacionItem, asesorFicha);

        // Assert
        assertThat(command.observacionItem()).isEqualTo(observacionItem);
        assertThat(command.asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoObservacionItemYAsesorFichaNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverObservacionItemCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo")
                            .containsExactlyInAnyOrder(
                                    FichasFields.ObservacionItem.OBSERVACION_ITEM,
                                    FichasFields.ObservacionItem.ASESOR_FICHA);
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(
                                    FichasCodes.ObservacionItem.OBSERVACION_ITEM_REQUERIDO,
                                    FichasCodes.ObservacionItem.ASESOR_FICHA_REQUERIDO);
                });
    }
}
