package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.keycloak;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.seguridad.application.auth.exception.AutenticacionException;
import com.arquisoft.seguridad.application.auth.exception.CredencialesInvalidasException;
import com.arquisoft.seguridad.infrastructure.auth.exception.ProveedorIdentidadNoDisponibleException;
import com.arquisoft.seguridad.application.auth.exception.TokenInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakAuthOutputAdapterTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private KeycloakAuthOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        // Los campos @Value no se inyectan en tests unitarios sin contexto Spring
        // Usar ReflectionTestUtils para setear los valores manualmente
        ReflectionTestUtils.setField(adapter, "keycloakServerUrl", "http://localhost:8180");
        ReflectionTestUtils.setField(adapter, "realm", "arquisoft");
        ReflectionTestUtils.setField(adapter, "clientId", "arquisoft-backend");
        ReflectionTestUtils.setField(adapter, "clientSecret", "test-secret");
    }

    @Test
    void debeAutenticar_cuandoCredencialesValidas() {
        // Arrange
        Map<String, Object> keycloakResponse = Map.of(
                "access_token", "eyJhbGc...",
                "refresh_token", "eyJhbGc-refresh...",
                "expires_in", 3600,
                "token_type", "Bearer",
                "scope", "openid profile email"
        );
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(keycloakResponse);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenReturn(responseEntity);

        // Act
        var credenciales = adapter.autenticar("estudiante@uco.edu.co", "password123");

        // Assert
        assertThat(credenciales).isNotNull();
        assertThat(credenciales.tokenAcceso()).isEqualTo("eyJhbGc...");
        assertThat(credenciales.tokenRefresco()).isEqualTo("eyJhbGc-refresh...");
        assertThat(credenciales.expiraEn()).isEqualTo(3600L);
        assertThat(credenciales.tipoToken()).isEqualTo("Bearer");
        assertThat(credenciales.alcance()).isEqualTo("openid profile email");

        verify(restTemplate).postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class));
    }

    @Test
    void debeLanzarCredencialesInvalidasException_cuandoKeycloakRespondeInvalidGrant() {
        // Arrange — Keycloak (OAuth2 RFC 6749 §5.2) responde 400 invalid_grant tanto para
        // usuario como para contraseña incorrectos: el mismo error para ambos casos.
        var cuerpoKeycloak = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid user credentials\"}"
                .getBytes(StandardCharsets.UTF_8);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST, "Bad Request", HttpHeaders.EMPTY, cuerpoKeycloak, StandardCharsets.UTF_8));

        // Act + Assert
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "wrong-password"))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessageContaining("Credenciales inválidas");
    }

    @Test
    void debeNoFiltrarUrlNiCuerpoDeKeycloak_cuandoCredencialInvalida() {
        // Arrange — el texto que arma RestTemplate incluye la URL interna del IdP y el cuerpo OIDC crudo
        var cuerpoKeycloak = "{\"error\":\"invalid_grant\",\"error_description\":\"Invalid user credentials\"}"
                .getBytes(StandardCharsets.UTF_8);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request on POST \"https://auth.arquisoft.top/realms/arquisoft/protocol/openid-connect/token\"",
                        HttpHeaders.EMPTY, cuerpoKeycloak, StandardCharsets.UTF_8));

        // Act + Assert — al cliente solo le llega el texto genérico
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "wrong-password"))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessageContaining("Credenciales inválidas")
                .hasMessageNotContaining("arquisoft.top")
                .hasMessageNotContaining("openid-connect")
                .hasMessageNotContaining("invalid_grant");
    }

    @Test
    void debeLanzarAutenticacionExceptionGenerica_cuandoKeycloakRetorna4xxSinInvalidGrant() {
        // Arrange — un 4xx que NO es invalid_grant (p.ej. el client del backend mal configurado)
        var cuerpoKeycloak = "{\"error\":\"invalid_client\",\"error_description\":\"Invalid client credentials\"}"
                .getBytes(StandardCharsets.UTF_8);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request on POST \"https://auth.arquisoft.top/realms/arquisoft/protocol/openid-connect/token\"",
                        HttpHeaders.EMPTY, cuerpoKeycloak, StandardCharsets.UTF_8));

        // Act + Assert — mensaje genérico, sin la URL ni el cuerpo de Keycloak
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "password"))
                .isInstanceOf(AutenticacionException.class)
                .isNotInstanceOf(CredencialesInvalidasException.class)
                .hasMessageContaining("No fue posible completar la autenticación")
                .hasMessageNotContaining("arquisoft.top")
                .hasMessageNotContaining("invalid_client");
    }

    @Test
    void debeLanzarProveedorNoDisponibleException_cuandoTimeoutRed() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(new ResourceAccessException("Connection timeout"));

        // Act + Assert
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "password"))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class)
                .hasMessageContaining("Servicio de autenticación no disponible temporalmente");
    }

    @Test
    void debePropagarExcepcionInesperada_cuandoFallaAlgoNoPrevisto() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(new IllegalStateException("Defecto de mapeo"));

        // Act + Assert — un defecto no se disfraza de fallo de autenticacion: debe salir como 500
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "password"))
                .isInstanceOf(IllegalStateException.class)
                .isNotInstanceOf(AutenticacionException.class);
    }

    @Test
    void debeLanzarProveedorNoDisponibleException_cuandoKeycloakRetorna5xx() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Keycloak caido"));

        // Act + Assert
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "password"))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class);
    }

    @Test
    void debeRefrescar_cuandoTokenRefrescoValido() {
        // Arrange
        Map<String, Object> keycloakResponse = Map.of(
                "access_token", "eyJhbGc-new...",
                "refresh_token", "eyJhbGc-refresh-new...",
                "expires_in", 3600,
                "token_type", "Bearer",
                "scope", "openid profile email"
        );
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(keycloakResponse);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenReturn(responseEntity);

        // Act
        var credenciales = adapter.refrescar("eyJhbGc-refresh-old...");

        // Assert
        assertThat(credenciales).isNotNull();
        assertThat(credenciales.tokenAcceso()).isEqualTo("eyJhbGc-new...");
        assertThat(credenciales.tokenRefresco()).isEqualTo("eyJhbGc-refresh-new...");
        assertThat(credenciales.expiraEn()).isEqualTo(3600L);
    }

    @Test
    void debeLanzarTokenInvalidoException_cuandoRefreshTokenInvalido400() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(HttpClientErrorException.BadRequest.create(
                        HttpStatus.BAD_REQUEST, "Bad Request", null, null, null));

        // Act + Assert
        assertThatThrownBy(() -> adapter.refrescar("invalid-refresh-token"))
                .isInstanceOf(TokenInvalidoException.class)
                .hasMessageContaining("Refresh token inválido o expirado");
    }

    @Test
    void debeLanzarProveedorNoDisponibleException_cuandoRefreshYRedNoDisponible() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        // Act + Assert
        assertThatThrownBy(() -> adapter.refrescar("token-refresco"))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class)
                .hasMessageContaining("Servicio de autenticación no disponible temporalmente");
    }

    @Test
    void debeConstruirBodyConClientSecret_cuandoClientSecretPresente() {
        // Arrange
        Map<String, Object> keycloakResponse = Map.of(
                "access_token", "eyJhbGc...",
                "refresh_token", "eyJhbGc-refresh...",
                "expires_in", 3600
        );
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(keycloakResponse);

        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.postForEntity(anyString(), captor.capture(), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenReturn(responseEntity);

        // Act
        adapter.autenticar("test@uco.edu.co", "password");

        // Assert
        HttpEntity<MultiValueMap<String, String>> capturedEntity = captor.getValue();
        MultiValueMap<String, String> body = capturedEntity.getBody();

        assertThat(body).isNotNull();
        assertThat(body.getFirst("client_secret")).isEqualTo("test-secret");
        assertThat(body.getFirst("grant_type")).isEqualTo("password");
        assertThat(body.getFirst("client_id")).isEqualTo("arquisoft-backend");
    }

    @Test
    void debeMapearCredencialesConValoresPorDefecto_cuandoFaltanCamposOpcionales() {
        // Arrange - response sin scope ni token_type
        Map<String, Object> keycloakResponse = Map.of(
                "access_token", "eyJhbGc...",
                "refresh_token", "eyJhbGc-refresh..."
        );
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(keycloakResponse);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenReturn(responseEntity);

        // Act
        var credenciales = adapter.autenticar("test@uco.edu.co", "password");

        // Assert - valores por defecto aplicados
        assertThat(credenciales.expiraEn()).isEqualTo(3600L);
        assertThat(credenciales.tipoToken()).isEqualTo("Bearer");
        assertThat(credenciales.alcance()).isEmpty();
    }
}
