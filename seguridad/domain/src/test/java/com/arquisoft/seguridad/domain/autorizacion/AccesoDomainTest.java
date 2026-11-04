package com.arquisoft.seguridad.domain.autorizacion;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.constant.SeguridadFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccesoDomainTest {

    @Test
    void debeCrearRecortandoEspacios_cuandoDatosValidos() {
        // Act
        var acceso = AccesoDomain.crear(" GET ", " /api/fichas-perfil ", " token ", " principal ", " corr-1 ");

        // Assert
        assertThat(acceso.getMetodo()).isEqualTo("GET");
        assertThat(acceso.getRecurso()).isEqualTo("/api/fichas-perfil");
        assertThat(acceso.getCredencial()).isEqualTo("token");
        assertThat(acceso.getEmisor()).isEqualTo("principal");
        assertThat(acceso.getCorrelacion()).isEqualTo("corr-1");
    }

    @Test
    void debeAceptarCorrelacionVacia_cuandoNoSeInforma() {
        // Act
        var acceso = AccesoDomain.crear("GET", "/api/x", "token", "principal", null);

        // Assert
        assertThat(acceso.getCorrelacion()).isNullOrEmpty();
    }

    @Test
    void debeAcumularFieldErrors_cuandoVariosCamposInvalidos() {
        // Act / Assert
        assertThatThrownBy(() -> AccesoDomain.crear(" ", null, "  ", "x".repeat(101), null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("codigoError").containsExactlyInAnyOrder(
                            SeguridadCodes.Autorizacion.ACCESO_METODO_REQUERIDO,
                            SeguridadCodes.Autorizacion.ACCESO_RECURSO_REQUERIDO,
                            SeguridadCodes.Autorizacion.ACCESO_CREDENCIAL_REQUERIDA,
                            SeguridadCodes.Autorizacion.ACCESO_EMISOR_EXCEDE_LIMITE);
                    assertThat(errores).extracting("campo").containsExactlyInAnyOrder(
                            SeguridadFields.Autorizacion.METODO,
                            SeguridadFields.Autorizacion.RECURSO,
                            SeguridadFields.Autorizacion.CREDENCIAL,
                            SeguridadFields.Autorizacion.EMISOR);
                });
    }

    @Test
    void debeRechazarConCodigoDeLongitud_cuandoMetodoRecursoYCredencialSuperanElLimite() {
        // Act / Assert
        assertThatThrownBy(() -> AccesoDomain.crear("G".repeat(11), "r".repeat(2049), "c".repeat(8193),
                "principal", null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(
                                SeguridadCodes.Autorizacion.ACCESO_METODO_EXCEDE_LIMITE,
                                SeguridadCodes.Autorizacion.ACCESO_RECURSO_EXCEDE_LIMITE,
                                SeguridadCodes.Autorizacion.ACCESO_CREDENCIAL_EXCEDE_LIMITE));
    }

    @Test
    void debeRechazar_cuandoLaCorrelacionSuperaElLimite() {
        // Act / Assert
        assertThatThrownBy(() -> AccesoDomain.crear("GET", "/api/x", "token", "principal", "c".repeat(129)))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactly(SeguridadCodes.Autorizacion.ACCESO_CORRELACION_EXCEDE_LIMITE));
    }
}
