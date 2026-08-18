package com.arquisoft.shared.web.filter;

import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import com.arquisoft.shared.tracing.infrastructure.traza.propagacion.TrazaHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@Order(-300)
public class TrazabilidadFilter extends OncePerRequestFilter {

    private static final String MARCA_AUDITORIA = "AUDIT";

    private static final int PRIMER_ESTADO_EXITOSO = 200;
    private static final int PRIMER_ESTADO_REDIRECCION = 300;
    private static final int PRIMER_ESTADO_CLIENTE = 400;
    private static final int PRIMER_ESTADO_SERVIDOR = 500;

    private static final String RUTAS_EXCLUIDAS_POR_DEFECTO =
            "/api/actuator/,/api/swagger-ui,/api/v3/api-docs,/api/swagger-resources";

    private final GestorTraza gestorTraza;
    private final List<String> rutasExcluidas;

    public TrazabilidadFilter(
            GestorTraza gestorTraza,
            @Value("${arquisoft.trazas.rutas-excluidas-auditoria:" + RUTAS_EXCLUIDAS_POR_DEFECTO + "}")
            List<String> rutasExcluidas) {
        this.gestorTraza = gestorTraza;
        this.rutasExcluidas = List.copyOf(rutasExcluidas);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var solicitud = SolicitudTraza.paraHttp(
                request.getHeader(TrazaHeaders.X_CORRELATION_ID),
                request.getHeader(TrazaHeaders.TRACEPARENT),
                request.getRemoteAddr(),
                request.getMethod(),
                request.getRequestURI());

        try (AlcanceTraza alcance = gestorTraza.abrir(solicitud)) {
            propagar(response, alcance);
            try {
                filterChain.doFilter(request, response);
            } finally {
                cerrar(request, response, alcance);
            }
        }
    }

    private void propagar(HttpServletResponse response, AlcanceTraza alcance) {
        response.setHeader(TrazaHeaders.X_CORRELATION_ID, alcance.correlacionId());
        response.setHeader(TrazaHeaders.X_TRANSACTION_ID, alcance.transaccionId());
    }

    private void cerrar(HttpServletRequest request, HttpServletResponse response, AlcanceTraza alcance) {
        if (request.isAsyncStarted() || esExcluida(request.getRequestURI())) {
            return;
        }
        int estado = response.getStatus();
        alcance.registrarSalida(estado);
        auditar(estado);
    }

    private void auditar(int estado) {
        if (estado >= PRIMER_ESTADO_SERVIDOR) {
            log.error(MARCA_AUDITORIA);
        } else if (estado >= PRIMER_ESTADO_CLIENTE) {
            log.warn(MARCA_AUDITORIA);
        } else if (estado >= PRIMER_ESTADO_EXITOSO && estado < PRIMER_ESTADO_REDIRECCION) {
            log.info(MARCA_AUDITORIA);
        }
    }

    private boolean esExcluida(String uri) {
        return uri != null && rutasExcluidas.stream().anyMatch(uri::startsWith);
    }
}
