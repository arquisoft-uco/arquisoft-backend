package com.arquisoft.biblioteca.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BibliotecaEnlacesPropertiesTest {

    private static BibliotecaEnlacesProperties propiedades(String urlBase, String ruta) {
        var propiedades = new BibliotecaEnlacesProperties();
        propiedades.setUrlBase(urlBase);
        propiedades.setRutaDocumentacion(ruta);
        return propiedades;
    }

    @Test
    void debeNoLanzar_cuandoAmbasPropiedadesTienenValor() {
        // Arrange
        var propiedades = propiedades("https://front.test", "/doc/{documentacionId}");

        // Act & Assert
        assertThatCode(propiedades::afterPropertiesSet).doesNotThrowAnyException();
    }

    @Test
    void debeFallar_cuandoUrlBaseEstaEnBlancoONula() {
        // Arrange
        var enBlanco = propiedades("  ", "/doc/{documentacionId}");
        var nula = propiedades(null, "/doc/{documentacionId}");

        // Act & Assert
        assertThatThrownBy(enBlanco::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(nula::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeFallar_cuandoRutaDocumentacionEstaEnBlancoONula() {
        // Arrange
        var enBlanco = propiedades("https://front.test", "");
        var nula = propiedades("https://front.test", null);

        // Act & Assert
        assertThatThrownBy(enBlanco::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(nula::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }
}
