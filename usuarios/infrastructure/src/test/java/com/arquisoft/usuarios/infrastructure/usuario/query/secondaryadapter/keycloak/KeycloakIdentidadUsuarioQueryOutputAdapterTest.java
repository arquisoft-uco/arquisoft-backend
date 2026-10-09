package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.keycloak;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.ProveedorIdentidadKey;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakIdentidadUsuarioQueryOutputAdapterTest {

    private static final String SERVER_URL = "http://kc";
    private static final String REALM = "arquisoft";
    private static final String TOKEN_URL = SERVER_URL + "/realms/" + REALM + "/protocol/openid-connect/token";
    private static final String TOKEN = "token-123";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppLogger logger;

    private KeycloakIdentidadUsuarioQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new KeycloakIdentidadUsuarioQueryOutputAdapter(logger, restTemplate);
        ReflectionTestUtils.setField(adapter, "serverUrl", SERVER_URL);
        ReflectionTestUtils.setField(adapter, "realm", REALM);
        ReflectionTestUtils.setField(adapter, "clientId", "arquisoft-api");
        ReflectionTestUtils.setField(adapter, "clientSecret", "secret");
    }

    @Test
    void debeRetornarNombresYApellidosConBearerYUrlDelUsuario_cuandoKeycloakResponde() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        stubToken();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.GET), any(HttpEntity.class), tipoMapa()))
                .thenReturn(ResponseEntity.ok(Map.of("firstName", "Ana María", "lastName", "Ramírez Díaz")));

        // Act
        var resultado = adapter.consultar(new IdentidadUsuarioCriteria(usuario));

        // Assert
        assertThat(resultado.nombres()).isEqualTo("Ana María");
        assertThat(resultado.apellidos()).isEqualTo("Ramírez Díaz");
        var peticion = forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(urlUsuario(usuario)), eq(HttpMethod.GET), peticion.capture(), tipoMapa());
        assertThat(peticion.getValue().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer " + TOKEN);
    }

    @Test
    void noDebeEnviarClientSecret_cuandoNoEstaConfigurado() {
        // Arrange
        ReflectionTestUtils.setField(adapter, "clientSecret", null);
        var usuario = UtilUUID.generarNuevoUUID();
        stubToken();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.GET), any(HttpEntity.class), tipoMapa()))
                .thenReturn(ResponseEntity.ok(Map.of("firstName", "Ana", "lastName", "Ramírez")));

        // Act
        adapter.consultar(new IdentidadUsuarioCriteria(usuario));

        // Assert
        var peticion = forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(TOKEN_URL), eq(HttpMethod.POST), peticion.capture(), tipoMapa());
        @SuppressWarnings("unchecked")
        var formulario = (MultiValueMap<String, String>) peticion.getValue().getBody();
        assertThat(formulario).containsKey("client_id").doesNotContainKey("client_secret");
    }

    @Test
    void debeLanzar503YLoguearNoEncontrada_cuandoKeycloakDa404() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        stubToken();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.GET), any(HttpEntity.class), tipoMapa()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(new IdentidadUsuarioCriteria(usuario)))
                .isInstanceOfSatisfying(ProveedorIdentidadUsuarioNoDisponibleException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(UsuariosCodes.Usuario.IDP_NO_DISPONIBLE));
        verify(logger).error(ProveedorIdentidadKey.LOG_IDENTIDAD_NO_ENCONTRADA, usuario);
    }

    @Test
    void debeLanzar503_cuandoKeycloakDa5xx() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        stubToken();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.GET), any(HttpEntity.class), tipoMapa()))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(new IdentidadUsuarioCriteria(usuario)))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        verify(logger).error(eq(RegistrarUsuarioKey.LOG_IDP_ERROR), eq(HttpStatus.INTERNAL_SERVER_ERROR), eq("caido"));
    }

    @Test
    void debeLanzar503_cuandoFallaElTransporte() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        stubToken();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.GET), any(HttpEntity.class), tipoMapa()))
                .thenThrow(new ResourceAccessException("timeout"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(new IdentidadUsuarioCriteria(usuario)))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        verify(logger).error(eq(RegistrarUsuarioKey.LOG_IDP_ERROR), eq("cliente-http"), eq("timeout"));
    }

    @Test
    void debeLanzar503SinLeerElUsuario_cuandoElTokenNoLlega() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(restTemplate.exchange(eq(TOKEN_URL), eq(HttpMethod.POST), any(HttpEntity.class), tipoMapa()))
                .thenReturn(ResponseEntity.ok(Map.of()));

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(new IdentidadUsuarioCriteria(usuario)))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        verify(restTemplate, never()).exchange(eq(urlUsuario(usuario)), eq(HttpMethod.GET),
                any(HttpEntity.class), tipoMapa());
    }

    @Test
    void debeLanzar503_cuandoElCuerpoDelUsuarioEsNulo() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        stubToken();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.GET), any(HttpEntity.class), tipoMapa()))
                .thenReturn(new ResponseEntity<Map<String, Object>>(HttpStatus.OK));

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(new IdentidadUsuarioCriteria(usuario)))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    private void stubToken() {
        when(restTemplate.exchange(eq(TOKEN_URL), eq(HttpMethod.POST), any(HttpEntity.class), tipoMapa()))
                .thenReturn(ResponseEntity.ok(Map.<String, Object>of("access_token", TOKEN)));
    }

    private static String urlUsuario(UUID usuario) {
        return SERVER_URL + "/admin/realms/" + REALM + "/users/" + usuario;
    }

    private static ParameterizedTypeReference<Map<String, Object>> tipoMapa() {
        return any();
    }
}
