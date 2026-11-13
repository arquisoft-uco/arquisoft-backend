package com.arquisoft.biblioteca.infrastructure.config;

import com.arquisoft.shared.util.UtilObjeto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = BibliotecaEnlacesProperties.PREFIJO)
@Getter
@Setter
public class BibliotecaEnlacesProperties implements InitializingBean {

    static final String PREFIJO = "biblioteca.enlaces";
    private static final String MENSAJE_URL_BASE_OBLIGATORIA = PREFIJO + ".url-base es obligatoria";
    private static final String MENSAJE_RUTA_OBLIGATORIA = PREFIJO + ".ruta-documentacion es obligatoria";

    private String urlBase;
    private String rutaDocumentacion;

    @Override
    public void afterPropertiesSet() {
        if (UtilObjeto.esNulo(urlBase) || urlBase.isBlank()) {
            throw new IllegalStateException(MENSAJE_URL_BASE_OBLIGATORIA);
        }
        if (UtilObjeto.esNulo(rutaDocumentacion) || rutaDocumentacion.isBlank()) {
            throw new IllegalStateException(MENSAJE_RUTA_OBLIGATORIA);
        }
    }
}
