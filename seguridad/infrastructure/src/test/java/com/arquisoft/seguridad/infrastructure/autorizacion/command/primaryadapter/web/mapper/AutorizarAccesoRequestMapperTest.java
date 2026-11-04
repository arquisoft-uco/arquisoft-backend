package com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class AutorizarAccesoRequestMapperTest {

    @Test
    void debeArmarElComando_cuandoLlegaLaPeticion() {
        // Arrange
        var request = new MockHttpServletRequest("POST", "/api/x");

        // Act
        var command = AutorizarAccesoRequestMapper.toCommand(request, "/api/x", "principal", "tok", "corr");

        // Assert
        assertThat(command.metodo()).isEqualTo("POST");
        assertThat(command.recurso()).isEqualTo("/api/x");
        assertThat(command.emisor()).isEqualTo("principal");
        assertThat(command.credencial()).isEqualTo("tok");
        assertThat(command.correlacion()).isEqualTo("corr");
    }
}
