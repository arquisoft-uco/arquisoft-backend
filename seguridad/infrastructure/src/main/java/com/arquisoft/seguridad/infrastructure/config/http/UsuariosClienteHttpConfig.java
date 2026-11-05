package com.arquisoft.seguridad.infrastructure.config.http;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.UsuarioSesionProperties;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.ConfiguracionClienteHttp;
import com.arquisoft.shared.webclient.FabricaClienteHttp;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(UsuarioSesionProperties.class)
@RequiredArgsConstructor
public class UsuariosClienteHttpConfig {

    public static final String CLIENTE_USUARIOS = "seguridadUsuariosClienteHttp";
    public static final String CLIENTE_TOKEN = "seguridadUsuariosTokenClienteHttp";

    private final UsuarioSesionProperties propiedades;
    private final FabricaClienteHttp fabrica;

    @Bean(CLIENTE_USUARIOS)
    public ClienteHttp seguridadUsuariosClienteHttp() {
        return fabrica.crear(new ConfiguracionClienteHttp(
                UtilTexto.aplicarTrim(propiedades.url()).replaceAll("/+$", ""),
                propiedades.connectTimeout(), propiedades.readTimeout()));
    }

    @Bean(CLIENTE_TOKEN)
    public ClienteHttp seguridadUsuariosTokenClienteHttp() {
        return fabrica.crear(new ConfiguracionClienteHttp(
                origen(propiedades.tokenUrl()), propiedades.connectTimeout(), propiedades.readTimeout()));
    }

    public static String origen(String url) {
        try {
            var uri = URI.create(UtilTexto.aplicarTrim(url));
            return UtilObjeto.esNulo(uri.getScheme()) ? UtilTexto.VACIO : uri.getScheme() + "://" + uri.getRawAuthority();
        } catch (IllegalArgumentException e) {
            return UtilTexto.VACIO;
        }
    }

    public static String ruta(String url) {
        try {
            var ruta = URI.create(UtilTexto.aplicarTrim(url)).getRawPath();
            return UtilObjeto.esNulo(ruta) ? UtilTexto.VACIO : ruta;
        } catch (IllegalArgumentException e) {
            return UtilTexto.VACIO;
        }
    }
}
