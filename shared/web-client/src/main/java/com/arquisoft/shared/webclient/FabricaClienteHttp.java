package com.arquisoft.shared.webclient;

import com.arquisoft.shared.web.client.TrazaClientHttpRequestInterceptor;
import com.arquisoft.shared.webclient.impl.ClienteHttpRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class FabricaClienteHttp {

    private final TrazaClientHttpRequestInterceptor trazaInterceptor;

    public ClienteHttp crear(ConfiguracionClienteHttp configuracion) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(configuracion.connectTimeout());
        factory.setReadTimeout(configuracion.readTimeout());
        return construir(RestClient.builder().requestFactory(factory), configuracion);
    }

    ClienteHttp construir(RestClient.Builder builder, ConfiguracionClienteHttp configuracion) {
        var restClient = builder
                .baseUrl(configuracion.urlBase())
                .requestInterceptor(trazaInterceptor)
                .build();
        return new ClienteHttpRestClient(restClient);
    }
}
