package com.arquisoft.seguridad.infrastructure.config.ratelimit;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.LimiteSolicitudesKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.util.UtilObjeto;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RedisBucketResolver implements BucketResolver, DisposableBean {

    // Prefijos de clave en Redis: identificadores de infraestructura compartidos con cualquier
    // otra instancia del backend que lea el mismo Redis. No son texto y no van al catalogo.
    private static final String CLAVE_LIMITE_GLOBAL = "arquisoft:ratelimit:global:";
    private static final String CLAVE_LIMITE_LOGIN = "arquisoft:ratelimit:login:";

    // Valor que se registra cuando el cliente Lettuce obtenido no es del tipo esperado y
    // ni siquiera existe — evita imprimir un null crudo en el log de diagnostico.
    private static final String CLIENTE_AUSENTE = "null";

    // Margen sobre el que Redis expira la entrada del bucket: el doble de la ventana de
    // recarga (1 minuto), para que un bucket a medio consumir no desaparezca antes de tiempo.
    private static final Duration EXPIRACION_BUCKET = Duration.ofMinutes(2);

    private static final Duration VENTANA_RECARGA = Duration.ofMinutes(1);

    private static final Duration RECARGA_INERTE = Duration.ofDays(1);
    private static final long RECARGA_MINIMA = 1L;

    private static final long TOKENS_POR_SOLICITUD = 1L;

    private final AppLogger logger;

    private final LimiteSolicitudesProperties properties;
    private final LettuceConnectionFactory lettuceConnectionFactory;

    private LettuceBasedProxyManager<String> proxyManager;
    private StatefulRedisConnection<String, byte[]> bucketConnection;

    private final BucketsLocales bucketsLocales;

    private final AtomicBoolean degradado = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        // getNativeClient() retorna AbstractRedisClient directamente en Spring Data Redis 4.x
        AbstractRedisClient nativeClient = lettuceConnectionFactory.getNativeClient();
        if (nativeClient instanceof RedisClient redisClient) {
            this.bucketConnection = redisClient.connect(
                    RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
            // Bucket4jLettuce.casBasedBuilder es la API no-deprecated en Bucket4j 8.x
            this.proxyManager = Bucket4jLettuce.casBasedBuilder(bucketConnection)
                    .expirationAfterWrite(ExpirationAfterWriteStrategy
                            .basedOnTimeForRefillingBucketUpToMax(EXPIRACION_BUCKET))
                    .build();
        } else {
            // logger.error: detalle tecnico para el desarrollador — nunca llega al cliente.
            // El nombre de la clase del cliente obtenido orienta rapidamente el diagnostico.
            logger.error(Mensajes.obtener(LimiteSolicitudesKey.LOG_CLIENTE_STANDALONE_ERROR),
                    !UtilObjeto.esNulo(nativeClient) ? nativeClient.getClass().getSimpleName() : CLIENTE_AUSENTE);
            // InfrastructureException con mensaje generico: si llegara a la capa web
            // (improbable desde @PostConstruct), el cliente ve un mensaje sin detalles internos.
            throw new InfrastructureException(
                    Mensajes.obtener(LimiteSolicitudesKey.ERROR_CLIENTE_STANDALONE),
                    SeguridadCodes.LimiteSolicitudes.REDIS_CLIENTE_STANDALONE_REQUERIDO);
        }
    }

    // El log NO va en @PostConstruct: este bean se construye antes que el catalogo de mensajes
    // (CatalogoMensajesRedisConfig), asi que alli Mensajes.obtener devolveria la clave cruda.
    // En ApplicationReadyEvent el catalogo ya esta instalado siempre.
    //
    // La rama de error del init si conserva su log ahi mismo: aborta el arranque, y una clave
    // cruda en el stack trace sigue diciendo que fallo — mientras que diferirla no llegaria a
    // emitirse nunca, porque ApplicationReadyEvent no ocurre si el contexto no levanta.
    @EventListener(ApplicationReadyEvent.class)
    public void registrarInicializacion() {
        logger.debug(Mensajes.obtener(LimiteSolicitudesKey.LOG_INIT_OK));
    }

    // Degradar a cuota local, y no dejar pasar sin limite, es lo que evita que una caida de Redis
    // abra una ventana de fuerza bruta contra /login. El limite pasa a ser por instancia, asi que
    // con N replicas un atacante obtiene N veces la cuota: sigue siendo un limite, y es el precio
    // de no tener estado compartido justo cuando el estado compartido es lo que ha fallado.
    //
    // El flag es lo que evita el segundo fallo, menos visible: sin el, cada peticion de la caida
    // sigue intentando Redis y paga su timeout, agotando hilos y conexiones del pool.
    @Override
    public ConsumptionProbe consumir(String ip, boolean esLogin) {
        if (!properties.enabled()) {
            return consumirSinLimite();
        }

        Supplier<BucketConfiguration> configuracion =
                esLogin ? this::configuracionLogin : this::configuracionGlobal;
        String clave = (esLogin ? CLAVE_LIMITE_LOGIN : CLAVE_LIMITE_GLOBAL) + ip;

        if (degradado.get()) {
            return consumirLocal(clave, configuracion);
        }

        try {
            return consumirEnRedis(clave, configuracion);
        } catch (RuntimeException e) {
            if (degradado.compareAndSet(false, true)) {
                logger.error(Mensajes.obtener(LimiteSolicitudesKey.LOG_DEGRADADO), e, e.getMessage());
            }
            return consumirLocal(clave, configuracion);
        }
    }

    ConsumptionProbe consumirEnRedis(String clave, Supplier<BucketConfiguration> configuracion) {
        return proxyManager.getProxy(clave, configuracion)
                .tryConsumeAndReturnRemaining(TOKENS_POR_SOLICITUD);
    }

    private ConsumptionProbe consumirLocal(String clave, Supplier<BucketConfiguration> configuracion) {
        return bucketsLocales.obtener(clave, configuracion)
                .tryConsumeAndReturnRemaining(TOKENS_POR_SOLICITUD);
    }

    private ConsumptionProbe consumirSinLimite() {
        return bucketSinLimite().tryConsumeAndReturnRemaining(TOKENS_POR_SOLICITUD);
    }

    // Ambas cuotas se recargan igual, y no es indiferente cual: con recarga por lotes el
    // X-Rate-Limit-Retry-After-Seconds que ve el cliente es el tiempo hasta que se repone la
    // ventana entera —hasta 60 s— aunque solo necesite un token. Con recarga continua la
    // cabecera dice lo que su nombre promete: cuanto falta para poder reintentar una vez.
    BucketConfiguration configuracionGlobal() {
        return porMinuto(properties.requestsPerMinute());
    }

    BucketConfiguration configuracionLogin() {
        return porMinuto(properties.loginRequestsPerMinute());
    }

    private static BucketConfiguration porMinuto(int solicitudes) {
        return BucketConfiguration.builder()
                .addLimit(limite -> limite
                        .capacity(solicitudes)
                        .refillGreedy(solicitudes, VENTANA_RECARGA))
                .build();
    }

    @Override
    public boolean estaLimiteSolicitudesHabilitado() {
        return properties.enabled();
    }

    public boolean estaDegradado() {
        return degradado.get();
    }

    // Las cuotas locales se descartan al volver a Redis: conservarlas solo mantendria el consumo de
    // una ventana ya cerrada, y a partir de aqui la cuota que manda es la distribuida.
    public void marcarSano() {
        degradado.set(false);
        bucketsLocales.limpiar();
    }

    public int ipsConCuotaLocal() {
        return bucketsLocales.tamanio();
    }

    public boolean hayConexion() {
        try {
            bucketConnection.sync().ping();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void destroy() {
        if (!UtilObjeto.esNulo(bucketConnection)) {
            bucketConnection.close();
        }
    }

    // El bucket nace lleno, asi que con esta capacidad no se agota nunca y la recarga es
    // irrelevante: Bucket4j rechaza tasas mayores a 1 token/ns, y Long.MAX_VALUE por dia lo es.
    private Bucket bucketSinLimite() {
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(Long.MAX_VALUE)
                        .refillIntervally(RECARGA_MINIMA, RECARGA_INERTE)
                        .build())
                .build();
    }
}
