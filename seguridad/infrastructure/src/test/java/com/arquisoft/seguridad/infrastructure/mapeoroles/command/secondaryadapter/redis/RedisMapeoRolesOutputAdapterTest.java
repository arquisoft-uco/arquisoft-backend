package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.entity.MapeoRolesEntity;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.MapeoRolesKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.connection.RedisSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class RedisMapeoRolesOutputAdapterTest {

    private static final String PUNTERO = "arquisoft:authz:generacion";
    private static final String VIGENTE = "arquisoft:authz:vigente";
    private static final String RESERVA = "arquisoft:authz:lock";
    private static final String PLANTILLA = "arquisoft:authz:gen:%s:client-role:";
    private static final MapeoRolesProperties PROPIEDADES = new MapeoRolesProperties(
            Duration.ofMinutes(5), Duration.ofMinutes(15), Duration.ofSeconds(30), Duration.ofSeconds(60));

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RedisConnection conexion;

    @Mock
    private RedisSetCommands setCommands;

    @Mock
    private RedisKeyCommands keyCommands;

    @Mock
    private AppLogger logger;

    private RedisMapeoRolesOutputAdapter adaptador;

    @BeforeEach
    void preparar() {
        adaptador = new RedisMapeoRolesOutputAdapter(stringRedisTemplate, PROPIEDADES, logger);
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void debeOtorgar_cuandoElScriptDevuelveUno() {
        // Arrange
        when(stringRedisTemplate.execute(any(RedisScript.class), eq(List.of(PUNTERO)), eq(PLANTILLA), eq("fichas:ver"),
                eq("asesor"))).thenReturn(1L);

        // Act / Assert
        assertThat(adaptador.rolClienteOtorgadoPor("fichas:ver", List.of("asesor"))).isTrue();
    }

    @Test
    void debeNoOtorgar_cuandoElScriptDevuelveCero() {
        // Arrange
        when(stringRedisTemplate.execute(any(RedisScript.class), eq(List.of(PUNTERO)), eq(PLANTILLA), eq("fichas:ver"),
                eq("asesor"))).thenReturn(0L);

        // Act / Assert
        assertThat(adaptador.rolClienteOtorgadoPor("fichas:ver", List.of("asesor"))).isFalse();
    }

    @Test
    void debeLanzar503_cuandoElScriptDevuelveMenosUnoONulo() {
        // Arrange
        when(stringRedisTemplate.execute(any(RedisScript.class), eq(List.of(PUNTERO)), eq(PLANTILLA), eq("fichas:ver"),
                eq("asesor"))).thenReturn(-1L, (Object) null);
        var roles = List.of("asesor");

        // Act / Assert
        assertThatThrownBy(() -> adaptador.rolClienteOtorgadoPor("fichas:ver", roles))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.rolClienteOtorgadoPor("fichas:ver", roles))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        verify(logger, times(2)).error(MapeoRolesKey.LOG_SIN_MAPEO);
    }

    @Test
    void debeEjecutarElScriptConLaClaveDelPunteroYLosArgumentosEnOrden() {
        // Arrange
        when(stringRedisTemplate.execute(any(RedisScript.class), any(List.class), any(Object[].class))).thenReturn(1L);
        var captorScript = ArgumentCaptor.forClass(RedisScript.class);

        // Act
        adaptador.rolClienteOtorgadoPor("fichas:ver", List.of("asesor", "estudiante"));

        // Assert
        verify(stringRedisTemplate).execute(captorScript.capture(), eq(List.of(PUNTERO)), eq(PLANTILLA), eq("fichas:ver"),
                eq("asesor"), eq("estudiante"));
        assertThat(captorScript.getValue().getResultType()).isEqualTo(Long.class);
    }

    @Test
    void debeNoConsultarRedis_cuandoNoHayRolesDeRealm() {
        // Act / Assert
        assertThat(adaptador.rolClienteOtorgadoPor("fichas:ver", List.of())).isFalse();
        verifyNoInteractions(stringRedisTemplate);
    }

    @Test
    void debeGuardarUnaGeneracionNuevaYMoverElPunteroAlFinal() {
        // Arrange
        var mapeo = new MapeoRolesEntity(Map.of("asesor", Set.of("fichas:ver"), "estudiante", Set.of("fichas:ver")));
        var claves = new ArrayList<String>();
        var miembros = new ArrayList<String>();
        when(stringRedisTemplate.getStringSerializer()).thenReturn(RedisSerializer.string());
        when(conexion.setCommands()).thenReturn(setCommands);
        when(conexion.keyCommands()).thenReturn(keyCommands);
        when(setCommands.sAdd(any(byte[].class), any(byte[][].class))).thenAnswer(invocacion -> {
            claves.add(new String((byte[]) invocacion.getArgument(0), StandardCharsets.UTF_8));
            for (var indice = 1; indice < invocacion.getArguments().length; indice++) {
                miembros.add(new String((byte[]) invocacion.getArgument(indice), StandardCharsets.UTF_8));
            }
            return 2L;
        });
        when(stringRedisTemplate.executePipelined(any(RedisCallback.class))).thenAnswer(invocacion -> {
            ((RedisCallback) invocacion.getArgument(0)).doInRedis(conexion);
            return List.of();
        });

        // Act
        adaptador.guardar(mapeo);

        // Assert
        var captorGeneracion = ArgumentCaptor.forClass(String.class);
        var orden = inOrder(setCommands, keyCommands, valueOperations);
        orden.verify(setCommands).sAdd(any(byte[].class), any(byte[][].class));
        orden.verify(keyCommands).expire(any(byte[].class), eq(Duration.ofMinutes(16).toSeconds()));
        orden.verify(valueOperations).set(eq(PUNTERO), captorGeneracion.capture(), eq(Duration.ofMinutes(15)));
        orden.verify(valueOperations).set(VIGENTE, "1", Duration.ofMinutes(5));
        assertThat(claves).containsExactly(PLANTILLA.formatted(captorGeneracion.getValue()) + "fichas:ver");
        assertThat(miembros).containsExactlyInAnyOrder("asesor", "estudiante");
        verify(logger).debug(MapeoRolesKey.LOG_MAPEO_GUARDADO, 1, 2);
    }

    @Test
    void debeReservar_cuandoSetIfAbsentDevuelveTrue() {
        // Arrange
        when(valueOperations.setIfAbsent(RESERVA, "1", Duration.ofSeconds(60))).thenReturn(true);

        // Act / Assert
        assertThat(adaptador.reservarActualizacion()).isTrue();
    }

    @Test
    void debeNoReservar_cuandoDevuelveFalseONulo() {
        // Arrange
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(false, (Boolean) null);

        // Act / Assert
        assertThat(adaptador.reservarActualizacion()).isFalse();
        assertThat(adaptador.reservarActualizacion()).isFalse();
    }

    @Test
    void debeEstarVigente_soloConMarcadorYPuntero() {
        // Arrange
        when(stringRedisTemplate.countExistingKeys(List.of(VIGENTE, PUNTERO))).thenReturn(2L, 1L, 0L, (Long) null);

        // Act / Assert
        assertThat(adaptador.estaVigente()).isTrue();
        assertThat(adaptador.estaVigente()).isFalse();
        assertThat(adaptador.estaVigente()).isFalse();
        assertThat(adaptador.estaVigente()).isFalse();
    }

    @Test
    void debeLanzar503_cuandoRedisFalla() {
        // Arrange
        var fallo = new QueryTimeoutException("redis caido");
        when(stringRedisTemplate.execute(any(RedisScript.class), any(List.class), any(Object[].class))).thenThrow(fallo);
        when(stringRedisTemplate.countExistingKeys(any())).thenThrow(fallo);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenThrow(fallo);
        when(stringRedisTemplate.executePipelined(any(RedisCallback.class))).thenThrow(fallo);
        var roles = List.of("asesor");
        var mapeo = new MapeoRolesEntity(Map.of("asesor", Set.of("fichas:ver")));

        // Act / Assert
        assertThatThrownBy(() -> adaptador.rolClienteOtorgadoPor("fichas:ver", roles))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        assertThatThrownBy(adaptador::estaVigente).isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        assertThatThrownBy(adaptador::reservarActualizacion).isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.guardar(mapeo)).isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        verify(logger, times(4)).error(MapeoRolesKey.LOG_ALMACEN_NO_DISPONIBLE, "redis caido");
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }
}
