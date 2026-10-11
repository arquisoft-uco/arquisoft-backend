package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoAsesorProyectoGradoCommandTest {

    @Test
    void debeCrearCommand_cuandoDatosValidos() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var asesorProyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarEstadoAsesorProyectoGradoCommand.crear(
                proyectoGrado, asesorProyectoGrado, coordinador, "FINALIZADO");

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.asesorProyectoGrado()).isEqualTo(asesorProyectoGrado);
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.estadoAsesor()).isEqualTo("FINALIZADO");
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoFaltanIdsYEstadoEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoAsesorProyectoGradoCommand.crear(null, null, null, " "))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.ASESOR_PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.ESTADO_ASESOR_REQUERIDO));
    }
}
