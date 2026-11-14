package com.arquisoft.entregables.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntregablesAlmacenamientoPropertiesTest {

    @Test
    void debeAceptar_cuandoElBucketEstaConfigurado() {
        // Arrange
        var propiedades = new EntregablesAlmacenamientoProperties();
        propiedades.setBucket("entregables");
        propiedades.setBucketArtefactos("artefactos");

        // Act & Assert
        assertThatCode(propiedades::afterPropertiesSet).doesNotThrowAnyException();
    }

    @Test
    void debeFallarElArranque_cuandoElBucketFaltaOEstaEnBlanco() {
        // Arrange
        var sinBucket = new EntregablesAlmacenamientoProperties();
        var enBlanco = new EntregablesAlmacenamientoProperties();
        enBlanco.setBucket("  ");
        enBlanco.setBucketArtefactos("artefactos");

        // Act & Assert
        assertThatThrownBy(sinBucket::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(enBlanco::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeFallarElArranque_cuandoElBucketDeArtefactosFaltaOEstaEnBlanco() {
        // Arrange
        var sinOrigen = new EntregablesAlmacenamientoProperties();
        sinOrigen.setBucket("entregables");
        var origenEnBlanco = new EntregablesAlmacenamientoProperties();
        origenEnBlanco.setBucket("entregables");
        origenEnBlanco.setBucketArtefactos("  ");

        // Act & Assert
        assertThatThrownBy(sinOrigen::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(origenEnBlanco::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }
}
