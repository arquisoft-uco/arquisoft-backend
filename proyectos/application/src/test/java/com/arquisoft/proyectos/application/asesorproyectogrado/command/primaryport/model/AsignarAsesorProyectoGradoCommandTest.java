package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignarAsesorProyectoGradoCommandTest {

    @Test
    void debeCrearCommandConElAsesorConvertido_cuandoLaEntradaEsValida() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var command = AsignarAsesorProyectoGradoCommand.crear(proyectoGrado, coordinador, asesor.toString());

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(command.coordinador()).isEqualTo(coordinador);
        assertThat(command.asesor()).isEqualTo(asesor);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoFaltanProyectoCoordinadorYAsesor() {
        // Act & Assert
        assertThatThrownBy(() -> AsignarAsesorProyectoGradoCommand.crear(null, null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.codigoError())
                        .containsExactlyInAnyOrder(
                                ProyectosCodes.AsesorProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.COORDINADOR_ID_REQUERIDO,
                                ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_REQUERIDO));
    }

    @Test
    void debeLanzarAsesorInvalido_cuandoElAsesorNoEsUuid() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> AsignarAsesorProyectoGradoCommand.crear(proyectoGrado, coordinador, "no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.AsesorProyectoGrado.ASESOR_ID_INVALIDO)));
    }
}
