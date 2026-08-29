package com.arquisoft.notificaciones.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notificacion")
@Data
public class NotificacionProperties {

    private String proveedor = "log";

    private String remitenteEmail = "no-reply@arquisoft.local";

    private String remitenteNombre = "Arquisoft";
}
