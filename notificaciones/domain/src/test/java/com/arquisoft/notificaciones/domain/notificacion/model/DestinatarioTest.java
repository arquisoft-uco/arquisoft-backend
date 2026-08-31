package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DestinatarioTest {

    @Test
    void debeNormalizarLosValores_cuandoLleganConEspacios() {
        // Arrange
        var result = new ValidationResult();

        // Act
        Destinatario destinatario =
                Destinatario.crear("  Ana Gomez  ", "  ana.gomez@soyuco.edu.co  ", result);

        // Assert
        assertThat(result.getErrores()).isEmpty();
        assertThat(destinatario.nombre()).isEqualTo("Ana Gomez");
        assertThat(destinatario.email()).isEqualTo("ana.gomez@soyuco.edu.co");
        assertThat(destinatario.esVacio()).isFalse();
    }

    @Test
    void debeAcumularAmbosErrores_cuandoNombreYCorreoFallan() {
        // Arrange
        var result = new ValidationResult();

        // Act
        Destinatario destinatario = Destinatario.crear("  ", "no-es-un-correo", result);

        // Assert
        assertThat(result.getErrores())
                .extracting("codigoError")
                .containsExactlyInAnyOrder(
                        NotificacionesCodes.Notificacion.DESTINATARIO_NOMBRE_REQUERIDO,
                        NotificacionesCodes.Notificacion.DESTINATARIO_INVALIDO);
        assertThat(destinatario.esVacio()).isTrue();
    }

    @Test
    void debeQuedarEnBlancoElCampoInvalido_cuandoSoloFallaUno() {
        // Arrange
        var result = new ValidationResult();

        // Act
        Destinatario destinatario = Destinatario.crear("Ana Gomez", "  ", result);

        // Assert
        assertThat(destinatario.nombre()).isEqualTo("Ana Gomez");
        assertThat(destinatario.email()).isEqualTo(UtilTexto.VACIO);
    }

    @Test
    void debeReconstruirSinValidar_cuandoLosDatosVienenDeLaBase() {
        // Act
        Destinatario destinatario = Destinatario.reconstruir(null, "no-es-un-correo");

        // Assert
        assertThat(destinatario.nombre()).isEqualTo(UtilTexto.VACIO);
        assertThat(destinatario.email()).isEqualTo("no-es-un-correo");
    }

    @Test
    void debeSerVacioElCentinela_cuandoSeConsultaVacio() {
        // Assert
        assertThat(Destinatario.VACIO.esVacio()).isTrue();
    }

    @Test
    void debeMedirLaLongitudSobreElValorRecortado_cuandoLosEspaciosDesbordanElLimite() {
        // Arrange
        var result = new ValidationResult();
        String emailAlLimite = "a".repeat(36) + "@soyuco.edu.co";

        // Act
        Destinatario destinatario =
                Destinatario.crear("Ana Gomez", "   " + emailAlLimite + "   ", result);

        // Assert
        assertThat(result.getErrores()).isEmpty();
        assertThat(destinatario.email()).isEqualTo(emailAlLimite);
    }
}
