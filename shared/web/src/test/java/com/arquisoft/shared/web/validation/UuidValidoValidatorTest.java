package com.arquisoft.shared.web.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UuidValidoValidatorTest {

    private UuidValidoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UuidValidoValidator();
    }

    @Test
    void debeSerValido_cuandoValorEsNull() {
        // La obligatoriedad la decide @NotNull/@NotBlank, no esta constraint
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void debeSerValido_cuandoTieneFormatoUUID() {
        assertThat(validator.isValid(UUID.randomUUID().toString(), null)).isTrue();
    }

    @Test
    void debeSerInvalido_cuandoNoTieneFormatoUUID() {
        assertThat(validator.isValid("no-es-uuid", null)).isFalse();
        assertThat(validator.isValid("", null)).isFalse();
    }
}
