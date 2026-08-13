package com.arquisoft.shared.util;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UtilUUIDTest {

    @Test
    void debeAceptarUUID_cuandoEstaEnMinusculas() {
        assertThat(UtilUUID.uuidValido("550e8400-e29b-41d4-a716-446655440000")).isTrue();
    }

    @Test
    void debeAceptarUUID_cuandoEstaEnMayusculas() {
        assertThat(UtilUUID.uuidValido("550E8400-E29B-41D4-A716-446655440000")).isTrue();
    }

    @Test
    void debeAceptarUUID_cuandoMezclaMayusculasYMinusculas() {
        assertThat(UtilUUID.uuidValido("550e8400-E29B-41d4-A716-446655440000")).isTrue();
    }

    @Test
    void debeRechazarValor_cuandoNoTieneFormatoUUID() {
        assertThat(UtilUUID.uuidValido("no-es-uuid")).isFalse();
        assertThat(UtilUUID.uuidValido("")).isFalse();
        assertThat(UtilUUID.uuidValido(null)).isFalse();
    }

    @Test
    void debeConvertirAUUID_cuandoFormatoEsValido() {
        UUID esperado = UUID.randomUUID();

        assertThat(UtilUUID.generarUUIDDesdeTexto(esperado.toString())).isEqualTo(esperado);
    }

    @Test
    void debeRetornarNull_cuandoFormatoEsInvalido() {
        assertThat(UtilUUID.generarUUIDDesdeTexto("no-es-uuid")).isNull();
    }
}
