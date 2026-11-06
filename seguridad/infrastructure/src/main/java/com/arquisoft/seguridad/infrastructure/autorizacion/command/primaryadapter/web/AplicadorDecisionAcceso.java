package com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.AutorizarAccesoInteractor;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.mapper.AutorizarAccesoRequestMapper;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.HttpKey;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class AplicadorDecisionAcceso {

    public static final String CABECERA_ID_DECISION = "X-Decision-Id";

    private final AutorizarAccesoInteractor autorizarAccesoInteractor;
    private final ResolvedorPlantillaRuta resolvedorPlantillaRuta;
    private final GestorTraza gestorTraza;
    private final NoAutenticadoEntryPoint puntoEntradaNoAutenticado;
    private final AccesoDenegadoHandler manejadorAccesoDenegado;
    private final HandlerExceptionResolver resolvedorExcepciones;

    // Constructor explicito: Lombok no propaga el @Qualifier al parametro generado.
    public AplicadorDecisionAcceso(
            AutorizarAccesoInteractor autorizarAccesoInteractor,
            ResolvedorPlantillaRuta resolvedorPlantillaRuta,
            GestorTraza gestorTraza,
            NoAutenticadoEntryPoint puntoEntradaNoAutenticado,
            AccesoDenegadoHandler manejadorAccesoDenegado,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolvedorExcepciones) {
        this.autorizarAccesoInteractor = autorizarAccesoInteractor;
        this.resolvedorPlantillaRuta = resolvedorPlantillaRuta;
        this.gestorTraza = gestorTraza;
        this.puntoEntradaNoAutenticado = puntoEntradaNoAutenticado;
        this.manejadorAccesoDenegado = manejadorAccesoDenegado;
        this.resolvedorExcepciones = resolvedorExcepciones;
    }

    public boolean aplicar(HttpServletRequest request, HttpServletResponse response, String emisor,
                           String credencial) {
        DecisionAccesoResult decision;
        try {
            decision = autorizarAccesoInteractor.ejecutar(AutorizarAccesoRequestMapper.toCommand(
                    request, resolvedorPlantillaRuta.identificarRecurso(request), emisor,
                    credencial, gestorTraza.correlacionActual()));
        } catch (RuntimeException e) {
            resolvedorExcepciones.resolveException(request, response, null, e);
            return false;
        }

        return switch (decision) {
            case DecisionAccesoResult.Permitida permitida -> {
                if (!permitida.idDecision().isBlank()) {
                    response.setHeader(CABECERA_ID_DECISION, permitida.idDecision());
                }
                yield true;
            }
            case DecisionAccesoResult.NoAutenticada ignorada -> {
                puntoEntradaNoAutenticado.commence(request, response, sinAutenticacion());
                yield false;
            }
            case DecisionAccesoResult.Denegada ignorada -> {
                manejadorAccesoDenegado.handle(request, response,
                        new AccessDeniedException(Mensajes.obtener(HttpKey.PROHIBIDO)));
                yield false;
            }
        };
    }

    private static InsufficientAuthenticationException sinAutenticacion() {
        return new InsufficientAuthenticationException(Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401));
    }
}
