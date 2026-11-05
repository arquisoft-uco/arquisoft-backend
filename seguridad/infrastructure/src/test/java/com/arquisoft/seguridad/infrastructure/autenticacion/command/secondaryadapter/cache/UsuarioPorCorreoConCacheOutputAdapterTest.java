package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.cache;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.UsuarioPorCorreoWebClient;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.UsuarioSesionProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioPorCorreoConCacheOutputAdapterTest {

    private static final String CORREO = "ana@uco.edu.co";
    private static final Duration TTL = Duration.ofMinutes(5);

    @Mock
    private UsuarioPorCorreoWebClient delegado;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> operaciones;

    @Mock
    private AppLogger logger;

    @BeforeEach
    void preparar() {
        org.mockito.Mockito.lenient().when(redis.opsForValue()).thenReturn(operaciones);
    }

    private UsuarioPorCorreoConCacheOutputAdapter adaptador(Duration ttl) {
        var propiedades = new UsuarioSesionProperties("https://usuarios.test", false, Duration.ofSeconds(1),
                Duration.ofSeconds(3), ttl, "https://kc.test/token", "cliente", "secreto", "/api/usuarios/por-correo", "legado");
        return new UsuarioPorCorreoConCacheOutputAdapter(delegado, redis, propiedades, logger);
    }

    @Test
    void debeDevolverDelCache_sinLlamarAlDelegado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(operaciones.get(anyString())).thenReturn(usuario.toString());

        // Act
        var id = adaptador(TTL).obtenerIdPorCorreo(CORREO);

        // Assert
        assertThat(id).contains(usuario);
        verifyNoInteractions(delegado);
    }

    @Test
    void debeConsultarYGuardarConTtl_cuandoNoEstaEnCache() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(operaciones.get(anyString())).thenReturn(null);
        when(delegado.obtenerIdPorCorreo(CORREO)).thenReturn(Optional.of(usuario));

        // Act
        var id = adaptador(TTL).obtenerIdPorCorreo(CORREO);

        // Assert
        assertThat(id).contains(usuario);
        verify(operaciones).set(anyString(), eq(usuario.toString()), eq(TTL));
    }

    @Test
    void debeNoGuardar_cuandoElDelegadoDevuelveVacio() {
        // Arrange
        when(operaciones.get(anyString())).thenReturn(null);
        when(delegado.obtenerIdPorCorreo(CORREO)).thenReturn(Optional.empty());

        // Act
        var id = adaptador(TTL).obtenerIdPorCorreo(CORREO);

        // Assert
        assertThat(id).isEmpty();
        verify(operaciones, never()).set(anyString(), anyString(), org.mockito.ArgumentMatchers.any(Duration.class));
    }

    @Test
    void debeConsultarDirecto_cuandoRedisFalla() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(operaciones.get(anyString())).thenThrow(new QueryTimeoutException("redis caido"));
        when(delegado.obtenerIdPorCorreo(CORREO)).thenReturn(Optional.of(usuario));
        org.mockito.Mockito.doThrow(new QueryTimeoutException("redis caido"))
                .when(operaciones).set(anyString(), anyString(), org.mockito.ArgumentMatchers.any(Duration.class));

        // Act
        var id = adaptador(TTL).obtenerIdPorCorreo(CORREO);

        // Assert
        assertThat(id).contains(usuario);
        verify(logger, org.mockito.Mockito.times(2))
                .warn(eq(ResolucionSesionKey.LOG_CACHE_USUARIO_DEGRADADA), eq("QueryTimeoutException"));
    }

    @Test
    void debeUsarHuellaDelCorreoNormalizado() {
        // Arrange
        when(operaciones.get(anyString())).thenReturn(null);
        when(delegado.obtenerIdPorCorreo(CORREO)).thenReturn(Optional.empty());
        var captor = ArgumentCaptor.forClass(String.class);

        // Act
        adaptador(TTL).obtenerIdPorCorreo(CORREO);

        // Assert
        verify(operaciones).get(captor.capture());
        assertThat(captor.getValue())
                .isEqualTo("arquisoft:seguridad:usuario-por-correo:" + ResolucionSesionConCacheOutputAdapter.huella(CORREO));
    }

    @Test
    void debeNoPonerElCorreoEnLaClave() {
        // Arrange
        when(operaciones.get(anyString())).thenReturn(null);
        when(delegado.obtenerIdPorCorreo(CORREO)).thenReturn(Optional.empty());
        var captor = ArgumentCaptor.forClass(String.class);

        // Act
        adaptador(TTL).obtenerIdPorCorreo(CORREO);

        // Assert
        verify(operaciones).get(captor.capture());
        assertThat(captor.getValue()).doesNotContain("ana").doesNotContain("uco");
    }

    @Test
    void debeConsultarDirectoSinRedis_cuandoElTtlEsCero() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(delegado.obtenerIdPorCorreo(CORREO)).thenReturn(Optional.of(usuario));

        // Act
        var id = adaptador(Duration.ZERO).obtenerIdPorCorreo(CORREO);

        // Assert
        assertThat(id).contains(usuario);
        verifyNoInteractions(redis);
    }
}
