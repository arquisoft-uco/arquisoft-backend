package com.arquisoft.artefactos.infrastructure.config;

import com.arquisoft.shared.util.UtilObjeto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "artefactos.almacenamiento")
@Getter
@Setter
public class ArtefactosAlmacenamientoProperties implements InitializingBean {

    private String bucket;
    private long tamanoMaximoBytes;

    @Override
    public void afterPropertiesSet() {
        if (tamanoMaximoBytes <= 0) {
            throw new IllegalStateException("artefactos.almacenamiento.tamano-maximo-bytes debe ser mayor que cero");
        }
        if (UtilObjeto.esNulo(bucket) || bucket.isBlank()) {
            throw new IllegalStateException("artefactos.almacenamiento.bucket es obligatorio");
        }
    }
}
