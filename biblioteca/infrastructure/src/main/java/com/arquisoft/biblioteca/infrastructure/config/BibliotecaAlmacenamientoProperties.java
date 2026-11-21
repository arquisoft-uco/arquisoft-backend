package com.arquisoft.biblioteca.infrastructure.config;

import com.arquisoft.shared.util.UtilTexto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = BibliotecaAlmacenamientoProperties.PREFIJO)
@Getter
@Setter
public class BibliotecaAlmacenamientoProperties implements InitializingBean {

    static final String PREFIJO = "biblioteca.almacenamiento";
    private static final String MENSAJE_BUCKET_ENTREGABLES_OBLIGATORIO = PREFIJO + ".bucket-entregables es obligatorio";

    private String bucketEntregables;

    @Override
    public void afterPropertiesSet() {
        if (UtilTexto.esVacioONulo(bucketEntregables)) {
            throw new IllegalStateException(MENSAJE_BUCKET_ENTREGABLES_OBLIGATORIO);
        }
    }
}
