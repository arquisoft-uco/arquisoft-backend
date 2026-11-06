package com.arquisoft.shared.webclient;

import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.client.TrazaClientHttpRequestInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FabricaClienteHttpTest {

    @Test
    void debePropagarLaCorrelacion() {
        // Arrange
        var gestor = mock(GestorTraza.class);
        when(gestor.correlacionActual()).thenReturn("corr-123");
        var fabrica = new FabricaClienteHttp(new TrazaClientHttpRequestInterceptor(gestor));
        var builder = RestClient.builder();
        var servidor = MockRestServiceServer.bindTo(builder).build();
        var cliente = fabrica.construir(builder,
                new ConfiguracionClienteHttp("http://destino.test", Duration.ofSeconds(1), Duration.ofSeconds(1)));
        servidor.expect(requestTo("http://destino.test/eco")).andExpect(header("X-Correlation-Id", "corr-123"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        // Act
        var respuesta = cliente.ejecutar(
                new SolicitudHttp(MetodoHttp.GET, "/eco", Map.of(), null, null), Map.class);

        // Assert
        assertThat(respuesta.estado()).isEqualTo(200);
        servidor.verify();
    }
}
