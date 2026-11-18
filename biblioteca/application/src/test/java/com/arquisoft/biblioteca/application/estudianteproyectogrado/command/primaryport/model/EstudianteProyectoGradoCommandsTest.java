package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.BibliotecaCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteProyectoGradoCommandsTest {

    @Test
    void debeCrearAgregarConTituloRecortado_cuandoLosDatosSonValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");

        // Act
        var command = AgregarEstudiantesProyectoGradoCommand.crear(
                proyecto.toString(), " Titulo ", List.of(primero.toString(), segundo.toString()), ocurridoEn);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.tituloProyecto()).isEqualTo("Titulo");
        assertThat(command.estudiantes()).containsExactly(primero, segundo);
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErroresDeAgregar_cuandoLosDatosSonInvalidos() {
        // Arrange
        var repetido = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(
                "no-es-uuid", " ", List.of(repetido, repetido, "malo"), null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .contains(BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                BibliotecaCodes.ProyectoGrado.TITULO_REQUERIDO,
                                BibliotecaCodes.EstudianteProyectoGrado.AUTOR_REQUERIDO,
                                BibliotecaCodes.EstudianteProyectoGrado.AUTOR_DUPLICADO,
                                BibliotecaCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO));
    }

    @Test
    void debeRechazarAgregar_cuandoNoHayEstudiantes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID().toString();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");

        // Act & Assert
        assertThatThrownBy(() -> AgregarEstudiantesProyectoGradoCommand.crear(proyecto, "Titulo", null, ocurridoEn))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(BibliotecaCodes.EstudianteProyectoGrado.AUTORES_REQUERIDOS)));
    }

    @Test
    void debeCrearRemover_cuandoLosDatosSonValidos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-11-15T10:00:00Z");

        // Act
        var command = RemoverEstudianteProyectoGradoCommand.crear(
                proyecto.toString(), estudiante.toString(), ocurridoEn);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
        assertThat(command.estudiante()).isEqualTo(estudiante);
        assertThat(command.ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeAcumularErroresDeRemover_cuandoLosDatosSonInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverEstudianteProyectoGradoCommand.crear("malo", null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                BibliotecaCodes.EstudianteProyectoGrado.PROYECTO_GRADO_REQUERIDO,
                                BibliotecaCodes.EstudianteProyectoGrado.AUTOR_REQUERIDO,
                                BibliotecaCodes.EstudianteProyectoGrado.OCURRIDO_EN_REQUERIDO));
    }
}
