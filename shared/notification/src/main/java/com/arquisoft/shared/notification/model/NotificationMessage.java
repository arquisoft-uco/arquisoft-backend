package com.arquisoft.shared.notification.model;

import java.util.List;

/**
 * Contenido y destinatarios de una notificacion, independiente del proveedor que la entregue.
 *
 * <p>Es el unico tipo que atraviesa {@code NotificationSender}. Al no derivar de ningun SDK, un
 * cambio de proveedor no se propaga a quien construye el mensaje.
 *
 * @param destinatarios a quienes se entrega; nunca vacia
 * @param asunto        linea de asunto
 * @param cuerpo        cuerpo del mensaje
 * @param esHtml        {@code true} si el cuerpo es HTML, {@code false} si es texto plano
 */
public record NotificationMessage(
        List<NotificationRecipient> destinatarios,
        String asunto,
        String cuerpo,
        boolean esHtml) {

    public NotificationMessage {
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
    public static NotificationMessage textoPlano(
            NotificationRecipient destinatario, String asunto, String cuerpo) {
        return new NotificationMessage(List.of(destinatario), asunto, cuerpo, false);
    }
}
