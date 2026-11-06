package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.config.cors.OrigenesPermitidos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ProteccionCsrfTest {

    private final ProteccionCsrf proteccion =
            new ProteccionCsrf(new OrigenesPermitidos("http://localhost:3000,http://localhost:5174"));

    @ParameterizedTest
    @ValueSource(strings = {"GET", "HEAD", "OPTIONS", "TRACE", "get"})
    void debePermitir_cuandoMetodoSeguro(String metodo) {
        // Act
        var resultado = proteccion.evaluar(metodo, null, null, null);

        // Assert
        assertThat(resultado).isEqualTo(ProteccionCsrf.Resultado.PERMITIDA);
    }

    @Test
    void debeRechazar_cuandoFaltaXsrfOEstaVacio() {
        // Act
        var nulo = proteccion.evaluar("POST", null, "http://localhost:3000", null);
        var vacio = proteccion.evaluar("DELETE", "  ", "http://localhost:3000", null);

        // Assert
        assertThat(nulo).isEqualTo(ProteccionCsrf.Resultado.SIN_CABECERA_XSRF);
        assertThat(vacio).isEqualTo(ProteccionCsrf.Resultado.SIN_CABECERA_XSRF);
    }

    @Test
    void debeRechazar_cuandoSinOrigenNiReferer() {
        // Act
        var sinNada = proteccion.evaluar("POST", "tok", null, null);
        var refererInvalido = proteccion.evaluar("POST", "tok", null, "no es una url");
        var refererRelativo = proteccion.evaluar("POST", "tok", null, "/ruta/relativa");

        // Assert
        assertThat(sinNada).isEqualTo(ProteccionCsrf.Resultado.SIN_ORIGEN);
        assertThat(refererInvalido).isEqualTo(ProteccionCsrf.Resultado.SIN_ORIGEN);
        assertThat(refererRelativo).isEqualTo(ProteccionCsrf.Resultado.SIN_ORIGEN);
    }

    @Test
    void debePermitir_cuandoOrigenONormalizadoOReferer() {
        // Act
        var origen = proteccion.evaluar("POST", "tok", "http://localhost:3000", null);
        var normalizado = proteccion.evaluar("PUT", "tok", "HTTP://localhost:5174/", null);
        var referer = proteccion.evaluar("PATCH", "tok", null, "http://localhost:5174/formulario?x=1");
        var origenPrevalece = proteccion.evaluar("POST", "tok", "http://localhost:3000", "http://malo.test/x");

        // Assert
        assertThat(origen).isEqualTo(ProteccionCsrf.Resultado.PERMITIDA);
        assertThat(normalizado).isEqualTo(ProteccionCsrf.Resultado.PERMITIDA);
        assertThat(referer).isEqualTo(ProteccionCsrf.Resultado.PERMITIDA);
        assertThat(origenPrevalece).isEqualTo(ProteccionCsrf.Resultado.PERMITIDA);
    }

    @Test
    void debeRechazar_cuandoOrigenNoPermitido() {
        // Act
        var ajeno = proteccion.evaluar("POST", "tok", "http://malo.test", null);
        var puertoDistinto = proteccion.evaluar("POST", "tok", "http://localhost:9999", null);
        var refererAjeno = proteccion.evaluar("POST", "tok", null, "http://malo.test/pagina");
        var nulo = proteccion.evaluar("POST", "tok", "null", null);

        // Assert
        assertThat(ajeno).isEqualTo(ProteccionCsrf.Resultado.ORIGEN_NO_PERMITIDO);
        assertThat(puertoDistinto).isEqualTo(ProteccionCsrf.Resultado.ORIGEN_NO_PERMITIDO);
        assertThat(refererAjeno).isEqualTo(ProteccionCsrf.Resultado.ORIGEN_NO_PERMITIDO);
        assertThat(nulo).isEqualTo(ProteccionCsrf.Resultado.ORIGEN_NO_PERMITIDO);
    }
}
