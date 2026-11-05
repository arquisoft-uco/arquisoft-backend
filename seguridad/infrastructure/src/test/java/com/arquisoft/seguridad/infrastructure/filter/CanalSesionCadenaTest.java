package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.application.autenticacion.command.primaryport.interactor.ResolverIdentidadSesionInteractor;
import com.arquisoft.seguridad.application.autenticacion.command.primaryport.model.ResolverIdentidadSesionCommand;
import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.AutorizarAccesoInteractor;
import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedorSesionPdp;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.ResolvedorPlantillaRuta;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.seguridad.infrastructure.config.cors.OrigenesPermitidos;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.seguridad.infrastructure.config.security.ProteccionCsrf;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CanalSesionCadenaTest {

    private static final String ISSUER = "https://idp.example.com/realms/test";
    private static final String COOKIE_NOMBRE = "SECURITY_BASELINE_SESSION";
    private static final String COOKIE = COOKIE_NOMBRE + "=abc";
    private static final String ORIGEN = "http://localhost:3000";

    @RestController
    static class IdentidadController {

        @PostMapping("/fichas-perfil")
        String identidad() {
            return String.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        }
    }

    @Mock
    private AutorizarAccesoInteractor autorizarAcceso;

    @Mock
    private ResolverIdentidadSesionInteractor resolverIdentidadSesion;

    @Mock
    private ResolvedorPlantillaRuta resolvedor;

    @Mock
    private GestorTraza gestorTraza;

    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        lenient().when(resolvedor.identificarRecurso(any())).thenReturn("/api/fichas-perfil");
        lenient().when(gestorTraza.correlacionActual()).thenReturn("corr-1");
        HandlerExceptionResolver resolvedorExcepciones = (request, response, handler, excepcion) -> {
            var estado = switch (excepcion) {
                case InsufficientAuthenticationException e -> 401;
                case AccessDeniedException e -> 403;
                case InfrastructureException e -> 503;
                default -> 500;
            };
            response.setStatus(estado);
            return new ModelAndView();
        };
        var logger = mock(AppLogger.class);
        var entryPoint = new NoAutenticadoEntryPoint(logger, resolvedorExcepciones);
        var denegado = new AccesoDenegadoHandler(logger, resolvedorExcepciones);
        var aplicador = new AplicadorDecisionAcceso(autorizarAcceso, resolvedor, gestorTraza, entryPoint, denegado,
                resolvedorExcepciones);
        var rutas = new RutasPublicas("/api", List.of(), List.of("/auth/logout"), "none", new MockEnvironment());
        var sesion = new SesionProperties("sesion-pdp", true, "pep", COOKIE_NOMBRE, "https://pdp.test", false,
                Duration.ofSeconds(1), Duration.ofSeconds(3), new SesionProperties.Cache(Duration.ofSeconds(60), 100));
        var proveedores = new ProveedoresAutenticacion(List.of(new ProveedorSesionPdp(sesion, resolverIdentidadSesion)));
        var emisor = new AutenticacionProperties.Emisor("principal", true, ISSUER, "", List.of(),
                ModoAutorizacion.PEP, false, new ClaimsIdentidad("sub", "email", "preferred_username", "name", ""));

        var filtroProveedores = new ProveedorAutenticacionFilter(proveedores,
                new ProteccionCsrf(new OrigenesPermitidos(ORIGEN)), aplicador, rutas, entryPoint, denegado,
                resolvedorExcepciones, logger);
        var filtroTraza = new IdentidadTrazaFilter(gestorTraza);
        var filtroAutorizacion = new AutorizacionAccesoFilter(aplicador, rutas,
                new AutenticacionProperties(List.of(emisor)), entryPoint);
        mockMvc = MockMvcBuilders.standaloneSetup(new IdentidadController())
                .addFilters(filtroProveedores, filtroTraza, filtroAutorizacion)
                .build();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private static RequestPostProcessor conContextoApi() {
        return (MockHttpServletRequest request) -> {
            request.setContextPath("/api");
            return request;
        };
    }

    private static void autenticarConBearer() {
        var jwt = Jwt.withTokenValue("bearer-tok").header("alg", "RS256").subject("sub-jwt").issuer(ISSUER)
                .issuedAt(Instant.now().minusSeconds(10)).expiresAt(Instant.now().plusSeconds(600)).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of(), "sub-jwt"));
    }

    @Test
    void debeResponder200_cuandoCookieValida() throws Exception {
        // Arrange
        when(autorizarAcceso.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Permitida("d-1"));
        when(resolverIdentidadSesion.ejecutar(ResolverIdentidadSesionCommand.crear(COOKIE)))
                .thenReturn(new ResolucionSesionResult.Resuelta("sub-sesion", "Ana", "ana@uco.edu.co", "t-1"));
        var captor = ArgumentCaptor.forClass(AutorizarAccesoCommand.class);

        // Act / Assert
        mockMvc.perform(post("/api/fichas-perfil").with(conContextoApi())
                        .cookie(new Cookie(COOKIE_NOMBRE, "abc"))
                        .header("X-XSRF-TOKEN", "tok").header("Origin", ORIGEN))
                .andExpect(status().isOk())
                .andExpect(content().string("sub-sesion"))
                .andExpect(header().string("X-Decision-Id", "d-1"));
        verify(autorizarAcceso).ejecutar(captor.capture());
        assertThat(captor.getValue().emisor()).isEqualTo("sesion-pdp");
        assertThat(captor.getValue().credencial()).isEqualTo(COOKIE);
    }

    @Test
    void debeResponder401_cuandoElPepRechazaLaSesion() throws Exception {
        // Arrange
        when(autorizarAcceso.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.NoAutenticada());

        // Act / Assert
        mockMvc.perform(post("/api/fichas-perfil").with(conContextoApi())
                        .cookie(new Cookie(COOKIE_NOMBRE, "abc"))
                        .header("X-XSRF-TOKEN", "tok").header("Origin", ORIGEN))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(resolverIdentidadSesion);
    }

    @Test
    void debeResponder503_cuandoPdpDeSesionCaido() throws Exception {
        // Arrange
        when(autorizarAcceso.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Permitida(""));
        when(resolverIdentidadSesion.ejecutar(any(ResolverIdentidadSesionCommand.class)))
                .thenThrow(new InfrastructureException("pdp caido", "SEG-X"));

        // Act / Assert
        mockMvc.perform(post("/api/fichas-perfil").with(conContextoApi())
                        .cookie(new Cookie(COOKIE_NOMBRE, "abc"))
                        .header("X-XSRF-TOKEN", "tok").header("Origin", ORIGEN))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void debeResponder403_cuandoCsrfOOrigenInvalido() throws Exception {
        // Act / Assert
        mockMvc.perform(post("/api/fichas-perfil").with(conContextoApi())
                        .cookie(new Cookie(COOKIE_NOMBRE, "abc"))
                        .header("Origin", ORIGEN))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/fichas-perfil").with(conContextoApi())
                        .cookie(new Cookie(COOKIE_NOMBRE, "abc"))
                        .header("X-XSRF-TOKEN", "tok").header("Origin", "http://malo.test"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(autorizarAcceso, resolverIdentidadSesion);
    }

    @Test
    void debeSeguirIgual_cuandoBearer() throws Exception {
        // Arrange
        autenticarConBearer();
        when(autorizarAcceso.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Permitida(""));
        var captor = ArgumentCaptor.forClass(AutorizarAccesoCommand.class);

        // Act / Assert
        mockMvc.perform(post("/api/fichas-perfil").with(conContextoApi()))
                .andExpect(status().isOk())
                .andExpect(content().string("sub-jwt"));
        verify(autorizarAcceso).ejecutar(captor.capture());
        assertThat(captor.getValue().emisor()).isEqualTo("principal");
        assertThat(captor.getValue().credencial()).isEqualTo("bearer-tok");
        verifyNoInteractions(resolverIdentidadSesion);
    }

    @Test
    void debeGanarBearer_cuandoBearerYCookieSinCabecera() throws Exception {
        // Arrange
        autenticarConBearer();
        when(autorizarAcceso.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Permitida(""));
        var captor = ArgumentCaptor.forClass(AutorizarAccesoCommand.class);

        // Act / Assert
        mockMvc.perform(post("/api/fichas-perfil").with(conContextoApi())
                        .cookie(new Cookie(COOKIE_NOMBRE, "abc")))
                .andExpect(status().isOk())
                .andExpect(content().string("sub-jwt"));
        verify(autorizarAcceso).ejecutar(captor.capture());
        assertThat(captor.getValue().emisor()).isEqualTo("principal");
        assertThat(captor.getValue().credencial()).isEqualTo("bearer-tok").doesNotContain(COOKIE_NOMBRE);
        verify(resolverIdentidadSesion, never()).ejecutar(any());
    }
}
