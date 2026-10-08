package com.arquisoft.proyectos.application.asesor.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAsesorCommandTest {

    @Test
    void debeCrearCommand_cuandoElPayloadEsValido() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-09-23T10:00:00Z");

        // Act
        var command = RemoverAsesorCommand.crear(
                id.toString(), "1036950123", "Carlos Rios", "carlos@uco.edu.co", ocurridoEn);

        // Assert
        assertThat(command.id()).isEqualTo(id);
        assertThat(command.identificador()).isEqualTo("1036950123");
        assertThat(command.nombre()).isEqualTo("Carlos Rios");
        assertThat(command.email()).isEqualTo("carlos@uco.edu.co");
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoElPayloadEsInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverAsesorCommand.crear("no-es-un-uuid", " ", " ", " ", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .contains(ProyectosFields.Asesor.ID, ProyectosFields.Asesor.IDENTIFICADOR,
                                ProyectosFields.Asesor.NOMBRE, ProyectosFields.Asesor.EMAIL,
                                ProyectosFields.Asesor.OCURRIDO_EN));
    }
}
