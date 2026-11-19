package com.arquisoft.shared.redis.catalogo.messageuco;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

/**
 * Sincronización con MessageUcoLab. La conexión HTTP es del starter, en {@code messageucolab.*}, y
 * la identidad de servicio del starter de Seguridad, en {@code security.pep.service-identity.*}.
 *
 * @param tamanioMaximoRespuesta límite de lectura de la consulta con todos los mensajes
 * @param eventos                suscripción a los avisos de cambio que MessageUcoLab publica en Pulsar
 */
@ConfigurationProperties("arquisoft.catalogo.messageuco")
public record MessageUcoProperties(
        @DefaultValue("16MB") DataSize tamanioMaximoRespuesta,
        @DefaultValue Eventos eventos) {

    /**
     * Avisos de cambio. El topic es {@code <prefijoTopic>-<client-id de la identidad>}: MessageUcoLab
     * publica en él solo los cambios del ambiente asociado a esa identidad.
     *
     * @param url                  dirección del broker de Pulsar
     * @param prefijoTopic         prefijo del topic de avisos
     * @param suscripcion          nombre de la suscripción durable; todas las instancias comparten la misma
     * @param reintentoSuscripcion espera entre intentos de suscribirse mientras Pulsar no responde
     * @param reintentoAviso       espera antes de volver a entregar un lote que falló por MessageUcoLab
     * @param ventanaLote          cuánto se esperan más avisos antes de aplicar los que llegaron
     * @param umbralRecargaCompleta con más mensajes que estos en un lote, se recarga el catálogo completo
     *                             (una consulta) en lugar de pedirlos uno por uno
     */
    public record Eventos(
            String url,
            @DefaultValue("persistent://public/default/message-events") String prefijoTopic,
            @DefaultValue("arquisoft-catalogo") String suscripcion,
            @DefaultValue("PT30S") Duration reintentoSuscripcion,
            @DefaultValue("PT1M") Duration reintentoAviso,
            @DefaultValue("PT2S") Duration ventanaLote,
            @DefaultValue("20") int umbralRecargaCompleta) {
    }
}
