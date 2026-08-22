package com.arquisoft.seguridad.domain.auth;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutenticacionDomainTest {

    private static final String CORREO_VALIDO = "estudiante@uco.edu.co";
    private static final String CLAVE_VALIDA = "secreto";

    @Test
    void debeConstruir_cuandoCorreoYClaveSonValidos() {
        // Act
        var autenticacion = AutenticacionDomain.crear("  " + CORREO_VALIDO + "  ", CLAVE_VALIDA);

        // Assert — el correo se recorta, la clave no
        assertThat(autenticacion.getCorreo()).isEqualTo(CORREO_VALIDO);
        assertThat(autenticacion.getClaveAcceso()).isEqualTo(CLAVE_VALIDA);
    }

    @Test
    void debeAcumularLosDosErrores_cuandoCorreoYClaveSonInvalidos() {
        // Act / Assert — Notification Pattern: no aborta en el primer error
        assertThatThrownBy(() -> AutenticacionDomain.crear("no-es-correo", "123"))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            SeguridadCodes.Autenticacion.EMAIL_FORMATO_INVALIDO,
                            SeguridadCodes.Autenticacion.CONTRASENA_DEMASIADO_CORTA);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            SeguridadFields.Autenticacion.EMAIL,
                            SeguridadFields.Autenticacion.CONTRASENA);
                });
    }

    @Test
    void debeReportarAmbosComoRequeridos_cuandoLosDosEstanEnBlanco() {
        // Act / Assert
        assertThatThrownBy(() -> AutenticacionDomain.crear("   ", "   "))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(
                                SeguridadCodes.Autenticacion.EMAIL_REQUERIDO,
                                SeguridadCodes.Autenticacion.CONTRASENA_REQUERIDA));
    }

    @Test
    void debeNoSeguirValidandoElFormato_cuandoElCorreoEstaEnBlanco() {
        // Act / Assert — un solo error por campo, no "requerido" mas "formato invalido"
        assertThatThrownBy(() -> AutenticacionDomain.crear(null, CLAVE_VALIDA))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .hasSize(1)
                        .extracting("codigoError")
                        .containsExactly(SeguridadCodes.Autenticacion.EMAIL_REQUERIDO));
    }

    @Test
    void debeNoExponerLaClave_cuandoSeImprimeElObjeto() {
        // Arrange
        var autenticacion = AutenticacionDomain.crear(CORREO_VALIDO, CLAVE_VALIDA);

        // Act / Assert — al ser clase y no record, no hereda un toString con los campos
        assertThat(autenticacion.toString()).doesNotContain(CLAVE_VALIDA);
    }
}
