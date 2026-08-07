package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UtilUUIDTest {

    @Test
    void debeAceptarUUID_cuandoEstaEnMinusculas() {
        assertThat(UtilUUID.uuidStringIsValid("550e8400-e29b-41d4-a716-446655440000")).isTrue();
    }

    @Test
    void debeAceptarUUID_cuandoEstaEnMayusculas() {
        assertThat(UtilUUID.uuidStringIsValid("550E8400-E29B-41D4-A716-446655440000")).isTrue();
    }

    @Test
    void debeAceptarUUID_cuandoMezclaMayusculasYMinusculas() {
        assertThat(UtilUUID.uuidStringIsValid("550e8400-E29B-41d4-A716-446655440000")).isTrue();
    }

    @Test
    void debeRechazarValor_cuandoNoTieneFormatoUUID() {
        assertThat(UtilUUID.uuidStringIsValid("no-es-uuid")).isFalse();
        assertThat(UtilUUID.uuidStringIsValid("")).isFalse();
        assertThat(UtilUUID.uuidStringIsValid(null)).isFalse();
    }

    @Test
    void debeConvertirAUUID_cuandoFormatoEsValido() {
        UUID esperado = UUID.randomUUID();

        assertThat(UtilUUID.generateUUIDFromString(esperado.toString())).isEqualTo(esperado);
    }

    @Test
    void debeRetornarNull_cuandoFormatoEsInvalido() {
        assertThat(UtilUUID.generateUUIDFromString("no-es-uuid")).isNull();
    }
}
