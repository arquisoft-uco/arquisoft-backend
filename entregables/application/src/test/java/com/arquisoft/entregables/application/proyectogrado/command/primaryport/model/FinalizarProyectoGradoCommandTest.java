package com.arquisoft.entregables.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FinalizarProyectoGradoCommandTest {

    @Test
    void debeCrearCommandConUuidConvertido_cuandoElIdEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var command = FinalizarProyectoGradoCommand.crear(proyectoGrado.toString());

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoElIdEsNuloOMalFormado() {
        // Act & Assert
        assertThatThrownBy(() -> FinalizarProyectoGradoCommand.crear("no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(EntregablesCodes.ProyectoGrado.ID_REQUERIDO)));
        assertThatThrownBy(() -> FinalizarProyectoGradoCommand.crear(null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
