package com.arquisoft.shared.notification.model;

import java.util.List;

/**
 * Contenido y destinatarios de una notificacion, independiente del proveedor que la entregue.
 *
 * <p>Es el unico tipo que atraviesa {@code EnvioNotificacionOutputPort}. Al no derivar de ningun SDK, un
 * cambio de proveedor no se propaga a quien construye el mensaje.
 *
 * @param destinatarios a quienes se entrega; nunca vacia
 * @param asunto        linea de asunto
 * @param cuerpo        cuerpo del mensaje
 * @param esHtml        {@code true} si el cuerpo es HTML, {@code false} si es texto plano
 */
public record MensajeNotificacion(
        List<DestinatarioNotificacion> destinatarios,
        String asunto,
        String cuerpo,
        boolean esHtml) {

    public MensajeNotificacion {
        destinatarios = destinatarios == null ? List.of() : List.copyOf(destinatarios);
    }

    /**
     * Construye un mensaje de texto plano para un unico destinatario — el caso mas frecuente.
     *
     * @param destinatario a quien se entrega
     * @param asunto       linea de asunto
     * @param cuerpo       cuerpo en texto plano
     * @return el mensaje listo para entregar
     */
    public static MensajeNotificacion textoPlano(
            DestinatarioNotificacion destinatario, String asunto, String cuerpo) {
        return new MensajeNotificacion(List.of(destinatario), asunto, cuerpo, false);
    }
}
