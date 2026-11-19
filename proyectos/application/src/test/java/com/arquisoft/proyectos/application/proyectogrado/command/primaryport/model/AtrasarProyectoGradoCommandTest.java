package com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model;

import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AtrasarProyectoGradoCommandTest {

    @Test
    void debeConvertirElUuid_cuandoElDatoEsValido() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();

        // Act
        var command = AtrasarProyectoGradoCommand.crear(proyecto.toString());

        // Assert
        assertThat(command.proyectoGrado()).isEqualTo(proyecto);
    }

    @Test
    void debeLanzar400_cuandoDatoMalformado() {
        // Act & Assert
        assertThatThrownBy(() -> AtrasarProyectoGradoCommand.crear("no-uuid"))
                .isInstanceOfSatisfying(ApplicationValidationException.class, excepcion ->
                        assertThat(excepcion.getValidationResult().getErrores())
                                .extracting(ValidationError::codigoError)
                                .containsExactly(ProyectosCodes.ProyectoGrado.PROYECTO_GRADO_ID_REQUERIDO));
    }
}
