package com.arquisoft.seguridad.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests unitarios para el enum {@link EstadoUsuario}.
 *
 * <p>Capa: domain — Java puro, sin Spring, sin Mockito.
 * Verifica la resolución desde código ({@code fromCode}) y el lanzamiento
 * de {@link IllegalArgumentException} ante un código desconocido.
 */
class EstadoUsuarioTest {

    @Test
    void debeResolverDesdeCode_cuandoCodeEsValido() {
        // Arrange / Act / Assert — asserts consolidados en un solo test

        // fromCode("ACTIVO") retorna ACTIVO
        assertThat(EstadoUsuario.fromCode("ACTIVO"))
                .isEqualTo(EstadoUsuario.ACTIVO);

        // fromCode("INACTIVO") retorna INACTIVO
        assertThat(EstadoUsuario.fromCode("INACTIVO"))
                .isEqualTo(EstadoUsuario.INACTIVO);

        // code inválido lanza IllegalArgumentException
        assertThatThrownBy(() -> EstadoUsuario.fromCode("DESCONOCIDO"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DESCONOCIDO");
    }
}
