package com.arquisoft.biblioteca.application.documentacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarDocumentacionCommandTest {

    @Test
    void debeCrearConLosIds_cuandoLosDatosSonValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var bibliotecario = UtilUUID.generarNuevoUUID();

        // Act
        var command = RegistrarDocumentacionCommand.crear(entregable.toString(), bibliotecario);

        // Assert
        assertThat(command.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(command.bibliotecario()).isEqualTo(bibliotecario);
    }

    @Test
    void debeAcumularErrores_cuandoElUuidEstaMalFormadoYFaltaElActor() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarDocumentacionCommand.crear("no-es-uuid", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.Documentacion.ENTREGABLE_REQUERIDO,
                                BibliotecaCodes.Documentacion.BIBLIOTECARIO_REQUERIDO));
    }
}
