package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorColeccionTest {

    private static final String CAMPO = "identificador";
    private static final String CODIGO = "CODIGO_ERROR";

    @Test
    void debeAcumularError_cuandoColeccionEstaVacia() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorColeccion.noVacia(new ArrayList<>(), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).codigoError()).isEqualTo(CODIGO);
    }

    @Test
    void debeAcumularError_cuandoColeccionSuperaElMaximo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorColeccion.tamanioMaximo(List.of("a", "b", "c"), 2, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).mensaje())
                .isEqualTo(Mensajes.formatear(ValidadorKey.TAMANIO_MAXIMO, CAMPO, 2));
    }

    @Test
    void debePasar_cuandoColeccionEsNulaEnMaxSize() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorColeccion.tamanioMaximo(null, 2, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoColeccionTieneDuplicados() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorColeccion.sinDuplicados(List.of("a", "b", "a"), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).mensaje())
                .isEqualTo(Mensajes.formatear(ValidadorKey.SIN_DUPLICADOS, CAMPO, "a"));
    }

    @Test
    void debePasar_cuandoColeccionNoTieneDuplicados() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorColeccion.sinDuplicados(List.of("a", "b"), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }
}
