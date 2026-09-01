package com.arquisoft.seguridad.infrastructure.config.ratelimit;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisBucketResolverTest {

    private static final int CUOTA_GLOBAL = 60;
    private static final int CUOTA_LOGIN = 3;
    private static final long UN_MINUTO_NANOS = Duration.ofMinutes(1).toNanos();

    @Mock
    private AppLogger logger;

    @Mock
    private LettuceConnectionFactory lettuceConnectionFactory;

    private RedisBucketResolver resolver(boolean habilitado) {
        return new RedisBucketResolver(
                logger,
                new LimiteSolicitudesProperties(habilitado, CUOTA_GLOBAL, CUOTA_LOGIN),
                lettuceConnectionFactory);
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
        var resolver = resolver(false);

        // Act
        var global = resolver.resolveBucket("192.168.1.10");
        var login = resolver.resolveLoginBucket("192.168.1.10");

        // Assert
        assertThat(global.tryConsume(1)).isTrue();
        assertThat(login.tryConsume(1)).isTrue();
        verifyNoInteractions(lettuceConnectionFactory);
    }

    @Test
    @DisplayName("el bucket sin límite no se agota")
    void debeNoAgotarse_cuandoSeConsumeElBucketSinLimite() {
        // Arrange
        var bucket = resolver(true).bucketSinLimite();

        // Act & Assert
        assertThat(bucket.tryConsume(Integer.MAX_VALUE))
                .as("es el bucket del fail open: si se agotara, una caída de Redis acabaría "
                        + "denegando exactamente lo que el fail open existe para dejar pasar")
                .isTrue();
        assertThat(bucket.tryConsume(1)).isTrue();
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
