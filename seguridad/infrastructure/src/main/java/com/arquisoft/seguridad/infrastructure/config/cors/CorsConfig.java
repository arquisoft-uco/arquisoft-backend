package com.arquisoft.seguridad.infrastructure.config.cors;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.ConfiguracionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.arquisoft.shared.tracing.infrastructure.traza.propagacion.TrazaHeaders;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    // Nombres de cabecera HTTP: son identificadores del protocolo, no texto para el usuario.
    // No van al catalogo — traducirlos romperia el CORS que el navegador evalua literalmente.
    private static final String CABECERA_AUTHORIZATION = "Authorization";
    private static final String CABECERA_CONTENT_TYPE = "Content-Type";
    private static final String CABECERA_TOTAL_ELEMENTOS = "X-Total-Count";
    private static final String CABECERA_NUMERO_PAGINA = "X-Page-Number";
    private static final String CABECERA_TAMANIO_PAGINA = "X-Page-Size";

    private static final String PATRON_TODAS_LAS_RUTAS = "/**";

    private static final String SEPARADOR_LISTA = ",";

    private final AppLogger logger;
    
    @Value("${security.cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;
    
    @Value("${security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;
    
    // La SPA autentica con Bearer token (header Authorization), NO con cookies:
    // solo se necesitan estos headers en las requests entrantes. Evitamos "*".
    @Value("${security.cors.allowed-headers:Authorization,Content-Type}")
    private String allowedHeaders;

    @Value("${security.cors.max-age:3600}")
    private long maxAge;

    // Bearer (no cookies) ⇒ credentials=false. Evita el modo credentialed de CORS
    // y su restricción de no combinar con orígenes comodín.
    @Value("${security.cors.allow-credentials:false}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        
        List<String> origins = Arrays.asList(allowedOrigins.split(SEPARADOR_LISTA));
        configuration.setAllowedOrigins(origins);
        
        List<String> methods = Arrays.asList(allowedMethods.split(SEPARADOR_LISTA));
        configuration.setAllowedMethods(methods);
        
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(SEPARADOR_LISTA)));
        
        configuration.setExposedHeaders(Arrays.asList(
                CABECERA_AUTHORIZATION,
                CABECERA_CONTENT_TYPE,
                CABECERA_TOTAL_ELEMENTOS,
                CABECERA_NUMERO_PAGINA,
                CABECERA_TAMANIO_PAGINA,
                TrazaHeaders.X_CORRELATION_ID,
                TrazaHeaders.X_TRANSACTION_ID
        ));
        
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
                
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(PATRON_TODAS_LAS_RUTAS, configuration);
        
        return source;
    }

    // El log NO va dentro del @Bean: el catalogo de mensajes se instala en un bean propio
    // (CatalogoMensajesRedisConfig) que puede construirse despues que este, y entonces
    // Mensajes.obtener devuelve la clave cruda en vez del texto — y SLF4J, al no encontrar
    // ningun {} en esa clave, descarta ademas los argumentos. En ApplicationReadyEvent el
    // catalogo ya esta instalado siempre.
    @EventListener(ApplicationReadyEvent.class)
    public void registrarConfiguracionAplicada() {
        logger.info(Mensajes.obtener(ConfiguracionKey.LOG_CORS_CONFIGURADO),
                Arrays.asList(allowedOrigins.split(SEPARADOR_LISTA)),
                Arrays.asList(allowedMethods.split(SEPARADOR_LISTA)),
                maxAge);
    }
}
