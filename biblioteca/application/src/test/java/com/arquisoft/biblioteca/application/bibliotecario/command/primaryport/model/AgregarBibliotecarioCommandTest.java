package com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarBibliotecarioCommandTest {

    @Test
    void debeCrearCommandConUuidConvertido_cuandoElPayloadEsValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var command = AgregarBibliotecarioCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("20161020123");
        assertThat(command.nombre()).isEqualTo("Ana Perez");
        assertThat(command.email()).isEqualTo("ana@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoVariosCamposSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarBibliotecarioCommand.crear("no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactlyInAnyOrder(BibliotecaFields.Bibliotecario.ID,
                                BibliotecaFields.Bibliotecario.IDENTIFICADOR, BibliotecaFields.Bibliotecario.NOMBRE,
                                BibliotecaFields.Bibliotecario.EMAIL, BibliotecaFields.Bibliotecario.OCURRIDO_EN));
    }
}
