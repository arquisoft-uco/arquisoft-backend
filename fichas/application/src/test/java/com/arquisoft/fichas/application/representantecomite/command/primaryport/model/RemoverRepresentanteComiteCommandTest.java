package com.arquisoft.fichas.application.representantecomite.command.primaryport.model;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverRepresentanteComiteCommandTest {

    @Test
    void debeCrearCommandRecortado_cuandoElPayloadEsValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-24T10:00:00Z");

        // Act
        var command = RemoverRepresentanteComiteCommand.crear(
                id.toString(), " 20161020123 ", " Ana Pérez ", " ana.perez@uco.edu.co ", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("20161020123");
        assertThat(command.nombre()).isEqualTo("Ana Pérez");
        assertThat(command.email()).isEqualTo("ana.perez@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoElPayloadEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverRepresentanteComiteCommand.crear("no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores)
                            .extracting(e -> e.campo())
                            .containsExactlyInAnyOrder(
                                    FichasFields.RepresentanteComite.ID,
                                    FichasFields.RepresentanteComite.IDENTIFICADOR,
                                    FichasFields.RepresentanteComite.NOMBRE,
                                    FichasFields.RepresentanteComite.EMAIL,
                                    FichasFields.RepresentanteComite.OCURRIDO_EN);
                    assertThat(errores)
                            .extracting(e -> e.codigoError())
                            .contains(
                                    FichasCodes.RepresentanteComite.IDENTIFICADOR_REQUERIDO,
                                    FichasCodes.RepresentanteComite.NOMBRE_REQUERIDO,
                                    FichasCodes.RepresentanteComite.EMAIL_REQUERIDO,
                                    FichasCodes.RepresentanteComite.OCURRIDO_EN_REQUERIDO);
                });
    }
}
