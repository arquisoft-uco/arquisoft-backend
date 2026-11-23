package com.arquisoft.shared.catalogoparametros.infrastructure.config;

import com.arquisoft.shared.catalogoparametros.CatalogoParametrosEncendido;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.webclient.ClienteHttp;
import com.arquisoft.shared.webclient.ConfiguracionClienteHttp;
import com.arquisoft.shared.webclient.FabricaClienteHttp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@CatalogoParametrosEncendido
@RequiredArgsConstructor
public class CatalogoParametrosClienteHttpConfig {

    public static final String CLIENTE_CATALOGO_PARAMETROS = "catalogoParametrosClienteHttp";
    public static final String CLIENTE_TOKEN_CATALOGO_PARAMETROS = "catalogoParametrosTokenClienteHttp";

    private final CatalogoParametrosProperties propiedades;
    private final FabricaClienteHttp fabrica;

    @Bean(CLIENTE_CATALOGO_PARAMETROS)
    public ClienteHttp catalogoParametrosClienteHttp() {
        return fabrica.crear(new ConfiguracionClienteHttp(
                UtilTexto.aplicarTrim(propiedades.baseUrl()).replaceAll("/+$", ""),
                propiedades.connectTimeout(), propiedades.readTimeout()));
    }

    @Bean(CLIENTE_TOKEN_CATALOGO_PARAMETROS)
    public ClienteHttp catalogoParametrosTokenClienteHttp() {
        return fabrica.crear(new ConfiguracionClienteHttp(
                propiedades.origenTokenUri(), propiedades.connectTimeout(), propiedades.readTimeout()));
    }
}
