package com.arquisoft.repositorio_artefactos.infrastructure.config;

import com.arquisoft.shared.util.UtilObjeto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = RepositorioArtefactosAlmacenamientoProperties.PREFIJO)
@Getter
@Setter
public class RepositorioArtefactosAlmacenamientoProperties implements InitializingBean {

    static final String PREFIJO = "repositorio-artefactos.almacenamiento";
    private static final String MENSAJE_TAMANO_INVALIDO = PREFIJO + ".tamano-maximo-bytes debe ser mayor que cero";
    private static final String MENSAJE_BUCKET_OBLIGATORIO = PREFIJO + ".bucket es obligatorio";

    private String bucket;
    private long tamanoMaximoBytes;

    @Override
    public void afterPropertiesSet() {
        if (tamanoMaximoBytes <= 0) {
            throw new IllegalStateException(MENSAJE_TAMANO_INVALIDO);
        }
        if (UtilObjeto.esNulo(bucket) || bucket.isBlank()) {
            throw new IllegalStateException(MENSAJE_BUCKET_OBLIGATORIO);
        }
    }
}
