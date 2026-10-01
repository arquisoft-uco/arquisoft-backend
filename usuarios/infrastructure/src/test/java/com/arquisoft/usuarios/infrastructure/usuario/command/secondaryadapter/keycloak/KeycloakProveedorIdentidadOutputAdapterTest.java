package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.keycloak;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.ProveedorIdentidadKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakProveedorIdentidadOutputAdapterTest {

    private static final String SERVER_URL = "http://kc";
    private static final String REALM = "arquisoft";
    private static final String TOKEN_URL = SERVER_URL + "/realms/" + REALM + "/protocol/openid-connect/token";

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AppLogger logger;

    private KeycloakProveedorIdentidadOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new KeycloakProveedorIdentidadOutputAdapter(logger, restTemplate);
        ReflectionTestUtils.setField(adapter, "serverUrl", SERVER_URL);
        ReflectionTestUtils.setField(adapter, "realm", REALM);
        ReflectionTestUtils.setField(adapter, "clientId", "arquisoft-api");
        ReflectionTestUtils.setField(adapter, "clientSecret", "secret");
    }

    private void stubToken() {
        when(restTemplate.exchange(
                eq(TOKEN_URL), eq(HttpMethod.POST), any(HttpEntity.class),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(ResponseEntity.ok(Map.of("access_token", "token-123")));
    }

    private RegistroIdentidadEntity registro(List<String> roles) {
        return new RegistroIdentidadEntity("ana@uco.edu.co", "Ana", "Pérez", roles);
    }

    @Test
    void debeRetornarTrue_cuandoElEmailExisteEnElIdp() {
        // Arrange
        stubToken();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("/users?exact=true&email=")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>>any()))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", "u1"))));

        // Act & Assert
        assertThat(adapter.existeEmail("ana@uco.edu.co")).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElEmailNoExisteEnElIdp() {
        // Arrange
        stubToken();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("/users?exact=true&email=")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>>any()))
                .thenReturn(ResponseEntity.ok(List.of()));

        // Act & Assert
        assertThat(adapter.existeEmail("ana@uco.edu.co")).isFalse();
    }

    @Test
    void debeLanzarNoDisponible_cuandoExisteEmailFalla() {
        // Arrange
        stubToken();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("/users?exact=true&email=")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>>any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.existeEmail("ana@uco.edu.co"))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeRegistrarSinRoles_cuandoListaDeRolesVacia() {
        // Arrange
        stubToken();
        var idAsignado = UUID.randomUUID();
        var location = URI.create(SERVER_URL + "/admin/realms/" + REALM + "/users/" + idAsignado);
        var headers = new HttpHeaders();
        headers.setLocation(location);
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(headers, HttpStatus.CREATED));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("execute-actions-email")), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act
        var resultado = adapter.registrar(registro(List.of()));

        // Assert
        assertThat(resultado).isEqualTo(idAsignado);
        verify(restTemplate, times(0)).exchange(
                argThat((String url) -> url != null && url.contains("role-mappings")), any(HttpMethod.class),
                any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void debeAsignarRealmRoles_cuandoLaListaTraeRoles() {
        // Arrange
        stubToken();
        var idAsignado = UUID.randomUUID();
        var location = URI.create(SERVER_URL + "/admin/realms/" + REALM + "/users/" + idAsignado);
        var headers = new HttpHeaders();
        headers.setLocation(location);
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(headers, HttpStatus.CREATED));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("/roles/estudiante")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", "role-id-1", "name", "estudiante")));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("role-mappings/realm")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("execute-actions-email")), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act
        var resultado = adapter.registrar(registro(List.of("estudiante")));

        // Assert
        assertThat(resultado).isEqualTo(idAsignado);
        verify(restTemplate, times(1)).exchange(
                argThat((String url) -> url != null && url.contains("role-mappings/realm")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void debeLanzarNoDisponible_cuandoCrearUsuarioFalla() {
        // Arrange
        stubToken();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.registrar(registro(List.of())))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
        verify(restTemplate, times(0)).exchange(
                argThat((String url) -> url != null && url.contains("execute-actions-email")), any(HttpMethod.class),
                any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void debeLanzarNoDisponible_cuandoLocationAusente() {
        // Arrange
        stubToken();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(new HttpHeaders(), HttpStatus.CREATED));

        // Act & Assert
        assertThatThrownBy(() -> adapter.registrar(registro(List.of())))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoElRolNoExisteEnKeycloak() {
        // Arrange
        stubToken();
        var idAsignado = UUID.randomUUID();
        var location = URI.create(SERVER_URL + "/admin/realms/" + REALM + "/users/" + idAsignado);
        var headers = new HttpHeaders();
        headers.setLocation(location);
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(headers, HttpStatus.CREATED));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("/roles/inexistente")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        // Act & Assert
        assertThatThrownBy(() -> adapter.registrar(registro(List.of("inexistente"))))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoRoleMappingFalla() {
        // Arrange
        stubToken();
        var idAsignado = UUID.randomUUID();
        var location = URI.create(SERVER_URL + "/admin/realms/" + REALM + "/users/" + idAsignado);
        var headers = new HttpHeaders();
        headers.setLocation(location);
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(headers, HttpStatus.CREATED));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("/roles/estudiante")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", "role-id-1", "name", "estudiante")));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("role-mappings/realm")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.registrar(registro(List.of("estudiante"))))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoExecuteActionsEmailFalla() {
        // Arrange
        stubToken();
        var idAsignado = UUID.randomUUID();
        var location = URI.create(SERVER_URL + "/admin/realms/" + REALM + "/users/" + idAsignado);
        var headers = new HttpHeaders();
        headers.setLocation(location);
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users")), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(headers, HttpStatus.CREATED));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("execute-actions-email")), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(Void.class)))
                .thenThrow(HttpClientErrorException.BadRequest.create(
                        HttpStatus.BAD_REQUEST, "User is disabled", null, null, null));

        // Act & Assert
        assertThatThrownBy(() -> adapter.registrar(registro(List.of())))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoElTokenDeServicioFallaPorTimeout() {
        // Arrange
        when(restTemplate.exchange(
                eq(TOKEN_URL), eq(HttpMethod.POST), any(HttpEntity.class),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenThrow(new ResourceAccessException("timeout"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.registrar(registro(List.of())))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeEliminarSinLanzar_cuandoElIdpConfirma204() {
        // Arrange
        stubToken();
        var id = UUID.randomUUID();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + id)), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act & Assert
        assertThatCode(() -> adapter.eliminar(id)).doesNotThrowAnyException();
        verify(restTemplate, times(1)).exchange(
                eq(TOKEN_URL), eq(HttpMethod.POST), any(HttpEntity.class),
                ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any());
    }

    @Test
    void debeIgnorar404_cuandoElUsuarioYaNoExisteAlEliminar() {
        // Arrange
        stubToken();
        var id = UUID.randomUUID();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + id)), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(Void.class)))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        // Act & Assert
        assertThatCode(() -> adapter.eliminar(id)).doesNotThrowAnyException();
    }

    @Test
    void debeRegistrarErrorSinLanzar_cuandoLaCompensacionFalla() {
        // Arrange
        stubToken();
        var id = UUID.randomUUID();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + id)), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(Void.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));

        // Act & Assert
        assertThatCode(() -> adapter.eliminar(id)).doesNotThrowAnyException();
        verify(logger, times(1)).error(any(com.arquisoft.shared.message.ClaveMensaje.class), eq(id.toString()));
    }

    private void stubRolEstudiante() {
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/roles/estudiante")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(ResponseEntity.ok(Map.of("id", "role-id-1", "name", "estudiante")));
    }

    private String urlRoleMappings(UUID usuario) {
        return SERVER_URL + "/admin/realms/" + REALM + "/users/" + usuario + "/role-mappings/realm";
    }

    @Test
    void debeRevocarElRealmRoleConSuRepresentacion_cuandoKeycloakResponde() {
        // Arrange
        stubToken();
        stubRolEstudiante();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.DELETE), any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act & Assert
        assertThatCode(() -> adapter.revocarRealmRole(usuario, "estudiante")).doesNotThrowAnyException();
        verify(restTemplate, times(1)).exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.DELETE),
                argThat((HttpEntity<?> entidad) -> entidad.getBody() instanceof List<?> cuerpo
                        && cuerpo.size() == 1
                        && ((Map<?, ?>) cuerpo.get(0)).get("name").equals("estudiante")),
                eq(Void.class));
        verify(restTemplate, never()).exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void debeLanzarNoDisponible_cuandoElUsuarioNoExisteEnKeycloakAlRevocar() {
        // Arrange
        stubToken();
        stubRolEstudiante();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.DELETE), any(HttpEntity.class),
                eq(Void.class)))
                .thenThrow(HttpClientErrorException.NotFound.create(
                        HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        // Act & Assert
        assertThatThrownBy(() -> adapter.revocarRealmRole(usuario, "estudiante"))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeLanzarNoDisponible_cuandoKeycloakNoRespondeAlRevocar() {
        // Arrange
        stubToken();
        stubRolEstudiante();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.DELETE), any(HttpEntity.class),
                eq(Void.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.revocarRealmRole(usuario, "estudiante"))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeReasignarElRol_cuandoLaTransaccionHaceRollbackTrasRevocar() {
        // Arrange
        stubToken();
        stubRolEstudiante();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlRoleMappings(usuario)), any(HttpMethod.class), any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        TransactionSynchronizationManager.initSynchronization();
        try {
            adapter.revocarRealmRole(usuario, "estudiante");
            var sincronizaciones = TransactionSynchronizationManager.getSynchronizations();

            // Act
            sincronizaciones.forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

            // Assert
            assertThat(sincronizaciones).hasSize(1);
            verify(restTemplate, times(1)).exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.POST),
                    any(HttpEntity.class), eq(Void.class));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void noDebeReasignar_cuandoLaTransaccionHaceCommit() {
        // Arrange
        stubToken();
        stubRolEstudiante();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.DELETE), any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        TransactionSynchronizationManager.initSynchronization();
        try {
            adapter.revocarRealmRole(usuario, "estudiante");

            // Act
            TransactionSynchronizationManager.getSynchronizations()
                    .forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_COMMITTED));

            // Assert
            verify(restTemplate, never()).exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.POST),
                    any(HttpEntity.class), eq(Void.class));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void debeRegistrarErrorSinLanzar_cuandoLaCompensacionDelRolFalla() {
        // Arrange
        stubToken();
        stubRolEstudiante();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.DELETE), any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        when(restTemplate.exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(Void.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));
        TransactionSynchronizationManager.initSynchronization();
        try {
            adapter.revocarRealmRole(usuario, "estudiante");
            var sincronizaciones = TransactionSynchronizationManager.getSynchronizations();

            // Act & Assert
            assertThatCode(() -> sincronizaciones.forEach(
                    s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK)))
                    .doesNotThrowAnyException();
            verify(logger, times(1)).error(ProveedorIdentidadKey.LOG_COMPENSACION_ROL_FALLIDA, usuario, "estudiante");
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
