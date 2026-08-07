package com.arquisoft.shared.notification;

import com.arquisoft.shared.notification.exception.EnvioNotificacionFallidoException;
import com.arquisoft.shared.notification.model.MensajeNotificacion;

/**
 * Puerto de salida para el envio de notificaciones.
 *
 * <p>El contrato no menciona ningun proveedor ni ningun protocolo: recibe un
 * {@link MensajeNotificacion}, que es un value object propio del proyecto, nunca un tipo del SDK
 * de un tercero. Esa es la unica barrera que hace falta para que cambiar de proveedor sea cambiar
 * que {@code @Component} implementa esta interfaz, sin tocar dominio ni aplicacion.
 *
 * <p>Las implementaciones se activan por la propiedad {@code notificacion.proveedor}; agregar un
 * proveedor nuevo (por ejemplo una API REST en lugar de SMTP) consiste en escribir otra clase
 * anotada con {@code @ConditionalOnProperty} y su valor correspondiente.
 */
public interface EnvioNotificacionOutputPort {

    /**
     * Entrega el mensaje a todos sus destinatarios.
     *
     * @param mensaje contenido y destinatarios de la notificacion
     * @throws EnvioNotificacionFallidoException
     *         si el proveedor rechaza el envio o no esta disponible
     */
    void enviar(MensajeNotificacion mensaje);
}
