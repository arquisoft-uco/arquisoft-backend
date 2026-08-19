package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.keycloak;

import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.infrastructure.exception.CredencialesInvalidasException;
import com.arquisoft.seguridad.infrastructure.exception.ProveedorIdentidadNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.exception.TokenInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

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

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
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
        CredencialesSesion credenciales = adapter.autenticar("estudiante@uco.edu.co", "password123");

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
    void debeLanzarCredencialesInvalidasException_cuandoKeycloakRetorna401() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(HttpClientErrorException.Unauthorized.create(
                        HttpStatus.UNAUTHORIZED, "Unauthorized", null, null, null));

        // Act + Assert
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "wrong-password"))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessageContaining("Credenciales invalidas");
    }

    @Test
    void debeLanzarAuthenticationException_cuandoKeycloakRetornaOtroError4xx() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request"));

        // Act + Assert
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "password"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Error al comunicarse con Keycloak");
    }

    @Test
    void debeLanzarProveedorNoDisponibleException_cuandoTimeoutRed() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(new ResourceAccessException("Connection timeout"));

        // Act + Assert
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "password"))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class)
                .hasMessageContaining("Servicio de autenticacion no disponible temporalmente");
    }

    @Test
    void debeLanzarAuthenticationException_cuandoExcepcionInesperada() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act + Assert
        assertThatThrownBy(() -> adapter.autenticar("estudiante@uco.edu.co", "password"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Error inesperado durante la autenticacion");
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
        CredencialesSesion credenciales = adapter.refrescar("eyJhbGc-refresh-old...");

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
                .hasMessageContaining("Refresh token invalido o expirado");
    }

    @Test
    void debeLanzarProveedorNoDisponibleException_cuandoRefreshYRedNoDisponible() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(new ResourceAccessException("Network error"));

        // Act + Assert
        assertThatThrownBy(() -> adapter.refrescar("token-refresco"))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class)
                .hasMessageContaining("Servicio de autenticacion no disponible temporalmente");
    }

    @Test
    void debeRetornarTrue_cuandoValidarTokenRefrescoExitoso() {
        // Arrange
        Map<String, Object> keycloakResponse = Map.of(
                "access_token", "eyJhbGc...",
                "refresh_token", "eyJhbGc-refresh...",
                "expires_in", 3600
        );
        ResponseEntity<Map<String, Object>> responseEntity = ResponseEntity.ok(keycloakResponse);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenReturn(responseEntity);

        // Act
        boolean valido = adapter.validarTokenRefresco("token-refresco-valido");

        // Assert
        assertThat(valido).isTrue();
    }

    @Test
    void debeRetornarFalso_cuandoValidarTokenRefrescoFalla() {
        // Arrange
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq((Class<Map<String, Object>>) (Class<?>) Map.class)))
                .thenThrow(HttpClientErrorException.BadRequest.create(
                        HttpStatus.BAD_REQUEST, "Bad Request", null, null, null));

        // Act
        boolean valido = adapter.validarTokenRefresco("token-refresco-invalido");

        // Assert
        assertThat(valido).isFalse();
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
        CredencialesSesion credenciales = adapter.autenticar("test@uco.edu.co", "password");

        // Assert - valores por defecto aplicados
        assertThat(credenciales.expiraEn()).isEqualTo(3600L);
        assertThat(credenciales.tipoToken()).isEqualTo("Bearer");
        assertThat(credenciales.alcance()).isEmpty();
    }
}
