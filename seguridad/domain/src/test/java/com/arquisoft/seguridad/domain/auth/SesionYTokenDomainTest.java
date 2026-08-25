package com.arquisoft.seguridad.domain.auth;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

class SesionYTokenDomainTest {

    @Test
    void debeConstruirLaSesion_cuandoIdentificadorYTtlSonValidos() {
        // Act
        var sesion = SesionDomain.crear("jti-123", 300L);

        // Assert
        assertThat(sesion.getIdentificadorToken()).isEqualTo("jti-123");
        assertThat(sesion.getTiempoVidaRestante()).isEqualTo(300L);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void debeReportarIdentificadorRequerido_cuandoElIdentificadorDeSesionEstaEnBlanco(String identificador) {
        // Act / Assert
        assertThat(codigosDe(() -> SesionDomain.crear(identificador, 300L)))
                .containsExactly(SeguridadCodes.Sesion.SESION_IDENTIFICADOR_REQUERIDO);
    }

    @ParameterizedTest
    @ValueSource(longs = {-1L, -300L})
    void debeReportarTtlInvalido_cuandoElTtlEsNegativo(long ttl) {
        // Act / Assert
        assertThat(codigosDe(() -> SesionDomain.crear("jti-123", ttl)))
                .containsExactly(SeguridadCodes.Sesion.SESION_TTL_INVALIDO);
    }

    @Test
    void debeConstruirLaSesionSinRequerirInvalidacion_cuandoElTtlEsCero() {
        // Act — TTL cero es un token ya vencido, no un dato invalido
        var sesion = SesionDomain.crear("jti-123", 0L);

        // Assert
        assertThat(sesion.requiereInvalidacion()).isFalse();
    }

    @Test
    void debeAcumularLosDosErrores_cuandoIdentificadorYTtlSonInvalidos() {
        // Act / Assert — Notification Pattern: no aborta en el primer error
        assertThat(codigosDe(() -> SesionDomain.crear("  ", -1L)))
                .containsExactlyInAnyOrder(
                        SeguridadCodes.Sesion.SESION_IDENTIFICADOR_REQUERIDO,
                        SeguridadCodes.Sesion.SESION_TTL_INVALIDO);
    }

    @Test
    void debeConservarElValorRecortado_cuandoElTokenNoEstaEnBlanco() {
        // Act / Assert
        assertThat(TokenDomain.crear("  eyJhbGc...  ").getValor()).isEqualTo("eyJhbGc...");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void debeReportarValorRequerido_cuandoElTokenEstaEnBlanco(String valor) {
        // Act / Assert
        assertThat(codigosDe(() -> TokenDomain.crear(valor)))
                .containsExactly(SeguridadCodes.Token.TOKEN_VALOR_REQUERIDO);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoHayErrores() {
        // Act / Assert — sigue siendo 422, pero ahora acompanado de fieldErrors[]
        assertThatThrownBy(() -> TokenDomain.crear(null))
                .isInstanceOf(DomainValidationException.class);
    }

    private static List<String> codigosDe(ThrowingCallable accion) {
        var excepcion = catchThrowableOfType(DomainValidationException.class, accion);
        return excepcion.getValidationResult().getErrores().stream()
                .map(ValidationError::codigoError)
                .toList();
    }

}
