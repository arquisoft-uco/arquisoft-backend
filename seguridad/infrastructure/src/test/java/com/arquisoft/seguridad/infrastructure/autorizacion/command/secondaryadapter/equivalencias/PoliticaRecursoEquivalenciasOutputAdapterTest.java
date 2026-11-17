package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.SoloAutenticacionProperties.Entrada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PoliticaRecursoEquivalenciasOutputAdapterTest {

    private PoliticaRecursoEquivalenciasOutputAdapter adaptador;

    @BeforeEach
    void preparar() {
        adaptador = new PoliticaRecursoEquivalenciasOutputAdapter(
                new SoloAutenticacionProperties(List.of(new Entrada("GET", "/api/usuarios/mi/perfil"))));
    }

    @Test
    void debeExigirSoloAutenticacion_cuandoMetodoYRecursoEstanListados() {
        // Act
        var resultado = adaptador.exigeSoloAutenticacion("GET", "/api/usuarios/mi/perfil");

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeNormalizarElMetodo_cuandoLlegaEnMinusculas() {
        // Act
        var resultado = adaptador.exigeSoloAutenticacion("get", "/api/usuarios/mi/perfil");

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void noDebeExigirla_cuandoElRecursoOElMetodoNoEstanListados() {
        // Act & Assert
        assertThat(adaptador.exigeSoloAutenticacion("GET", "/api/usuarios/otro")).isFalse();
        assertThat(adaptador.exigeSoloAutenticacion("DELETE", "/api/usuarios/mi/perfil")).isFalse();
    }

    @Test
    void noDebeExigirla_cuandoNoHayRecursosListados() {
        // Arrange
        var vacio = new PoliticaRecursoEquivalenciasOutputAdapter(new SoloAutenticacionProperties(null));

        // Act
        var resultado = vacio.exigeSoloAutenticacion("GET", "/api/usuarios/mi/perfil");

        // Assert
        assertThat(resultado).isFalse();
    }
}
