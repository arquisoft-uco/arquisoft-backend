package com.arquisoft.seguridad.application.auth.command.primaryport.model;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutenticarUsuarioCommandTest {

    private static final String CLAVE_VALIDA = "password123";

    @Test
    void debeConstruir_cuandoLosDatosSonValidos() {
        // Act
        var command = AutenticarUsuarioCommand.crear("  estudiante@uco.edu.co  ", CLAVE_VALIDA);

        // Assert — el correo se recorta, la contrasena no
        assertThat(command.email()).isEqualTo("estudiante@uco.edu.co");
        assertThat(command.contrasena()).isEqualTo(CLAVE_VALIDA);
    }

    @Test
    void debeAcumularLosDosErrores_cuandoCorreoYClaveSonInvalidos() {
        // Act / Assert — Notification Pattern: no aborta en el primero
        assertThatThrownBy(() -> AutenticarUsuarioCommand.crear("no-es-correo", "123"))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var errores = ((ApplicationValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            SeguridadCodes.Autenticacion.EMAIL_FORMATO_INVALIDO,
                            SeguridadCodes.Autenticacion.CONTRASENA_DEMASIADO_CORTA);
                    assertThat(errores).extracting("campo").contains(
                            SeguridadFields.Autenticacion.EMAIL,
                            SeguridadFields.Autenticacion.CONTRASENA);
                });
    }

    @Test
    void debeNoExponerLaContrasena_cuandoSeImprimeElCommand() {
        // Arrange
        var command = AutenticarUsuarioCommand.crear("estudiante@uco.edu.co", CLAVE_VALIDA);

        // Act / Assert
        assertThat(command.toString())
                .doesNotContain(CLAVE_VALIDA)
                .contains("estudiante@uco.edu.co");
    }
}
