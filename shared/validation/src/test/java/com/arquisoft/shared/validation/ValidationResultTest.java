package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.constant.AppCodes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationResultTest {

    @Test
    void debeDevolverTextoVacio_cuandoNoHayErrores() {
        // Arrange
        var result = new ValidationResult();

        // Act & Assert
        assertThat(result.describirErrores()).isEmpty();
    }

    @Test
    void debeUnirLosErroresConSeparador_cuandoHayVarios() {
        // Arrange
        var result = new ValidationResult();
        result.agregarError("titulo", "TITULO_REQUERIDO", "obligatorio");
        result.agregarError("asesor", "ASESOR_REQUERIDO", "no válido");

        // Act
        String descripcion = result.describirErrores();

        // Assert
        assertThat(descripcion).isEqualTo(
                "[TITULO_REQUERIDO] titulo: obligatorio | [ASESOR_REQUERIDO] asesor: no válido");
    }

    @Test
    void debeLlevarElCodigoDeDominio_cuandoLanzaPorInvarianteDeDominio() {
        // Arrange
        var result = new ValidationResult();
        result.agregarError("titulo", "TITULO_REQUERIDO", "obligatorio");

        // Act & Assert
        assertThatThrownBy(result::lanzarSiTieneErrores)
                .isInstanceOf(DomainValidationException.class)
                .hasMessage("[TITULO_REQUERIDO] titulo: obligatorio")
                .extracting(e -> ((DomainValidationException) e).getCodigoError())
                .isEqualTo(AppCodes.Validacion.DOMINIO);
    }

    @Test
    void debeLlevarElCodigoDeAplicacion_cuandoLanzaPorIntegridadDeEntrada() {
        // Arrange
        var result = new ValidationResult();
        result.agregarError("asesor", "ASESOR_INVALIDO", "formato invalido");

        // Act & Assert
        assertThatThrownBy(result::lanzarSiTieneErroresDeEntrada)
                .isInstanceOf(ApplicationValidationException.class)
                .extracting(e -> ((ApplicationValidationException) e).getCodigoError())
                .isEqualTo(AppCodes.Validacion.APLICACION);
    }

    @Test
    void noDebeLanzar_cuandoNoHayErrores() {
        // Arrange
        var result = new ValidationResult();

        // Act & Assert
        result.lanzarSiTieneErrores();
        result.lanzarSiTieneErroresDeEntrada();
        assertThat(result.tieneErrores()).isFalse();
    }
}
