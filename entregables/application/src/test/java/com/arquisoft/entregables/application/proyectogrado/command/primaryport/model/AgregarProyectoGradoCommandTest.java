package com.arquisoft.entregables.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarProyectoGradoCommandTest {

    @Test
    void debeCrearConNombreTalCual_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarProyectoGradoCommand.crear(id.toString(), "Sistema de gestion");

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(id);
        assertThat(command.nombre()).isEqualTo("Sistema de gestion");
    }

    @Test
    void debeAcumularErrores_cuandoIdInvalidoYNombreEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> AgregarProyectoGradoCommand.crear("no-es-uuid", " "))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting(error -> error.campo())
                        .containsExactlyInAnyOrder(
                                EntregablesFields.ProyectoGrado.ID, EntregablesFields.ProyectoGrado.NOMBRE));
    }

    @Test
    void debeCrear_cuandoElNombreEsMuyLargo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var nombre = "a".repeat(500);

        // Act
        var command = AgregarProyectoGradoCommand.crear(id.toString(), nombre);

        // Assert
        assertThat(command.nombre()).isEqualTo(nombre);
    }
}
