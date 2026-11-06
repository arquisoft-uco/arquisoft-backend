package com.arquisoft.shared.redis.catalogo.messageuco;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

/**
 * Sincronización con MessageUcoLab. La conexión es del starter, en {@code messageucolab.*}.
 */
@ConfigurationProperties("arquisoft.catalogo.messageuco")
public record MessageUcoProperties(
        @DefaultValue("PT1M") Duration lockLease,
        @DefaultValue("16MB") DataSize tamanioMaximoRespuesta) {
}
