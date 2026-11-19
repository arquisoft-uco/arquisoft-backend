package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.config;

import com.arquisoft.evaluaciones.infrastructure.config.EvaluacionesFinalizacionProperties;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ConfiguracionFinalizacionPropertiesAdapterTest {

    @Test
    void debeEntregarPesosIgualesAUno_cuandoLaConfiguracionNoSeSobreescribe() {
        // Arrange
        var adapter = new ConfiguracionFinalizacionPropertiesAdapter(new EvaluacionesFinalizacionProperties());

        // Act
        var pesos = adapter.obtenerPesos();

        // Assert
        assertThat(pesos.pesoAsesor()).isEqualByComparingTo("1");
        assertThat(pesos.pesoJurado()).isEqualByComparingTo("1");
    }

    @Test
    void debeEntregarLosPesosConfigurados_cuandoSeSobreescriben() {
        // Arrange
        var properties = new EvaluacionesFinalizacionProperties();
        properties.setPesoAsesor(new BigDecimal("2"));
        properties.setPesoJurado(new BigDecimal("3"));
        var adapter = new ConfiguracionFinalizacionPropertiesAdapter(properties);

        // Act
        var pesos = adapter.obtenerPesos();

        // Assert
        assertThat(pesos.pesoAsesor()).isEqualByComparingTo("2");
        assertThat(pesos.pesoJurado()).isEqualByComparingTo("3");
    }
}
