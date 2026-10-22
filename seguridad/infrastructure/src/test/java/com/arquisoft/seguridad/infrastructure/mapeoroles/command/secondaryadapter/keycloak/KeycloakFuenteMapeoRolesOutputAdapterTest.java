package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.model.ObtencionMapeoRoles;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class KeycloakFuenteMapeoRolesOutputAdapterTest {

    private static final String BASE = "http://kc.test";
    private static final String UUID_CLIENTE = "cffeb896-738c-4000-80cf-8cf99e05233f";
    private static final String SECRETO = "secreto-de-la-cuenta-de-servicio";
    private static final String URL_TOKEN = BASE + "/realms/arquisoft/protocol/openid-connect/token";
    private static final String URL_ROLES = BASE + "/admin/realms/arquisoft/roles?first=0&max=1000";
    private static final String URL_COMPOSITES = BASE + "/admin/realms/arquisoft/roles/%s/composites/clients/" + UUID_CLIENTE;
    private static final String TOKEN_JSON = "{\"access_token\":\"tok-servicio\"}";

    private MockRestServiceServer server;
    private AppLogger logger;
    private KeycloakFuenteMapeoRolesOutputAdapter adaptador;

    @BeforeEach
    void preparar() {
        var restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();
        logger = mock(AppLogger.class);
        var propiedades = new KeycloakFuenteMapeoRolesProperties(BASE + "/", "arquisoft", "arquisoft-api", SECRETO,
                UUID_CLIENTE, Duration.ofSeconds(2), Duration.ofSeconds(3));
        adaptador = new KeycloakFuenteMapeoRolesOutputAdapter(restTemplate, propiedades, logger);
    }

    private void esperarToken() {
        server.expect(once(), requestTo(URL_TOKEN))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("grant_type=client_credentials")))
                .andRespond(withSuccess(TOKEN_JSON, MediaType.APPLICATION_JSON));
    }

    private void esperarRoles(String json) {
        server.expect(once(), requestTo(URL_ROLES))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer tok-servicio"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }

    private void esperarComposites(String rol, String json) {
        server.expect(once(), requestTo(URL_COMPOSITES.formatted(rol)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }

    @Test
    void debeObtenerElMapeo_cuandoTodoResponde() {
        // Arrange
        esperarToken();
        esperarRoles("[{\"name\":\"asesor\"},{\"name\":\"offline_access\"}]");
        esperarComposites("asesor", "[{\"name\":\"fichas:ver\"},{\"name\":\"fichas:editar\"}]");
        esperarComposites("offline_access", "[]");

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isInstanceOf(ObtencionMapeoRoles.Obtenida.class);
        var mapeo = ((ObtencionMapeoRoles.Obtenida) resultado).mapeo().clientRolesPorRolRealm();
        assertThat(mapeo).containsOnlyKeys("asesor", "offline_access");
        assertThat(mapeo.get("asesor")).containsExactlyInAnyOrder("fichas:ver", "fichas:editar");
        assertThat(mapeo.get("offline_access")).isEmpty();
        server.verify();
    }

    @Test
    void debeDevolverNoDisponible_cuandoElTokenDa401() {
        // Arrange
        server.expect(once(), requestTo(URL_TOKEN)).andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        // Act / Assert
        assertThat(adaptador.obtener()).isEqualTo(new ObtencionMapeoRoles.NoDisponible("401"));
    }

    @Test
    void debeDevolverNoDisponible_cuandoLaRespuestaNoTraeToken() {
        // Arrange
        server.expect(once(), requestTo(URL_TOKEN)).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));

        // Act / Assert
        assertThat(adaptador.obtener()).isInstanceOf(ObtencionMapeoRoles.NoDisponible.class);
    }

    @Test
    void debeDevolverNoDisponible_cuandoComposites403() {
        // Arrange
        esperarToken();
        esperarRoles("[{\"name\":\"asesor\"}]");
        server.expect(once(), requestTo(URL_COMPOSITES.formatted("asesor"))).andRespond(withStatus(HttpStatus.FORBIDDEN));

        // Act / Assert
        assertThat(adaptador.obtener()).isEqualTo(new ObtencionMapeoRoles.NoDisponible("403"));
    }

    @Test
    void debeDevolverNoDisponible_cuandoComposites404() {
        // Arrange
        esperarToken();
        esperarRoles("[{\"name\":\"asesor\"}]");
        server.expect(once(), requestTo(URL_COMPOSITES.formatted("asesor"))).andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Act / Assert
        assertThat(adaptador.obtener()).isEqualTo(new ObtencionMapeoRoles.NoDisponible("404"));
    }

    @Test
    void debeDevolverNoDisponible_cuandoNoHayRoles() {
        // Arrange
        esperarToken();
        esperarRoles("[]");

        // Act / Assert
        assertThat(adaptador.obtener()).isInstanceOf(ObtencionMapeoRoles.NoDisponible.class);
    }

    @Test
    void debeDevolverNoDisponible_cuandoNingunRolOtorgaClientRoles() {
        // Arrange
        esperarToken();
        esperarRoles("[{\"name\":\"asesor\"},{\"name\":\"estudiante\"}]");
        esperarComposites("asesor", "[]");
        esperarComposites("estudiante", "[]");

        // Act / Assert
        assertThat(adaptador.obtener()).isInstanceOf(ObtencionMapeoRoles.NoDisponible.class);
    }

    @Test
    void debeDevolverNoDisponible_cuandoHayTimeout() {
        // Arrange
        server.expect(once(), requestTo(URL_TOKEN)).andRespond(request -> {
            throw new SocketTimeoutException("Read timed out");
        });

        // Act
        var resultado = adaptador.obtener();

        // Assert
        assertThat(resultado).isEqualTo(new ObtencionMapeoRoles.NoDisponible("ResourceAccessException"));
    }

    @Test
    void debeNoRegistrarElSecreto() throws IOException {
        // Arrange
        server.expect(once(), requestTo(URL_TOKEN)).andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        var captor = ArgumentCaptor.forClass(Object.class);

        // Act
        var resultado = adaptador.obtener();

        // Assert
        verify(logger).error(any(ClaveMensaje.class), any(Throwable.class), captor.capture(), captor.capture());
        assertThat(captor.getAllValues()).noneMatch(valor -> String.valueOf(valor).contains(SECRETO));
        assertThat(resultado.toString()).doesNotContain(SECRETO);
    }
}
