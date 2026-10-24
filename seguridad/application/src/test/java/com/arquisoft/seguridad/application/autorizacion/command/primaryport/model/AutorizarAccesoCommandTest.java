package com.arquisoft.seguridad.application.autorizacion.command.primaryport.model;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutorizarAccesoCommandTest {

    @Test
    void debeCrearRecortandoEspacios_cuandoDatosValidos() {
        // Act
        var command = AutorizarAccesoCommand.crear(" GET ", " /api/x ", " token ", " principal ", null);

        // Assert
        assertThat(command.metodo()).isEqualTo("GET");
        assertThat(command.recurso()).isEqualTo("/api/x");
        assertThat(command.credencial()).isEqualTo("token");
        assertThat(command.emisor()).isEqualTo("principal");
    }

    @Test
    void debeAcumularErrores_cuandoFaltaCredencialYMetodoSuperaElLimite() {
        // Act / Assert
        assertThatThrownBy(() -> AutorizarAccesoCommand.crear("G".repeat(11), "/api/x", " ", "principal", null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(
                                SeguridadCodes.Autorizacion.ACCESO_METODO_EXCEDE_LIMITE,
                                SeguridadCodes.Autorizacion.ACCESO_CREDENCIAL_REQUERIDA));
    }

    @Test
    void debeRechazar_cuandoLaCorrelacionSuperaElLimite() {
        // Act / Assert
        assertThatThrownBy(() -> AutorizarAccesoCommand.crear("GET", "/api/x", "token", "principal", "c".repeat(129)))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactly(SeguridadCodes.Autorizacion.ACCESO_CORRELACION_EXCEDE_LIMITE));
    }

    @Test
    void debeRechazarConCodigoDeLongitud_cuandoRecursoCredencialYEmisorSuperanElLimite() {
        // Act / Assert
        assertThatThrownBy(() -> AutorizarAccesoCommand.crear("GET", "r".repeat(2049), "c".repeat(8193),
                "e".repeat(101), null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactlyInAnyOrder(
                                SeguridadCodes.Autorizacion.ACCESO_RECURSO_EXCEDE_LIMITE,
                                SeguridadCodes.Autorizacion.ACCESO_CREDENCIAL_EXCEDE_LIMITE,
                                SeguridadCodes.Autorizacion.ACCESO_EMISOR_EXCEDE_LIMITE));
    }

    @Test
    void debeNoExponerLaCredencial_cuandoSeImprimeElCommand() {
        // Arrange
        var command = AutorizarAccesoCommand.crear("GET", "/api/x", "secreto-token", "principal", null);

        // Act / Assert
        assertThat(command.toString()).doesNotContain("secreto-token");
    }
}
