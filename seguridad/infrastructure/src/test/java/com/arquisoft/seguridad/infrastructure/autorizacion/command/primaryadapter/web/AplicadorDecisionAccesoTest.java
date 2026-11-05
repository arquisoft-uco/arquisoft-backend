package com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.AutorizarAccesoInteractor;
import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AplicadorDecisionAccesoTest {

    @Mock
    private AutorizarAccesoInteractor interactor;

    @Mock
    private ResolvedorPlantillaRuta resolvedor;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private NoAutenticadoEntryPoint entryPoint;

    @Mock
    private AccesoDenegadoHandler accesoDenegadoHandler;

    @Mock
    private HandlerExceptionResolver resolvedorExcepciones;

    private AplicadorDecisionAcceso aplicador;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void preparar() {
        aplicador = new AplicadorDecisionAcceso(interactor, resolvedor, gestorTraza, entryPoint,
                accesoDenegadoHandler, resolvedorExcepciones);
        request = new MockHttpServletRequest("POST", "/api/fichas-perfil");
        response = new MockHttpServletResponse();
        when(resolvedor.identificarRecurso(request)).thenReturn("/api/fichas-perfil");
        when(gestorTraza.correlacionActual()).thenReturn("corr-1");
    }

    @Test
    void debeContinuarConXDecisionId_cuandoPermitida() {
        // Arrange
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Permitida("dec-1"));
        var captor = ArgumentCaptor.forClass(AutorizarAccesoCommand.class);

        // Act
        var continuar = aplicador.aplicar(request, response, "sesion-pdp", "SECURITY_BASELINE_SESSION=abc");

        // Assert
        assertThat(continuar).isTrue();
        assertThat(response.getHeader("X-Decision-Id")).isEqualTo("dec-1");
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().emisor()).isEqualTo("sesion-pdp");
        assertThat(captor.getValue().credencial()).isEqualTo("SECURITY_BASELINE_SESSION=abc");
        assertThat(captor.getValue().metodo()).isEqualTo("POST");
        assertThat(captor.getValue().recurso()).isEqualTo("/api/fichas-perfil");
        assertThat(captor.getValue().correlacion()).isEqualTo("corr-1");
    }

    @Test
    void debeResponder401_cuandoNoAutenticada() {
        // Arrange
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.NoAutenticada());

        // Act
        var continuar = aplicador.aplicar(request, response, "sesion-pdp", "c=1");

        // Assert
        assertThat(continuar).isFalse();
        verify(entryPoint).commence(eq(request), eq(response), any(InsufficientAuthenticationException.class));
        verifyNoInteractions(accesoDenegadoHandler);
    }

    @Test
    void debeResponder403_cuandoDenegada() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Denegada());

        // Act
        var continuar = aplicador.aplicar(request, response, "sesion-pdp", "c=1");

        // Assert
        assertThat(continuar).isFalse();
        verify(accesoDenegadoHandler).handle(eq(request), eq(response), any(AccessDeniedException.class));
        verifyNoInteractions(entryPoint);
    }

    @Test
    void debeResolverLaExcepcion_cuandoFallaElInteractor() {
        // Arrange
        var fallo = new ProveedorAutorizacionNoDisponibleException("pep caido");
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenThrow(fallo);

        // Act
        var continuar = aplicador.aplicar(request, response, "sesion-pdp", "c=1");

        // Assert
        assertThat(continuar).isFalse();
        verify(resolvedorExcepciones).resolveException(request, response, null, fallo);
    }
}
