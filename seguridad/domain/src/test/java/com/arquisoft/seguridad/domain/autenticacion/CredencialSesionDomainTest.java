package com.arquisoft.seguridad.domain.autenticacion;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadLimits;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class CredencialSesionDomainTest {

    @Test
    void debeCrear_cuandoCredencialValida() {
        // Act
        var sesion = CredencialSesionDomain.crear("  SECURITY_BASELINE_SESSION=abc  ");

        // Assert
        assertThat(sesion.getCredencial()).isEqualTo("SECURITY_BASELINE_SESSION=abc");
    }

    @Test
    void debeLanzar422_cuandoCredencialVaciaOExcedeLimite() {
        // Arrange
        var excedida = "x".repeat(SeguridadLimits.CredencialSesion.CREDENCIAL_MAX + 1);

        // Act
        var codigosVacia = codigosDe(() -> CredencialSesionDomain.crear("   "));
        var codigosNula = codigosDe(() -> CredencialSesionDomain.crear(null));
        var codigosExcedida = codigosDe(() -> CredencialSesionDomain.crear(excedida));
        var limiteExacto = CredencialSesionDomain.crear("x".repeat(SeguridadLimits.CredencialSesion.CREDENCIAL_MAX));

        // Assert
        assertThat(codigosVacia).containsExactly(SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_REQUERIDA);
        assertThat(codigosNula).containsExactly(SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_REQUERIDA);
        assertThat(codigosExcedida).containsExactly(SeguridadCodes.CredencialSesion.SESION_CREDENCIAL_EXCEDE_LIMITE);
        assertThat(limiteExacto.getCredencial()).hasSize(SeguridadLimits.CredencialSesion.CREDENCIAL_MAX);
    }

    private static List<String> codigosDe(ThrowingCallable accion) {
        var excepcion = catchThrowableOfType(DomainValidationException.class, accion);
        return excepcion.getValidationResult().getErrores().stream()
                .map(ValidationError::codigoError)
                .toList();
    }
}
