package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContenidoTest {

    @Test
    void debeNormalizarLosTresCampos_cuandoLleganConEspacios() {
        // Arrange
        var result = new ValidationResult();

        // Act
        Contenido contenido = Contenido.crear("  Asunto  ", "  Cuerpo  ", "  Pie  ", result);

        // Assert
        assertThat(result.getErrores()).isEmpty();
        assertThat(contenido.asunto()).isEqualTo("Asunto");
        assertThat(contenido.cuerpo()).isEqualTo("Cuerpo");
        assertThat(contenido.pie()).isEqualTo("Pie");
    }

    @Test
    void debeAceptarPieAusente_cuandoNoSeSuministra() {
        // Arrange
        var result = new ValidationResult();

        // Act
        Contenido contenido = Contenido.crear("Asunto", "Cuerpo", null, result);

        // Assert
        assertThat(result.getErrores()).isEmpty();
        assertThat(contenido.pie()).isEqualTo(UtilTexto.VACIO);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoFaltanAsuntoYCuerpo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        Contenido contenido = Contenido.crear("  ", null, "Pie", result);

        // Assert
        assertThat(result.getErrores())
                .extracting("codigoError")
                .containsExactlyInAnyOrder(
                        NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO,
                        NotificacionesCodes.Notificacion.CUERPO_REQUERIDO);
        assertThat(contenido.esVacio()).isTrue();
    }

    @Test
    void debeReconstruirSinValidar_cuandoLosDatosVienenDeLaBase() {
        // Act
        Contenido contenido = Contenido.reconstruir(null, null, null);

        // Assert
        assertThat(contenido.esVacio()).isTrue();
        assertThat(contenido.pie()).isEqualTo(UtilTexto.VACIO);
    }

    @Test
    void debeSerVacioElCentinela_cuandoSeConsultaVacio() {
        // Assert
        assertThat(Contenido.VACIO.esVacio()).isTrue();
    }
}
