package com.arquisoft.biblioteca.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BibliotecaAlmacenamientoPropertiesTest {

    @Test
    void debeAceptar_cuandoElBucketEstaInformado() {
        // Arrange
        var propiedades = new BibliotecaAlmacenamientoProperties();
        propiedades.setBucketEntregables("entregables");

        // Act & Assert
        assertThatCode(propiedades::afterPropertiesSet).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    void debeFallarElArranque_cuandoElBucketFaltaOEstaEnBlanco(String bucket) {
        // Arrange
        var propiedades = new BibliotecaAlmacenamientoProperties();
        propiedades.setBucketEntregables(bucket);

        // Act & Assert
        assertThatThrownBy(propiedades::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }
}
