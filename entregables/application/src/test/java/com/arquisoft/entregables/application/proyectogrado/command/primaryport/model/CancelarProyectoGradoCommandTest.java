package com.arquisoft.entregables.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancelarProyectoGradoCommandTest {

    @Test
    void debeCrearCommandConUuidConvertido_cuandoElIdEsValido() {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();

        // Act
        var command = CancelarProyectoGradoCommand.crear(proyectoGrado.toString());

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyectoGrado);
    }

    @Test
    void debeAcumularErroresDeEntrada_cuandoIdInvalido() {
        // Act & Assert
        assertThatThrownBy(() -> CancelarProyectoGradoCommand.crear("no-es-uuid"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(EntregablesCodes.ProyectoGrado.ID_REQUERIDO)));
        assertThatThrownBy(() -> CancelarProyectoGradoCommand.crear(null))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
