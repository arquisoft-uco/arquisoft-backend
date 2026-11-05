package com.arquisoft.seguridad.application.autenticacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.validation.ApplicationValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class ResolverIdentidadSesionCommandTest {

    @Test
    void debeCrear_cuandoCredencialValida() {
        // Act
        var command = ResolverIdentidadSesionCommand.crear("  SECURITY_BASELINE_SESSION=abc  ");

        // Assert
        assertThat(command.credencial()).isEqualTo("SECURITY_BASELINE_SESSION=abc");
    }

    @Test
    void debeLanzar400_cuandoCredencialVaciaOExcedeLimite() {
        // Arrange
        var excedida = "x".repeat(SeguridadLimits.CredencialSesion.CREDENCIAL_MAX + 1);

        // Act
        var codigosVacia = codigosDe(() -> ResolverIdentidadSesionCommand.crear("  "));
        var codigosExcedida = codigosDe(() -> ResolverIdentidadSesionCommand.crear(excedida));

        // Assert
        assertThat(codigosVacia).containsExactly(SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_REQUERIDA);
        assertThat(codigosExcedida).containsExactly(SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_EXCEDE_LIMITE);
    }

    private static List<String> codigosDe(ThrowingCallable accion) {
        var excepcion = catchThrowableOfType(ApplicationValidationException.class, accion);
        return excepcion.getValidationResult().getErrores().stream()
                .map(ValidationError::codigoError)
                .toList();
    }
}
