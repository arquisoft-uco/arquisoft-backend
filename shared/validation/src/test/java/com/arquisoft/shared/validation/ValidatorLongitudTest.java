package com.arquisoft.shared.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorLongitudTest {

    private static final String CAMPO = "titulo";
    private static final String CODIGO = "CODIGO_ERROR";

    @Test
    void debeAcumularError_cuandoSuperaLaLongitudMaxima() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorLongitud.longitudMaxima("abcdef", 3, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).mensaje()).contains(CAMPO, "3");
    }

    @Test
    void debeAcumularError_cuandoNoAlcanzaLaLongitudMinima() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorLongitud.longitudMinima("ab", 5, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).mensaje()).contains(CAMPO, "5");
    }

    @Test
    void debeAcumularUnSoloErrorConElRango_cuandoQuedaPorDebajoDelMinimo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorLongitud.longitudEntre("ab", 3, 10, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores()).hasSize(1);
        assertThat(result.getErrores().get(0).mensaje()).contains(CAMPO, "3", "10");
    }

    @Test
    void debeAcumularUnSoloErrorConElRango_cuandoSuperaElMaximo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorLongitud.longitudEntre("abcdefghijk", 3, 10, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores()).hasSize(1);
        assertThat(result.getErrores().get(0).mensaje()).contains(CAMPO, "3", "10");
    }

    @Test
    void debePasar_cuandoLaLongitudCaeDentroDelRango() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorLongitud.longitudEntre("abcde", 3, 10, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }

    @Test
    void debeIgnorarLosEspacios_cuandoMideLaLongitud() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorLongitud.longitudEntre("   abc   ", 3, 3, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }
}
