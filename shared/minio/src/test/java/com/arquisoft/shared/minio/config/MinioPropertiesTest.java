package com.arquisoft.shared.minio.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MinioPropertiesTest {

    private static MinioProperties conDescargaMinutos(int minutos) {
        var propiedades = new MinioProperties();
        propiedades.getPresignedUrlExpiry().setDescargaMinutos(minutos);
        return propiedades;
    }

    @Test
    void debeRechazarArranque_cuandoDescargaMinutosEsCero() {
        // Arrange
        var propiedades = conDescargaMinutos(0);

        // Act & Assert
        assertThatThrownBy(propiedades::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeRechazarArranque_cuandoDescargaMinutosSuperaCinco() {
        // Arrange
        var propiedades = conDescargaMinutos(6);

        // Act & Assert
        assertThatThrownBy(propiedades::afterPropertiesSet).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeAceptar_cuandoDescargaMinutosEstaEntreUnoYCinco() {
        // Arrange
        var minimo = conDescargaMinutos(1);
        var maximo = conDescargaMinutos(5);

        // Act & Assert
        assertThatCode(minimo::afterPropertiesSet).doesNotThrowAnyException();
        assertThatCode(maximo::afterPropertiesSet).doesNotThrowAnyException();
    }
}
