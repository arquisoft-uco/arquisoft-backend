package com.arquisoft.shared.web.config;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expone el catálogo de mensajes como bean inyectable.
 *
 * <p>Vive en {@code shared:web} y no en {@code shared:message} porque ese módulo se mantiene sin
 * dependencias externas: lo importa {@code shared:domain} de forma transitiva, y añadirle
 * {@code spring-context} pondría Spring en el classpath de la capa de dominio.
 *
 * <p>Estar aquí además lo hace importable desde los slices {@code @WebMvcTest}, que necesitan el
 * bean porque {@code GlobalAppExceptionHandler} lo recibe por constructor.
 */
@Configuration
public class CatalogoMensajesConfig {

    /**
     * Construye el catálogo y lo instala también en la fachada estática {@link Mensajes}, de modo
     * que la capa de dominio — que no admite inyección — resuelva las claves contra la misma
     * instancia que reciben aplicación e infraestructura.
     *
     * @return el catálogo compartido
     */
    @Bean
    public CatalogoMensajes catalogoMensajes() {
        CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();
        Mensajes.instalar(catalogo);
        return catalogo;
    }
}
