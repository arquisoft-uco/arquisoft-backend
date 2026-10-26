package com.arquisoft.artefactos.application.asesorproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAsesorProyectoGradoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();

        // Act
        var command = AgregarAsesorProyectoGradoCommand.crear(asesor.toString(), proyecto.toString(), ocurridoEn);

        // Assert
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErrores_cuandoTodosLosDatosSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarAsesorProyectoGradoCommand.crear("no-es-uuid", null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.AsesorProyectoGrado.ASESOR,
                                ArtefactosFields.AsesorProyectoGrado.PROYECTO_GRADO,
                                ArtefactosFields.AsesorProyectoGrado.OCURRIDO_EN));
    }
}
