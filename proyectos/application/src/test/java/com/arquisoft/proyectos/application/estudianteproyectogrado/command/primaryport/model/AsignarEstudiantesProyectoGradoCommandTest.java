package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarEstudiantesProyectoGradoCommandTest {

    @Test
    void debeCrearCommandConLosUuidsConvertidos_cuandoLaEntradaEsValida() {
        // Arrange
        var proyectoGrado = UUID.randomUUID();
        var estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        // Act
        var command = AsignarEstudiantesProyectoGradoCommand.crear(
                proyectoGrado, estudiantes.stream().map(UUID::toString).toList());

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.estudiantes()).containsExactlyElementsOf(estudiantes);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoSinProyectoYConExcesoDeEstudiantesInvalidos() {
        // Arrange
        var estudiantes = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), "no-uuid");

        // Act & Assert
        assertThatThrownBy(() -> AsignarEstudiantesProyectoGradoCommand.crear(null, estudiantes))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_MAXIMO,
                                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_INVALIDO));
    }
}
