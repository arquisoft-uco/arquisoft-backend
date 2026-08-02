package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.AppKeys;
import com.arquisoft.shared.message.Messages;
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
        boolean valido = DomainValidator.uuidValido("no-es-uuid", CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.tieneErrores()).isTrue();
        assertThat(result.getErrores().get(0).mensaje())
                .isEqualTo(Messages.formatear(AppKeys.Validador.UUID_INVALIDO, CAMPO));
    }

    @Test
    void debePasar_cuandoUUIDTieneFormatoValido() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.uuidValido(UUID.randomUUID().toString(), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoColeccionEstaVacia() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.noVacia(new ArrayList<>(), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).codigoError()).isEqualTo(CODIGO);
    }

    @Test
    void debeAcumularError_cuandoColeccionSuperaElMaximo() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.tamanioMaximo(List.of("a", "b", "c"), 2, CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).mensaje())
                .isEqualTo(Messages.formatear(AppKeys.Validador.TAMANIO_MAXIMO, CAMPO, 2));
    }

    @Test
    void debePasar_cuandoColeccionEsNulaEnMaxSize() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.tamanioMaximo(null, 2, CAMPO, CODIGO, result);

        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }

    @Test
    void debeAcumularError_cuandoColeccionTieneDuplicados() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.sinDuplicados(List.of("a", "b", "a"), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.getErrores().get(0).mensaje())
                .isEqualTo(Messages.formatear(AppKeys.Validador.SIN_DUPLICADOS, CAMPO, "a"));
    }

    @Test
    void debePasar_cuandoColeccionNoTieneDuplicados() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = DomainValidator.sinDuplicados(List.of("a", "b"), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }
}
