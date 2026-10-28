package com.arquisoft.artefactos.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class AgregarEstudiantesProyectoGradoCommandTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-02T10:00:00Z");

    @Test
    void debeCrearConEstudiantesEnOrden_cuandoDatosValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarEstudiantesProyectoGradoCommand.crear(
                proyecto.toString(), List.of(primero.toString(), segundo.toString()), OCURRIDO_EN);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.estudiantes()).containsExactly(primero, segundo);
        assertThat(command.ocurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeAcumularErrores_cuandoTodoEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(null, null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(ValidationError::campo, ValidationError::codigoError)
                        .containsExactlyInAnyOrder(
                                tuple(ArtefactosFields.EstudianteProyectoGrado.PROYECTO_GRADO,
                                        ArtefactosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO),
                                tuple(ArtefactosFields.EstudianteProyectoGrado.ESTUDIANTES,
                                        ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS),
                                tuple(ArtefactosFields.EstudianteProyectoGrado.OCURRIDO_EN,
                                        ArtefactosCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO)));
    }

    @Test
    void debeRechazar_cuandoUnEstudianteNoEsUuid() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID().toString();
        var estudiantes = List.of(UtilUUID.generarNuevoUUID().toString(), "no-es-uuid");

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyecto, estudiantes, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_REQUERIDO)));
    }

    @Test
    void debeRechazar_cuandoUnEstudianteSeRepite() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID().toString();
        var estudiante = UtilUUID.generarNuevoUUID().toString();
        var estudiantes = List.of(estudiante, estudiante);

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyecto, estudiantes, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.EstudianteProyectoGrado.ESTUDIANTES_DUPLICADOS)));
    }
}
