package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.keycloak;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.ModificacionIdentidadEntity;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

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

// Split de KeycloakProveedorIdentidadOutputAdapterTest: la clase original pasaba las 500 líneas
// de Checkstyle (FileLength) al sumar los casos de HU-257. Comparten adaptador y fixtures propios,
// sin duplicar los tests de registrar/eliminar/revocar que ya cubre el archivo original.
@ExtendWith(MockitoExtension.class)
class KeycloakProveedorIdentidadOutputAdapterModificacionTest {

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
    void debeRetornarTrue_cuandoElEmailExisteEnOtraIdentidad() {
        // Arrange
        stubToken();
        var propio = UUID.randomUUID();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("/users?exact=true&email=")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>>any()))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", UUID.randomUUID().toString()))));

        // Act & Assert
        assertThat(adapter.existeEmailEnOtraIdentidad("ana@uco.edu.co", propio)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElEmailSoloPerteneceAlPropioUsuario() {
        // Arrange
        stubToken();
        var propio = UUID.randomUUID();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.contains("/users?exact=true&email=")), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<List<Map<String, Object>>>>any()))
                .thenReturn(ResponseEntity.ok(List.of(Map.of("id", propio.toString()))));

        // Act & Assert
        assertThat(adapter.existeEmailEnOtraIdentidad("ana@uco.edu.co", propio)).isFalse();
    }

    @Test
    void debeEnviarPutParcial_cuandoActualizaLaIdentidad() {
        // Arrange
        stubToken();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + usuario)), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(ResponseEntity.ok(Map.of("email", "viejo@uco.edu.co",
                        "firstName", "Viejo", "lastName", "Nombre")));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + usuario)), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act & Assert
        assertThatCode(() -> adapter.actualizar(
                new ModificacionIdentidadEntity(usuario, "nuevo@uco.edu.co", "Nuevo", "Apellido")))
                .doesNotThrowAnyException();
        verify(restTemplate, times(1)).exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + usuario)), eq(HttpMethod.PUT),
                argThat((HttpEntity<?> entidad) -> entidad.getBody() instanceof Map<?, ?> cuerpo
                        && "nuevo@uco.edu.co".equals(cuerpo.get("email"))),
                eq(Void.class));
    }

    @Test
    void debeLanzarNoDisponible_cuandoActualizarFalla() {
        // Arrange
        stubToken();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + usuario)), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.actualizar(
                new ModificacionIdentidadEntity(usuario, "nuevo@uco.edu.co", "Nuevo", "Apellido")))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeRestaurarLaIdentidadPrevia_cuandoLaTransaccionHaceRollbackTrasActualizar() {
        // Arrange
        stubToken();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + usuario)), eq(HttpMethod.GET),
                any(HttpEntity.class), ArgumentMatchers.<org.springframework.core.ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(ResponseEntity.ok(Map.of("email", "viejo@uco.edu.co",
                        "firstName", "Viejo", "lastName", "Nombre")));
        when(restTemplate.exchange(
                argThat((String url) -> url != null && url.endsWith("/users/" + usuario)), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        TransactionSynchronizationManager.initSynchronization();
        try {
            adapter.actualizar(new ModificacionIdentidadEntity(usuario, "nuevo@uco.edu.co", "Nuevo", "Apellido"));
            var sincronizaciones = TransactionSynchronizationManager.getSynchronizations();

            // Act
            sincronizaciones.forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

            // Assert — el PUT se envía dos veces: la actualización y la restauración del valor previo
            verify(restTemplate, times(2)).exchange(
                    argThat((String url) -> url != null && url.endsWith("/users/" + usuario)), eq(HttpMethod.PUT),
                    any(HttpEntity.class), eq(Void.class));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void noDebeLlamarAKeycloak_cuandoAsignarRealmRolesRecibeListaVacia() {
        // Act & Assert
        assertThatCode(() -> adapter.asignarRealmRoles(UUID.randomUUID(), List.of())).doesNotThrowAnyException();
        verify(restTemplate, never()).exchange(any(String.class), any(HttpMethod.class),
                any(HttpEntity.class), eq(Void.class));
    }

    @Test
    void debeAsignarLosRealmRoles_cuandoLaListaTraeRoles() {
        // Arrange
        stubToken();
        stubRolEstudiante();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.POST), any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act & Assert
        assertThatCode(() -> adapter.asignarRealmRoles(usuario, List.of("estudiante")))
                .doesNotThrowAnyException();
        verify(restTemplate, times(1)).exchange(eq(urlRoleMappings(usuario)), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(Void.class));
    }
}
