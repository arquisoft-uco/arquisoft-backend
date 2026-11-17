package com.arquisoft.shared.amqp;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.beans.DirectFieldAccessor;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitListenerConfigTest {

    private static SimpleMessageListenerContainer contenedor(String concurrencia) {
        var config = new RabbitListenerConfig();
        var campos = new DirectFieldAccessor(config);
        campos.setPropertyValue("concurrentConsumers", 5);
        campos.setPropertyValue("maxConcurrentConsumers", 10);
        campos.setPropertyValue("prefetch", 1);
        var fabrica = config.rabbitListenerContainerFactory(
                new CachingConnectionFactory());
        var endpoint = new SimpleRabbitListenerEndpoint();
        endpoint.setId("consumidor-de-prueba");
        endpoint.setQueueNames("cola-de-prueba");
        endpoint.setMessageListener(mensaje -> { });
        endpoint.setConcurrency(concurrencia);
        return fabrica.createListenerContainer(endpoint);
    }

    @Test
    void debeFijarUnSoloConsumidorYMaximoUno_cuandoElEndpointUsaUnSoloConsumidor() {
        // Act
        var contenedor = contenedor(RabbitListenerConfig.UN_SOLO_CONSUMIDOR);

        // Assert
        var campos = new DirectFieldAccessor(contenedor);
        assertThat(RabbitListenerConfig.UN_SOLO_CONSUMIDOR).isEqualTo("1-1");
        assertThat(campos.getPropertyValue("concurrentConsumers")).isEqualTo(1);
        assertThat(campos.getPropertyValue("maxConcurrentConsumers")).isEqualTo(1);
    }

    @Test
    void debeConservarElMaximoDeLaFabrica_cuandoElEndpointSoloFijaUnEntero() {
        // Act
        var contenedor = contenedor("1");

        // Assert
        var campos = new DirectFieldAccessor(contenedor);
        assertThat(campos.getPropertyValue("concurrentConsumers")).isEqualTo(1);
        assertThat(campos.getPropertyValue("maxConcurrentConsumers")).isEqualTo(10);
    }
}
