package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.lettuce.core.cluster.RedisClusterClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisBucketResolverTest {

    private static final int CUOTA_GLOBAL = 60;
    private static final int CUOTA_LOGIN = 3;
    private static final int MAX_IPS = 3;
    private static final long UN_MINUTO_NANOS = Duration.ofMinutes(1).toNanos();

    private static final String IP = "192.168.1.10";
    private static final boolean LOGIN = true;
    private static final boolean GLOBAL = false;

    @Mock
    private AppLogger logger;

    @Mock
    private LettuceConnectionFactory lettuceConnectionFactory;

    private RedisBucketResolver resolver(boolean habilitado) {
        return new RedisBucketResolver(
                logger,
                new LimiteSolicitudesProperties(habilitado, CUOTA_GLOBAL, CUOTA_LOGIN, MAX_IPS),
                lettuceConnectionFactory,
                new BucketsLocales(MAX_IPS));
    }

    // Redis caido: el consumo distribuido revienta y el resolver tiene que degradar, no rendirse.
    // Se espia el unico punto que habla con Redis porque getProxy es perezoso y no falla al pedirlo.
    private RedisBucketResolver resolverConRedisCaido() {
        var resolver = spy(resolver(true));
        doThrow(new IllegalStateException("Currently not connected. Commands are rejected."))
                .when(resolver).consumirEnRedis(anyString(), any());
        return resolver;
    }

    @Test
    @DisplayName("mantiene la cuota cuando Redis se cae, en vez de dejar pasar sin limite")
    void debeMantenerLaCuota_cuandoRedisSeCae() {
        // Arrange
        var resolver = resolverConRedisCaido();

        // Act
        for (int i = 0; i < CUOTA_LOGIN; i++) {
            assertThat(resolver.consumir(IP, LOGIN).isConsumed()).isTrue();
        }

        // Assert
        assertThat(resolver.consumir(IP, LOGIN).isConsumed())
                .as("sin cuota local una caida de Redis abre una ventana de fuerza bruta contra "
                        + "/login: exactamente lo que el limitador existe para cerrar")
                .isFalse();
    }

    @Test
    @DisplayName("la cuota local es por IP, no compartida entre todas")
    void debeAislarLaCuotaPorIp_cuandoEstaDegradado() {
        // Arrange
        var resolver = resolverConRedisCaido();

        // Act
        for (int i = 0; i < CUOTA_LOGIN; i++) {
            resolver.consumir(IP, LOGIN);
        }

        // Assert
        assertThat(resolver.consumir("10.0.0.1", LOGIN).isConsumed())
                .as("una cuota compartida convertiria a cualquier cliente en el limitador de todos "
                        + "los demas durante la caida")
                .isTrue();
    }

    @Test
    @DisplayName("la cuota de login y la global no se consumen entre si")
    void debeSepararLasCuotas_cuandoEstaDegradado() {
        // Arrange
        var resolver = resolverConRedisCaido();

        // Act
        for (int i = 0; i < CUOTA_LOGIN; i++) {
            resolver.consumir(IP, LOGIN);
        }

        // Assert
        assertThat(resolver.consumir(IP, GLOBAL).isConsumed()).isTrue();
    }

    @Test
    @DisplayName("deja de consultar a Redis mientras está degradado")
    void debeNoConsultarRedis_cuandoYaEstaDegradado() {
        // Arrange
        var resolver = resolverConRedisCaido();
        resolver.consumir(IP, GLOBAL);

        // Act
        resolver.consumir(IP, GLOBAL);
        resolver.consumir(IP, GLOBAL);

        // Assert
        verify(resolver).consumirEnRedis(anyString(), any());
        assertThat(resolver.estaDegradado())
                .as("sin el flag, cada peticion de la caida sigue pagando el timeout de Redis y "
                        + "agota los hilos y el pool de conexiones")
                .isTrue();
    }

    @Test
    @DisplayName("registra la degradación una sola vez, no en cada petición")
    void debeRegistrarLaDegradacionUnaVez_cuandoRedisSeCae() {
        // Arrange
        var resolver = resolverConRedisCaido();

        // Act
        resolver.consumir(IP, GLOBAL);
        resolver.consumir(IP, GLOBAL);
        resolver.consumir(IP, GLOBAL);

        // Assert
        verify(logger).error(any(ClaveMensaje.class), any(Throwable.class), any());
    }

    @Test
    @DisplayName("la caché local no crece por encima de su tope")
    void debeAcotarLaCacheLocal_cuandoSeVenMasIpsQueElTope() {
        // Arrange
        var resolver = resolverConRedisCaido();

        // Act
        for (int i = 0; i < MAX_IPS * 10; i++) {
            resolver.consumir("10.0.0." + i, GLOBAL);
        }

        // Assert
        assertThat(resolver.ipsConCuotaLocal())
                .as("sin tope, una caida de Redis se convierte en un vector de agotamiento de "
                        + "memoria — la denegacion de servicio la provocaria el propio limitador")
                .isEqualTo(MAX_IPS);
    }

    @Test
    @DisplayName("vuelve a la cuota distribuida y descarta las locales al marcarse sano")
    void debeDescartarLasCuotasLocales_cuandoVuelveASerSano() {
        // Arrange
        var resolver = resolverConRedisCaido();
        resolver.consumir(IP, GLOBAL);

        // Act
        resolver.marcarSano();

        // Assert
        assertThat(resolver.estaDegradado()).isFalse();
        assertThat(resolver.ipsConCuotaLocal()).isZero();
    }

    @Test
    @DisplayName("las dos cuotas se recargan de la misma forma")
    void debeRecargarIgualAmbasCuotas_cuandoSeComparanLasConfiguraciones() {
        // Arrange
        var resolver = resolver(true);

        // Act
        Bandwidth global = unicoLimite(resolver.configuracionGlobal());
        Bandwidth login = unicoLimite(resolver.configuracionLogin());

        // Assert
        assertThat(login.isRefillIntervally())
                .as("dos cuotas del mismo limitador con estrategias de recarga distintas hacen que "
                        + "X-Rate-Limit-Retry-After-Seconds signifique una cosa en /login y otra en "
                        + "el resto de la API, sin que nada lo declare")
                .isEqualTo(global.isRefillIntervally());
    }

    @Test
    @DisplayName("la cabecera de reintento cuenta lo que falta para un solo token")
    void debeRecargarDeFormaContinua_cuandoSeConstruyeUnaCuota() {
        // Act
        Bandwidth global = unicoLimite(resolver(true).configuracionGlobal());

        // Assert
        assertThat(global.isRefillIntervally())
                .as("con recarga por lotes getNanosToWaitForRefill devuelve el tiempo hasta reponer "
                        + "la ventana entera, y la cabecera le dice al cliente que espere hasta 60 s "
                        + "cuando solo necesita un token")
                .isFalse();
    }

    @Test
    @DisplayName("cada cuota toma su propio valor de las propiedades")
    void debeTomarLaCuotaDeLasPropiedades_cuandoSeConstruyenLasConfiguraciones() {
        // Arrange
        var resolver = resolver(true);

        // Act
        Bandwidth global = unicoLimite(resolver.configuracionGlobal());
        Bandwidth login = unicoLimite(resolver.configuracionLogin());

        // Assert
        assertThat(global.getCapacity()).isEqualTo(CUOTA_GLOBAL);
        assertThat(global.getRefillTokens()).isEqualTo(CUOTA_GLOBAL);
        assertThat(global.getRefillPeriodNanos()).isEqualTo(UN_MINUTO_NANOS);
        assertThat(login.getCapacity()).isEqualTo(CUOTA_LOGIN);
        assertThat(login.getRefillTokens()).isEqualTo(CUOTA_LOGIN);
        assertThat(login.getRefillPeriodNanos()).isEqualTo(UN_MINUTO_NANOS);
    }

    @Test
    @DisplayName("no toca Redis cuando el limitador está deshabilitado")
    void debeNoTocarRedis_cuandoElLimitadorEstaDeshabilitado() {
        // Arrange
        var resolver = spy(resolver(false));

        // Act
        var global = resolver.consumir(IP, GLOBAL);
        var login = resolver.consumir(IP, LOGIN);

        // Assert
        assertThat(global.isConsumed()).isTrue();
        assertThat(login.isConsumed()).isTrue();
        verify(resolver, never()).consumirEnRedis(anyString(), any());
        verifyNoInteractions(lettuceConnectionFactory);
    }

    @Test
    @DisplayName("deshabilitado no se agota por muchas peticiones que reciba")
    void debeNoAgotarse_cuandoElLimitadorEstaDeshabilitado() {
        // Arrange
        var resolver = resolver(false);

        // Act & Assert
        for (int i = 0; i < CUOTA_GLOBAL * 10; i++) {
            assertThat(resolver.consumir(IP, GLOBAL).isConsumed()).isTrue();
        }
    }

    @Test
    @DisplayName("aborta el arranque cuando el cliente Lettuce no es standalone")
    void debeAbortarElArranque_cuandoElClienteNoEsStandalone() {
        // Arrange
        var resolver = resolver(true);
        when(lettuceConnectionFactory.getNativeClient()).thenReturn(mock(RedisClusterClient.class));

        // Act & Assert
        assertThatThrownBy(resolver::init)
                .isInstanceOf(InfrastructureException.class)
                .hasFieldOrPropertyWithValue("codigoError",
                        SeguridadCodes.LimiteSolicitudes.REDIS_CLIENTE_STANDALONE_REQUERIDO);
    }

    @Test
    @DisplayName("aborta el arranque cuando no hay cliente Lettuce")
    void debeAbortarElArranque_cuandoNoHayCliente() {
        // Arrange
        var resolver = resolver(true);
        when(lettuceConnectionFactory.getNativeClient()).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(resolver::init).isInstanceOf(InfrastructureException.class);
    }

    @Test
    @DisplayName("el cierre no falla cuando nunca se abrió la conexión")
    void debeCerrarSinFallar_cuandoNoHayConexionAbierta() {
        // Arrange
        var resolver = resolver(true);

        // Act & Assert
        resolver.destroy();
    }

    @Test
    @DisplayName("expone si el limitador está habilitado")
    void debeExponerSiEstaHabilitado_cuandoSeConsultanLasPropiedades() {
        // Act & Assert
        assertThat(resolver(true).estaLimiteSolicitudesHabilitado()).isTrue();
        assertThat(resolver(false).estaLimiteSolicitudesHabilitado()).isFalse();
    }

    private static Bandwidth unicoLimite(BucketConfiguration configuracion) {
        assertThat(configuracion.getBandwidths()).hasSize(1);
        return configuracion.getBandwidths()[0];
    }
}
