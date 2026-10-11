package com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DestituirEstudianteProyectoGradoCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var command = DestituirEstudianteProyectoGradoCommand.crear(proyectoGrado, estudiante, coordinador);

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.estudiante()).isEqualTo(estudiante);
        assertThat(command.coordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoLosTresLlegaNulos() {
        // Act & Assert
        assertThatThrownBy(() -> DestituirEstudianteProyectoGradoCommand.crear(null, null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.EstudianteProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_ID_REQUERIDO,
                                ProyectosCodes.EstudianteProyectoGrado.COORDINADOR_ID_REQUERIDO));
    }
}
