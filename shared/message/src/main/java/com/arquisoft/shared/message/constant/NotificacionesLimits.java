package com.arquisoft.shared.message.constant;

/**
 * Límites de longitud del contexto notificaciones. Ver la nota de {@link FichasLimits}.
 *
 * <p>Los valores coinciden con las columnas de la tabla {@code notificacion}: validar en el
 * agregado con el mismo límite que declara el DDL evita que un texto largo llegue a la base y
 * reviente con un error de driver en lugar de un 422 explicable.
 */
public final class NotificacionesLimits {

    private NotificacionesLimits() {}

    public static final class Notificacion {

        private Notificacion() {}

        public static final int ID_EVENTO_MAX = 36;
        public static final int TIPO_MAX = 60;
        public static final int DESTINATARIO_MAX = 50;
        public static final int ASUNTO_MAX = 200;
        public static final int DESTINATARIO_NOMBRE_MAX = 100;
    }
}
