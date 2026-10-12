package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrarProyectoGradoCommandTest {

    @Test
    void debeCrearCommandConLosUuidsConvertidos_cuandoElPayloadEsValido() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();
        var estudiantes = List.of(UUID.randomUUID(), UUID.randomUUID());

        // Act
        var command = RegistrarProyectoGradoCommand.crear(fichaPerfil.toString(), "Sistema de gestión",
                coordinador.toString(), estudiantes.stream().map(UUID::toString).toList());

        // Assert
        assertThat(command.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(command.tituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.estudiantes()).containsExactlyElementsOf(estudiantes);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoIdsInvalidosTituloEnBlancoYSinEstudiantes() {
        // Act & Assert
        assertThatThrownBy(() -> RegistrarProyectoGradoCommand.crear("no-uuid", " ", "tampoco", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.ProyectoGrado.FICHA_PERFIL_ID_REQUERIDO,
                                ProyectosCodes.ProyectoGrado.TITULO_REQUERIDO,
                                ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO,
                                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_REQUERIDOS));
    }

    @Test
    void debeRechazarExcesoYUuidInvalido_cuandoLlegaMasDelMaximoDeEstudiantes() {
        // Arrange
        var estudiantes = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), "no-uuid");

        // Act & Assert
        assertThatThrownBy(() -> RegistrarProyectoGradoCommand.crear(UUID.randomUUID().toString(), "Titulo",
                UUID.randomUUID().toString(), estudiantes))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTES_MAXIMO,
                                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_INVALIDO));
    }
}
