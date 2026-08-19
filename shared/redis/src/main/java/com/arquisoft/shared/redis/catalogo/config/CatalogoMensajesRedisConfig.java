package com.arquisoft.shared.redis.catalogo.config;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.respaldo.CatalogoMensajesRespaldo;
import com.arquisoft.shared.redis.catalogo.CatalogoMensajesHealthIndicator;
import com.arquisoft.shared.redis.catalogo.CatalogoMensajesRedis;
import com.arquisoft.shared.redis.catalogo.MonitorCatalogoRedis;
import com.arquisoft.shared.redis.catalogo.ResultadoCarga;
import com.arquisoft.shared.redis.catalogo.exception.CatalogoMensajesIncompletoException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Arranque del catálogo de mensajes contra Redis.
 *
 * <p>El bean es {@code @Primary} y no usa {@code @ConditionalOnMissingBean}: el orden de evaluación
 * de esa condición entre configuraciones de usuario no es determinista, y el catálogo es demasiado
 * central para depender de él. Es además el único punto del proyecto que llama a
 * {@link Mensajes#instalar}: los tests, que no cargan esta clase, resuelven contra el catálogo de
 * prueba sin necesitar Redis.
 *
 * <p>Aquí vive el fail-fast: si Redis no responde, si al catálogo le falta una sola de las claves
 * declaradas, o si algún patrón no admite los parámetros que su clave declara, el contexto de Spring
 * no levanta. Lo tercero se comprueba también en el build contra los {@code .properties}, pero lo
 * que se despliega es lo que Redis tenga: entre uno y otro hay una carga manual que pudo quedarse a
 * medias o apuntar a otra instancia.
 */
@Configuration
public class CatalogoMensajesRedisConfig {

    // El catálogo aún no existe cuando se emiten estas trazas, así que sus textos son literales.
    private static final String LOG_CARGADO = "Catálogo de mensajes cargado desde Redis: {} claves";

    @Bean
    public StringRedisTemplate catalogoStringRedisTemplate(RedisConnectionFactory connectionFactory) {
        // StringRedisTemplate y no el RedisTemplate<String, Object> de RedisConfig: aquel serializa
        // con GenericJacksonJsonRedisSerializer con default typing y no sabe leer las cadenas planas
        // que deja el script de carga.
        return new StringRedisTemplate(connectionFactory);
    }

    @Bean
    @Primary
    public CatalogoMensajesRedis catalogoMensajesRedis(StringRedisTemplate plantilla, AppLogger logger) {
        CatalogoMensajes respaldo = CatalogoMensajesRespaldo.porDefecto();
        var catalogo = new CatalogoMensajesRedis(plantilla, respaldo, logger);

        ResultadoCarga resultado = cargar(catalogo);
        if (!resultado.esCompleto()) {
            throw CatalogoMensajesIncompletoException.porClavesFaltantes(
                    resultado.faltantes(), resultado.declaradas());
        }
        if (!resultado.esConsistente()) {
            throw CatalogoMensajesIncompletoException.porAridadInconsistente(resultado.desajustes());
        }

        logger.info(LOG_CARGADO, resultado.cargadas());
        Mensajes.instalar(catalogo);
        return catalogo;
    }

    @Bean
    public MonitorCatalogoRedis monitorCatalogoRedis(CatalogoMensajesRedis catalogo, AppLogger logger) {
        return new MonitorCatalogoRedis(catalogo, logger);
    }

    @Bean
    public CatalogoMensajesHealthIndicator catalogoMensajesHealthIndicator(CatalogoMensajesRedis catalogo) {
        return new CatalogoMensajesHealthIndicator(catalogo);
    }

    private ResultadoCarga cargar(CatalogoMensajesRedis catalogo) {
        try {
            return catalogo.recargar();
        } catch (RuntimeException e) {
            throw CatalogoMensajesIncompletoException.porFaltaDeConexion(e);
        }
    }
}
