package com.arquisoft.artefactos.infrastructure.config;

import com.arquisoft.shared.util.UtilObjeto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = ArtefactosAlmacenamientoProperties.PREFIJO)
@Getter
@Setter
public class ArtefactosAlmacenamientoProperties implements InitializingBean {

    static final String PREFIJO = "artefactos.almacenamiento";
    private static final String MENSAJE_TAMANO_INVALIDO = PREFIJO + ".tamano-maximo-bytes debe ser mayor que cero";
    private static final String MENSAJE_CUOTA_INVALIDA = PREFIJO + ".cuota-proyecto-grado-bytes debe ser mayor que cero";
    private static final String MENSAJE_TAMANO_SUPERA_CUOTA =
            PREFIJO + ".tamano-maximo-bytes no puede superar cuota-proyecto-grado-bytes";
    private static final String MENSAJE_BUCKET_OBLIGATORIO = PREFIJO + ".bucket es obligatorio";

    private String bucket;
    private long tamanoMaximoBytes;
    private long cuotaProyectoGradoBytes;

    @Override
    public void afterPropertiesSet() {
        if (tamanoMaximoBytes <= 0) {
            throw new IllegalStateException(MENSAJE_TAMANO_INVALIDO);
        }
        if (cuotaProyectoGradoBytes <= 0) {
            throw new IllegalStateException(MENSAJE_CUOTA_INVALIDA);
        }
        if (tamanoMaximoBytes > cuotaProyectoGradoBytes) {
            throw new IllegalStateException(MENSAJE_TAMANO_SUPERA_CUOTA);
        }
        if (UtilObjeto.esNulo(bucket) || bucket.isBlank()) {
            throw new IllegalStateException(MENSAJE_BUCKET_OBLIGATORIO);
        }
    }
}
