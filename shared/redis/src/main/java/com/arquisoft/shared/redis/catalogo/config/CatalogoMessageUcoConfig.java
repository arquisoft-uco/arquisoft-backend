package com.arquisoft.shared.redis.catalogo.config;

import co.edu.uco.messageucolab.client.api.MessageReader;
import co.edu.uco.messageucolab.client.api.port.MessageQueryPort;
import co.edu.uco.messageucolab.client.autoconfigure.MessageUcoLabProperties;
import co.edu.uco.messageucolab.client.http.MessageUcoLabAccessTokenProvider;
import co.edu.uco.messageucolab.client.http.MessageUcoLabHttpAdapter;
import co.edu.uco.seguridad.pep.starter.m2m.ServiceIdentityProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.redis.catalogo.messageuco.EscuchaAvisosMessageUco;
import com.arquisoft.shared.redis.catalogo.messageuco.MessageUcoProperties;
import com.arquisoft.shared.redis.catalogo.messageuco.ProcesadorAvisosMessageUco;
import com.arquisoft.shared.redis.catalogo.messageuco.SincronizadorCatalogoMessageUco;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

/**
 * Alimenta el catálogo desde MessageUcoLab mediante su starter (ADR-017).
 *
 * <p>El catálogo se recarga completo al arrancar y, después, solo cuando MessageUcoLab avisa por
 * Pulsar que un mensaje cambió. No hay recarga periódica.
 *
 * <p>MessageUcoLab exige el token de una identidad de servicio, que pide el starter de Seguridad
 * con {@code security.pep.service-identity.*}; su client-id es también el sufijo del topic de
 * avisos. Ninguno de los dos starters valida sus propiedades al arrancar: el token se pide en la
 * primera consulta. Aquí se exigen porque, sin ellas, cada consulta fallaría y la aplicación
 * seguiría con los textos de Redis sin que nadie notara que la fuente elegida nunca funcionó.
 */
@Configuration
@ConditionalOnProperty(name = "arquisoft.catalogo.fuente", havingValue = "messageuco")
@EnableConfigurationProperties(MessageUcoProperties.class)
public class CatalogoMessageUcoConfig {

    private static final String FALTA_URL =
            "Con arquisoft.catalogo.fuente=messageuco es obligatorio configurar messageucolab.url";
    private static final String FALTA_IDENTIDAD =
            "Con arquisoft.catalogo.fuente=messageuco es obligatoria la identidad de servicio: "
                    + "security.pep.service-identity.enabled=true y su token-uri, client-id y client-secret";
    private static final String FALTA_PULSAR =
            "Con arquisoft.catalogo.fuente=messageuco es obligatorio configurar arquisoft.catalogo.messageuco.eventos.url";

    /**
     * Reemplaza el puerto del starter para ampliar el límite de lectura de {@link WebClient}.
     *
     * <p>Con el límite por defecto de 256 KB, {@code getAll} falla con un {@code 200 OK}: el catálogo
     * completo pesa más. Se hace aquí y no con {@code spring.http.codecs} para no alterar a los demás
     * clientes de la aplicación.
     */
    @Bean
    public MessageQueryPort messageQueryPort(
            WebClient.Builder constructor,
            MessageUcoLabProperties conexion,
            MessageUcoLabAccessTokenProvider token,
            MessageUcoProperties propiedades) {
        int limite = Math.toIntExact(propiedades.tamanioMaximoRespuesta().toBytes());
        return new MessageUcoLabHttpAdapter(
                constructor.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(limite)), conexion, token);
    }

    /**
     * La identidad llega por {@link ObjectProvider} porque su bean solo existe con
     * {@code security.pep.service-identity.enabled=true}: si falta, el error debe nombrar la
     * propiedad y no ser un fallo genérico de inyección.
     */
    @Bean
    public SincronizadorCatalogoMessageUco sincronizadorCatalogoMessageUco(
            MessageReader lector,
            MessageUcoLabProperties conexion,
            ObjectProvider<ServiceIdentityProperties> identidad,
            StringRedisTemplate plantilla,
            AppLogger logger) {
        if (UtilTexto.esVacioONulo(conexion.url())) {
            throw new IllegalArgumentException(FALTA_URL);
        }
        identidadConfigurada(identidad);
        return new SincronizadorCatalogoMessageUco(lector, plantilla, logger);
    }

    @Bean
    public ProcesadorAvisosMessageUco procesadorAvisosMessageUco(
            SincronizadorCatalogoMessageUco sincronizador, MessageUcoProperties propiedades, AppLogger logger) {
        return new ProcesadorAvisosMessageUco(
                sincronizador, JsonMapper.shared(), propiedades.eventos().umbralRecargaCompleta(), logger);
    }

    @Bean
    public EscuchaAvisosMessageUco escuchaAvisosMessageUco(
            ProcesadorAvisosMessageUco procesador,
            SincronizadorCatalogoMessageUco sincronizador,
            MessageUcoProperties propiedades,
            ObjectProvider<ServiceIdentityProperties> identidad,
            GestorTraza gestorTraza,
            AppLogger logger) {
        if (UtilTexto.esVacioONulo(propiedades.eventos().url())) {
            throw new IllegalArgumentException(FALTA_PULSAR);
        }
        String clientId = identidadConfigurada(identidad).clientId();
        return new EscuchaAvisosMessageUco(
                procesador, sincronizador, propiedades.eventos(), clientId, gestorTraza, logger);
    }

    private ServiceIdentityProperties identidadConfigurada(ObjectProvider<ServiceIdentityProperties> identidad) {
        ServiceIdentityProperties propiedades = identidad.getIfAvailable();
        if (UtilObjeto.esNulo(propiedades) || !propiedades.configured()) {
            throw new IllegalArgumentException(FALTA_IDENTIDAD);
        }
        return propiedades;
    }
}
