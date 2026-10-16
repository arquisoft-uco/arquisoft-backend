package com.arquisoft.repositorio_artefactos.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositorioArtefactosAlmacenamientoPropertiesTest {

    private static RepositorioArtefactosAlmacenamientoProperties propiedades(String bucket, long maximo) {
        var propiedades = new RepositorioArtefactosAlmacenamientoProperties();
        propiedades.setBucket(bucket);
        propiedades.setTamanoMaximoBytes(maximo);
        return propiedades;
    }

    @Test
    void debeAceptar_cuandoBucketYMaximoSonValidos() {
        // Act & Assert
        assertThatCode(() -> propiedades("repositorio-artefactos", 5_242_880L).afterPropertiesSet())
                .doesNotThrowAnyException();
    }

    @Test
    void debeFallarElArranque_cuandoElMaximoNoEsPositivo() {
        // Act & Assert
        assertThatThrownBy(() -> propiedades("repositorio-artefactos", 0L).afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeFallarElArranque_cuandoNoHayBucket() {
        // Act & Assert
        assertThatThrownBy(() -> propiedades(" ", 10L).afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> propiedades(null, 10L).afterPropertiesSet())
                .isInstanceOf(IllegalStateException.class);
    }
}
