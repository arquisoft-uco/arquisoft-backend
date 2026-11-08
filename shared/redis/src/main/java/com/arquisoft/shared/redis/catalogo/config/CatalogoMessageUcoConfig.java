package com.arquisoft.shared.redis.catalogo.config;

import co.edu.uco.messageucolab.client.api.MessageReader;
import co.edu.uco.messageucolab.client.api.port.MessageQueryPort;
import co.edu.uco.messageucolab.client.autoconfigure.MessageUcoLabProperties;
import co.edu.uco.messageucolab.client.http.MessageUcoLabHttpAdapter;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.redis.catalogo.messageuco.MessageUcoProperties;
import com.arquisoft.shared.redis.catalogo.messageuco.ProgramadorSincronizacionMessageUco;
import com.arquisoft.shared.redis.catalogo.messageuco.SincronizadorCatalogoMessageUco;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Alimenta el catálogo desde MessageUcoLab mediante su starter.
 *
 * <p>El starter se autoconfigura siempre y no valida sus propiedades. Aquí se exigen porque, sin
 * identificadores, cada consulta fallaría y la aplicación seguiría con los textos de Redis sin que
 * nadie notara que la fuente elegida nunca funcionó.
 */
@Configuration
@ConditionalOnProperty(name = "arquisoft.catalogo.fuente", havingValue = "messageuco")
@EnableConfigurationProperties(MessageUcoProperties.class)
public class CatalogoMessageUcoConfig {

    private static final String FORMATO_FALTANTE =
            "Con arquisoft.catalogo.fuente=messageuco es obligatorio configurar messageucolab.%s";

    /**
     * Reemplaza el puerto del starter para ampliar el límite de lectura de {@link WebClient}.
     *
     * <p>Con el límite por defecto de 256 KB, {@code getAll} falla con un {@code 200 OK}: el catálogo
     * completo pesa más. Se hace aquí y no con {@code spring.http.codecs} para no alterar a los demás
     * clientes de la aplicación.
     */
    @Bean
    public MessageQueryPort messageQueryPort(
            WebClient.Builder constructor, MessageUcoLabProperties conexion, MessageUcoProperties propiedades) {
        int limite = Math.toIntExact(propiedades.tamanioMaximoRespuesta().toBytes());
        return new MessageUcoLabHttpAdapter(
                constructor.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(limite)), conexion);
    }

    @Bean
    public SincronizadorCatalogoMessageUco sincronizadorCatalogoMessageUco(
            MessageReader lector,
            MessageUcoLabProperties conexion,
            StringRedisTemplate plantilla,
            AppLogger logger) {
        exigir(conexion.url(), "url");
        exigir(conexion.applicationId(), "application-id");
        exigir(conexion.environmentId(), "environment-id");
        return new SincronizadorCatalogoMessageUco(lector, plantilla, logger);
    }

    @Bean
    public ProgramadorSincronizacionMessageUco programadorSincronizacionMessageUco(
            SincronizadorCatalogoMessageUco sincronizador,
            StringRedisTemplate plantilla,
            MessageUcoProperties propiedades,
            GestorTraza gestorTraza,
            AppLogger logger) {
        return new ProgramadorSincronizacionMessageUco(sincronizador, plantilla, propiedades, gestorTraza, logger);
    }

    private void exigir(String valor, String propiedad) {
        if (UtilTexto.esVacioONulo(valor)) {
            throw new IllegalArgumentException(FORMATO_FALTANTE.formatted(propiedad));
        }
    }
}
