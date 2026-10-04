package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.keycloak;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.ProveedorIdentidadKey;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakProveedorIdentidadOutputAdapterHabilitacionTest {

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
                ArgumentMatchers.<ParameterizedTypeReference<Map<String, Object>>>any()))
                .thenReturn(ResponseEntity.ok(Map.of("access_token", "token-123")));
    }

    private String urlUsuario(UUID usuario) {
        return SERVER_URL + "/admin/realms/" + REALM + "/users/" + usuario;
    }

    private static boolean esHabilitacion(HttpEntity<?> entidad, boolean habilitado) {
        return entidad != null && Map.of("enabled", habilitado).equals(entidad.getBody());
    }

    @Test
    void debeEnviarPutConEnabledFalse_cuandoSeDeshabilitaLaIdentidad() {
        // Arrange
        stubToken();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.PUT), any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // Act
        adapter.cambiarHabilitacion(usuario, false);

        // Assert
        verify(restTemplate, times(1)).exchange(eq(urlUsuario(usuario)), eq(HttpMethod.PUT),
                argThat((HttpEntity<?> entidad) -> esHabilitacion(entidad, false)), eq(Void.class));
    }

    @Test
    void debeLanzarNoDisponible_cuandoKeycloakFallaAlCambiarHabilitacion() {
        // Arrange
        stubToken();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.PUT), any(HttpEntity.class),
                eq(Void.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.cambiarHabilitacion(usuario, false))
                .isInstanceOf(ProveedorIdentidadUsuarioNoDisponibleException.class);
    }

    @Test
    void debeRehabilitarLaIdentidad_cuandoLaTransaccionHaceRollbackTrasDeshabilitar() {
        // Arrange
        stubToken();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.PUT), any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        TransactionSynchronizationManager.initSynchronization();
        try {
            adapter.cambiarHabilitacion(usuario, false);
            var sincronizaciones = TransactionSynchronizationManager.getSynchronizations();

            // Act
            sincronizaciones.forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

            // Assert
            assertThat(sincronizaciones).hasSize(1);
            verify(restTemplate, times(1)).exchange(eq(urlUsuario(usuario)), eq(HttpMethod.PUT),
                    argThat((HttpEntity<?> entidad) -> esHabilitacion(entidad, true)), eq(Void.class));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void debeRegistrarErrorSinLanzar_cuandoLaCompensacionDeHabilitacionFalla() {
        // Arrange
        stubToken();
        var usuario = UUID.randomUUID();
        when(restTemplate.exchange(eq(urlUsuario(usuario)), eq(HttpMethod.PUT), any(HttpEntity.class),
                eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "caido"));
        TransactionSynchronizationManager.initSynchronization();
        try {
            adapter.cambiarHabilitacion(usuario, false);
            var sincronizaciones = TransactionSynchronizationManager.getSynchronizations();

            // Act & Assert
            assertThatCode(() -> sincronizaciones.forEach(
                    s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK)))
                    .doesNotThrowAnyException();
            verify(logger, times(1)).error(eq(ProveedorIdentidadKey.LOG_COMPENSACION_HABILITACION_FALLIDA),
                    eq(usuario), eq(true));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
