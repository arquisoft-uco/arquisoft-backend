package com.arquisoft.shared.web.client;

import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.Traceparent;
import com.arquisoft.shared.tracing.infrastructure.traza.propagacion.TrazaHeaders;
import com.arquisoft.shared.util.UtilTexto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TrazaClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final GestorTraza gestorTraza;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String correlacion = gestorTraza.correlacionActual();
        if (!UtilTexto.esVacioONulo(correlacion)) {
            var cabeceras = request.getHeaders();
            if (!cabeceras.containsHeader(TrazaHeaders.X_CORRELATION_ID)) {
                cabeceras.set(TrazaHeaders.X_CORRELATION_ID, correlacion);
            }
            Traceparent.emitir(correlacion, gestorTraza.transaccionActual())
                    .filter(valor -> !cabeceras.containsHeader(TrazaHeaders.TRACEPARENT))
                    .ifPresent(valor -> cabeceras.set(TrazaHeaders.TRACEPARENT, valor));
        }
        return execution.execute(request, body);
    }
}
