package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstudiantesProyectoGradoCommandTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Test
    void debeCrearCommandConUuidsConvertidos_cuandoElPayloadEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarEstudiantesProyectoGradoCommand.crear(
                proyectoGrado.toString(), List.of(primero.toString(), segundo.toString()), OCURRIDO_EN);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.estudiantes()).containsExactly(primero, segundo);
        assertThat(command.ocurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeRechazar_cuandoElProyectoNoEsUuid() {
        // Arrange
        var estudiantes = List.of(UtilUUID.generarNuevoUUID().toString());

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear("no-uuid", estudiantes, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO));
    }

    @Test
    void debeRechazar_cuandoUnEstudianteNoEsUuid() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID().toString();
        var estudiantes = List.of(UtilUUID.generarNuevoUUID().toString(), "no-uuid");

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyectoGrado, estudiantes, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTE));
    }

    @Test
    void debeRechazar_cuandoLaListaEstaVaciaONula() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyectoGrado, List.of(), OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTES));
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyectoGrado, null, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class);
    }

    @Test
    void debeRechazar_cuandoHayEstudiantesDuplicados() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID().toString();
        var estudiante = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(
                proyectoGrado, List.of(estudiante, estudiante), OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.ESTUDIANTES));
    }

    @Test
    void debeRechazar_cuandoOcurridoEnEsNulo() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID().toString();
        var estudiantes = List.of(UtilUUID.generarNuevoUUID().toString());

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyectoGrado, estudiantes, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.OCURRIDO_EN));
    }

    @Test
    void debeRechazar_cuandoElProyectoEsElUuidDeCeros() {
        // Arrange
        var estudiantes = List.of(UtilUUID.generarNuevoUUID().toString());

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(
                UtilUUID.obtenerPorDefectoComoTexto(), estudiantes, OCURRIDO_EN))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(e -> e.campo())
                        .containsExactly(EvaluacionesFields.EstudianteProyectoGrado.PROYECTO_GRADO));
    }
}
