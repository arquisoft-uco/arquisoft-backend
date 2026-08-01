package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.AppMessages;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DomainValidatorTest {

    private static final String CAMPO = "identificador";
    private static final String CODIGO = "CODIGO_ERROR";

    @Test
    void debeAcumularError_cuandoUUIDTieneFormatoInvalido() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.validUUID("no-es-uuid", CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getErrors().get(0).message())
                .isEqualTo(AppMessages.DomainValidator.VALID_UUID.formatted(CAMPO));
    }

    @Test
    void debePasar_cuandoUUIDTieneFormatoValido() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.validUUID(UUID.randomUUID().toString(), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoColeccionEstaVacia() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.notEmpty(new ArrayList<>(), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrors().get(0).errorCode()).isEqualTo(CODIGO);
    }

    @Test
    void debeAcumularError_cuandoColeccionSuperaElMaximo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.maxSize(List.of("a", "b", "c"), 2, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrors().get(0).message())
                .isEqualTo(AppMessages.DomainValidator.MAX_SIZE.formatted(CAMPO, 2));
    }

    @Test
    void debePasar_cuandoColeccionEsNulaEnMaxSize() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.maxSize(null, 2, CAMPO, CODIGO, result);

        assertThat(valido).isTrue();
        assertThat(result.hasErrors()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoColeccionTieneDuplicados() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.sinDuplicados(List.of("a", "b", "a"), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrors().get(0).message())
                .isEqualTo(AppMessages.DomainValidator.SIN_DUPLICADOS.formatted(CAMPO, "a"));
    }

    @Test
    void debePasar_cuandoColeccionNoTieneDuplicados() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.sinDuplicados(List.of("a", "b"), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.hasErrors()).isFalse();
    }
}
