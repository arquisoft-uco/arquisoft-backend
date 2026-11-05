package com.arquisoft.shared.webclient.impl;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ClienteHttpKey;
import com.arquisoft.shared.webclient.MetodoHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;
import com.arquisoft.shared.webclient.TipoContenido;
import com.arquisoft.shared.webclient.exception.ClienteHttpTransporteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ClienteHttpRestClientTest {

    private static final String BASE = "http://destino.test";

    record Eco(String valor) {}

    private MockRestServiceServer servidor;
    private ClienteHttpRestClient cliente;

    @BeforeEach
    void preparar() {
        var builder = RestClient.builder().baseUrl(BASE);
        servidor = MockRestServiceServer.bindTo(builder).build();
        cliente = new ClienteHttpRestClient(builder.build());
    }

    @Test
    void debeDevolverEstadoYCuerpo_cuandoRespondeJson() {
        // Arrange
        servidor.expect(requestTo(BASE + "/eco")).andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"valor\":\"a\"}"))
                .andRespond(withSuccess("{\"valor\":\"b\"}", MediaType.APPLICATION_JSON));
        var solicitud = new SolicitudHttp(MetodoHttp.POST, "/eco", Map.of(), new Eco("a"), TipoContenido.JSON);

        // Act
        var respuesta = cliente.ejecutar(solicitud, Eco.class);

        // Assert
        assertThat(respuesta.estado()).isEqualTo(200);
        assertThat(respuesta.cuerpo()).isEqualTo(new Eco("b"));
        assertThat(respuesta.esExitosa()).isTrue();
    }

    @Test
    void debeDevolverElEstado_cuandoRespondeError4xx5xx() {
        // Arrange
        servidor.expect(requestTo(BASE + "/eco")).andRespond(withStatus(HttpStatus.UNPROCESSABLE_CONTENT));
        servidor.expect(requestTo(BASE + "/eco")).andRespond(withStatus(HttpStatus.BAD_GATEWAY));
        var solicitud = new SolicitudHttp(MetodoHttp.GET, "/eco", null, null, null);

        // Act
        var primera = cliente.ejecutar(solicitud, Eco.class);
        var segunda = cliente.ejecutar(solicitud, Eco.class);

        // Assert
        assertThat(primera.estado()).isEqualTo(422);
        assertThat(primera.cuerpo()).isNull();
        assertThat(segunda.estado()).isEqualTo(502);
        assertThat(segunda.esExitosa()).isFalse();
    }

    @Test
    void debeEnviarFormulario_cuandoElContenidoEsForm() {
        // Arrange
        servidor.expect(requestTo(BASE + "/token")).andExpect(method(HttpMethod.POST))
                .andExpect(header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(content().string("grant_type=client_credentials&client_id=x"))
                .andRespond(withSuccess("{\"valor\":\"t\"}", MediaType.APPLICATION_JSON));
        var formulario = new LinkedHashMap<String, String>();
        formulario.put("grant_type", "client_credentials");
        formulario.put("client_id", "x");
        var solicitud = new SolicitudHttp(MetodoHttp.POST, "/token", Map.of(), formulario, TipoContenido.FORM);

        // Act
        var respuesta = cliente.ejecutar(solicitud, Eco.class);

        // Assert
        assertThat(respuesta.cuerpo().valor()).isEqualTo("t");
        servidor.verify();
    }

    @Test
    void debeEnviarLasCabeceras() {
        // Arrange
        servidor.expect(requestTo(BASE + "/eco")).andExpect(header("Authorization", "Bearer abc"))
                .andRespond(withSuccess("{\"valor\":\"b\"}", MediaType.APPLICATION_JSON));
        var solicitud = new SolicitudHttp(MetodoHttp.GET, "/eco", Map.of("Authorization", "Bearer abc"), null, null);

        // Act
        cliente.ejecutar(solicitud, Eco.class);

        // Assert
        servidor.verify();
    }

    @Test
    void debeLanzarTransporte_cuandoHayTimeout() {
        // Arrange
        servidor.expect(requestTo(BASE + "/eco")).andRespond(request -> {
            throw new SocketTimeoutException("lento");
        });
        var solicitud = new SolicitudHttp(MetodoHttp.GET, "/eco", null, null, null);

        // Act & Assert
        assertThatThrownBy(() -> cliente.ejecutar(solicitud, Eco.class))
                .isInstanceOf(ClienteHttpTransporteException.class)
                .hasRootCauseInstanceOf(IOException.class)
                .satisfies(fallo -> {
                    var excepcion = (ClienteHttpTransporteException) fallo;
                    assertThat(excepcion.getError().getMessage())
                            .isEqualTo(Mensajes.obtener(ClienteHttpKey.ERROR_TRANSPORTE))
                            .doesNotContain("ResourceAccessException");
                    assertThat(excepcion.getError().getDetalle()).isEqualTo("ResourceAccessException");
                });
    }

    @Test
    void debeLanzarTransporte_cuandoElCuerpoEsIlegible() {
        // Arrange
        servidor.expect(requestTo(BASE + "/eco"))
                .andRespond(withSuccess("no es json", MediaType.APPLICATION_JSON));
        var solicitud = new SolicitudHttp(MetodoHttp.GET, "/eco", null, null, null);

        // Act & Assert
        assertThatThrownBy(() -> cliente.ejecutar(solicitud, Eco.class))
                .isInstanceOf(ClienteHttpTransporteException.class);
    }

    @Test
    void debeIgnorarElCuerpo_cuandoElTipoDeRespuestaEsVoid() {
        // Arrange
        servidor.expect(requestTo(BASE + "/eco"))
                .andRespond(withSuccess("{\"valor\":\"b\"}", MediaType.APPLICATION_JSON));
        var solicitud = new SolicitudHttp(MetodoHttp.GET, "/eco", null, null, null);

        // Act
        var respuesta = cliente.ejecutar(solicitud, Void.class);

        // Assert
        assertThat(respuesta.estado()).isEqualTo(200);
        assertThat(respuesta.cuerpo()).isNull();
        assertThat(respuesta.esExitosa()).isTrue();
    }

    @Test
    void debeDevolverCuerpoNulo_cuandoLa2xxNoTraeCuerpo() {
        // Arrange
        servidor.expect(requestTo(BASE + "/eco")).andRespond(withStatus(HttpStatus.NO_CONTENT));
        var solicitud = new SolicitudHttp(MetodoHttp.GET, "/eco", null, null, null);

        // Act
        var respuesta = cliente.ejecutar(solicitud, Eco.class);

        // Assert
        assertThat(respuesta.estado()).isEqualTo(204);
        assertThat(respuesta.cuerpo()).isNull();
    }

    @Test
    void debeUsarElMetodoIndicado_cuandoEsPutODelete() {
        // Arrange
        servidor.expect(requestTo(BASE + "/eco/1")).andExpect(method(HttpMethod.PUT))
                .andExpect(content().json("{\"valor\":\"a\"}"))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        servidor.expect(requestTo(BASE + "/eco/1")).andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.NO_CONTENT));
        var put = new SolicitudHttp(MetodoHttp.PUT, "/eco/1", null, new Eco("a"), TipoContenido.JSON);
        var delete = new SolicitudHttp(MetodoHttp.DELETE, "/eco/1", null, null, null);

        // Act
        var respuestaPut = cliente.ejecutar(put, Void.class);
        var respuestaDelete = cliente.ejecutar(delete, Void.class);

        // Assert
        assertThat(respuestaPut.estado()).isEqualTo(204);
        assertThat(respuestaDelete.estado()).isEqualTo(204);
        servidor.verify();
    }
}
