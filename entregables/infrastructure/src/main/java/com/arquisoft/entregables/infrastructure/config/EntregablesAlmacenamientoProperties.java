package com.arquisoft.entregables.infrastructure.config;

import com.arquisoft.shared.util.UtilObjeto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = EntregablesAlmacenamientoProperties.PREFIJO)
@Getter
@Setter
public class EntregablesAlmacenamientoProperties implements InitializingBean {

    static final String PREFIJO = "entregables.almacenamiento";
    private static final String MENSAJE_BUCKET_OBLIGATORIO = PREFIJO + ".bucket es obligatorio";

    private String bucket;

    @Override
    public void afterPropertiesSet() {
        if (UtilObjeto.esNulo(bucket) || bucket.isBlank()) {
            throw new IllegalStateException(MENSAJE_BUCKET_OBLIGATORIO);
        }
    }
}
