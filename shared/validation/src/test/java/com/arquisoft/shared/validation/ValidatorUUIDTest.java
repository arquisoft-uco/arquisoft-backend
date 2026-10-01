package com.arquisoft.shared.validation;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ValidadorKey;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorUUIDTest {

    private static final String CAMPO = "identificador";
    private static final String CODIGO = "CODIGO_ERROR";

    @Test
    void debeAcumularError_cuandoUUIDTieneFormatoInvalido() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorUUID.uuidValido("no-es-uuid", CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isFalse();
        assertThat(result.tieneErrores()).isTrue();
        assertThat(result.getErrores().get(0).mensaje())
                .isEqualTo(Mensajes.formatear(ValidadorKey.UUID_INVALIDO, CAMPO));
    }

    @Test
    void debePasar_cuandoUUIDTieneFormatoValido() {
        // Arrange
        var result = new ValidationResult();

        // Act
        boolean valido = ValidatorUUID.uuidValido(UUID.randomUUID().toString(), CAMPO, CODIGO, result);

        // Assert
        assertThat(valido).isTrue();
        assertThat(result.tieneErrores()).isFalse();
    }
}
