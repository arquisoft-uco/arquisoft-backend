package com.arquisoft.repositorio_artefactos.infrastructure.config;

import com.arquisoft.shared.util.UtilObjeto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "repositorio-artefactos.almacenamiento")
@Getter
@Setter
public class RepositorioArtefactosAlmacenamientoProperties implements InitializingBean {

    private String bucket;
    private long tamanoMaximoBytes;

    @Override
    public void afterPropertiesSet() {
        if (tamanoMaximoBytes <= 0) {
            throw new IllegalStateException(
                    "repositorio-artefactos.almacenamiento.tamano-maximo-bytes debe ser mayor que cero");
        }
        if (UtilObjeto.esNulo(bucket) || bucket.isBlank()) {
            throw new IllegalStateException("repositorio-artefactos.almacenamiento.bucket es obligatorio");
        }
    }
}
