package com.arquisoft.artefactos.application.artefacto.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarArtefactoCommandTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarArtefactoCommand.crear(
                proyecto.toString(), asesor, version.toString(), List.of(estudiante.toString()));

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.asesor()).isEqualTo(asesor);
        assertThat(command.versionRepositorioArtefacto()).isEqualTo(version);
        assertThat(command.estudiantes()).containsExactly(estudiante);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoProyectoVersionYEstudiantesSonInvalidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarArtefactoCommand.crear("x", asesor, "y", List.of("z")))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                ArtefactosFields.Artefacto.PROYECTO_GRADO,
                                ArtefactosFields.Artefacto.VERSION_REPOSITORIO_ARTEFACTO,
                                ArtefactosFields.EstudianteArtefacto.ESTUDIANTES));
    }

    @Test
    void debeAceptarListaVacia_cuandoNoHayEstudiantes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID().toString();
        var version = UtilUUID.generarNuevoUUID().toString();

        // Act
        var command = AsignarArtefactoCommand.crear(proyecto, UtilUUID.generarNuevoUUID(), version, null);

        // Assert
        assertThat(command.estudiantes()).isEmpty();
    }
}
