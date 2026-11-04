package com.arquisoft.artefactos.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactosAlmacenamientoPropertiesTest {

    @Test
    void debePasar_cuandoBucketYTamanoValidos() {
        // Arrange
        var propiedades = new ArtefactosAlmacenamientoProperties();
        propiedades.setBucket("artefactos");
        propiedades.setTamanoMaximoBytes(5_242_880L);

        // Act & Assert
        assertThatCode(propiedades::afterPropertiesSet).doesNotThrowAnyException();
    }

    @Test
    void debeFallar_cuandoTamanoCero() {
        // Arrange
        var propiedades = new ArtefactosAlmacenamientoProperties();
        propiedades.setBucket("artefactos");
        propiedades.setTamanoMaximoBytes(0L);

        // Act & Assert
        assertThatThrownBy(propiedades::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeFallar_cuandoBucketVacio() {
        // Arrange
        var propiedades = new ArtefactosAlmacenamientoProperties();
        propiedades.setBucket(" ");
        propiedades.setTamanoMaximoBytes(5_242_880L);

        // Act & Assert
        assertThatThrownBy(propiedades::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }
}
