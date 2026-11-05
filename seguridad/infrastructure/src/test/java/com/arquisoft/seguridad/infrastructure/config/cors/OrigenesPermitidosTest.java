package com.arquisoft.seguridad.infrastructure.config.cors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrigenesPermitidosTest {

    @Test
    void debeParsearYNormalizarLaLista_cuandoTraeBarraFinalYMayusculas() {
        // Arrange
        var origenes = new OrigenesPermitidos("http://localhost:3000/, HTTP://Localhost:4200 ");

        // Act / Assert
        assertThat(origenes.lista()).containsExactly("http://localhost:3000", "http://localhost:4200");
        assertThat(origenes.contiene("HTTP://LOCALHOST:3000/")).isTrue();
        assertThat(origenes.contiene("http://localhost:5173")).isFalse();
        assertThat(origenes.contiene(null)).isFalse();
    }

    @Test
    void debeIgnorarVaciosYEspacios_cuandoLaListaTraeElementosVacios() {
        // Arrange
        var conVacios = new OrigenesPermitidos(" , ,http://localhost:3000,,");
        var vacia = new OrigenesPermitidos("");

        // Act / Assert
        assertThat(conVacios.lista()).containsExactly("http://localhost:3000");
        assertThat(vacia.lista()).isEmpty();
    }
}
