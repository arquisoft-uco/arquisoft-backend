package com.arquisoft.shared.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuracion del envio de notificaciones — prefijo {@code notificacion}.
 *
 * <p>{@code proveedor} decide que implementacion de {@code EnvioNotificacionOutputPort} queda activa. Los
 * valores actuales son {@code log} (no envia nada, para desarrollo) y {@code smtp}. Un proveedor
 * nuevo agrega su valor aqui documentado y su propia clase con {@code @ConditionalOnProperty}.
 *
 * <p>Las credenciales del servidor SMTP no viven en esta clase: las gestiona Spring bajo
 * {@code spring.mail.*}, de modo que cambiar de Brevo a Gmail o a Mailpit es cambiar host,
 * puerto y credenciales en el {@code .env}, sin tocar codigo.
 */
@ConfigurationProperties(prefix = "notificacion")
@Data
public class NotificacionProperties {

    /** Proveedor activo: {@code log} o {@code smtp}. */
    private String proveedor = "log";

    /** Direccion que figura como remitente en los mensajes enviados. */
    private String remitenteEmail = "no-reply@arquisoft.local";

    /** Nombre visible del remitente. */
    private String remitenteNombre = "Arquisoft";
}
