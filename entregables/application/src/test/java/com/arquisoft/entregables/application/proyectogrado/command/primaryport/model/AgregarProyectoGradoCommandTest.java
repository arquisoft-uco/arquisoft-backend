package com.arquisoft.entregables.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.message.constant.EntregablesLimits;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarProyectoGradoCommandTest {

    @Test
    void debeCrearConNombreRecortado_cuandoDatosValidos() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act
        var command = AgregarProyectoGradoCommand.crear(id.toString(), "  Sistema de gestion  ");

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
    void debeRechazar_cuandoElNombreSuperaElMaximo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID().toString();
        var nombre = "a".repeat(EntregablesLimits.ProyectoGrado.NOMBRE_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> AgregarProyectoGradoCommand.crear(id, nombre))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(EntregablesCodes.ProyectoGrado.NOMBRE_LONGITUD_MAXIMA)));
    }
}
