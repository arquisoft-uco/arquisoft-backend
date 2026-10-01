package com.arquisoft.shared.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorNumeroTest {

    private static final String CAMPO = "nota";
    private static final String CODIGO = "CODIGO_ERROR";

    @Test
    void debeAcumularError_cuandoNoAlcanzaElValorMinimo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorNumero.valorMinimo(2, 5, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).mensaje()).contains(CAMPO, "5");
    }

    @Test
    void debeAcumularError_cuandoSuperaElValorMaximo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorNumero.valorMaximo(11, 10, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).mensaje()).contains(CAMPO, "10");
    }

    @Test
    void debeAcumularUnSoloErrorConElRango_cuandoQuedaFueraDelRango() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorNumero.valorEntre(0.5, 1, 5, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores()).hasSize(1);
        assertThat(result.getErrores().get(0).mensaje()).contains(CAMPO, "1", "5");
    }

    @Test
    void debePasar_cuandoElValorCaeEnElBordeDelRango() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorNumero.valorEntre(5, 1, 5, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }

    @Test
    void debePasar_cuandoElValorEsNulo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorNumero.valorEntre(null, 1, 5, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }
}
