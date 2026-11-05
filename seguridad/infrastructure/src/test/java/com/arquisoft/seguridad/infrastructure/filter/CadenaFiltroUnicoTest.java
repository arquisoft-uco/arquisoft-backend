package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.AutorizarAccesoInteractor;
import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.AplicadorListaNegra;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.ResolvedorPlantillaRuta;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.BearerAuthenticationToken;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.bearer.ProveedorBearer;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.seguridad.infrastructure.config.security.ProteccionCsrf;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.seguridad.infrastructure.config.security.SeguridadConfig;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = CadenaFiltroUnicoTest.Configuracion.class)
class CadenaFiltroUnicoTest {

    static final AutorizarAccesoInteractor INTERACTOR = mock(AutorizarAccesoInteractor.class);
    static final AuthenticationManager GESTOR = mock(AuthenticationManager.class);
    static final boolean[] DECISION_HABILITADA = {true};

    @RestController
    static class ControladorPrueba {

        @GetMapping({"/ruta/{id}", "/publica"})
        String responder() {
            return "ok";
        }
    }

    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    static class Configuracion {

        @Bean
        ControladorPrueba controladorPrueba() {
            return new ControladorPrueba();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            return new UrlBasedCorsConfigurationSource();
        }

        @Bean
        SecurityFilterChain cadena(HttpSecurity http) throws Exception {
            var entryPoint = mock(NoAutenticadoEntryPoint.class);
            doAnswer(invocacion -> {
                invocacion.<HttpServletResponse>getArgument(1).setStatus(401);
                return null;
            }).when(entryPoint).commence(any(), any(), any());
            var denegado = mock(AccesoDenegadoHandler.class);
            var gestorTraza = mock(GestorTraza.class);
            when(gestorTraza.correlacionActual()).thenReturn("corr-1");
            var resolvedor = mock(ResolvedorPlantillaRuta.class);
            when(resolvedor.identificarRecurso(any())).thenReturn("/ruta/{id}");
            var rutas = new RutasPublicas("", List.of("/publica"), List.of(), "none", new MockEnvironment());
            var propiedades = new AutenticacionProperties(List.of());
            var aplicadorDecision = new AplicadorDecisionAcceso(INTERACTOR, resolvedor, gestorTraza, entryPoint,
                    denegado, mock(HandlerExceptionResolver.class));
            var listaNegra = mock(AplicadorListaNegra.class);
            when(listaNegra.aplicar(any(), any(), any())).thenReturn(true);
            var filtro = new AutorizacionAccesoFilter(
                    new ProveedoresAutenticacion(List.of(new ProveedorBearer(
                            new AutenticacionProperties(List.of(new AutenticacionProperties.Emisor("principal", true,
                                    "https://idp.test", "", List.of(),
                                    ModoAutorizacion.PEP,
                                    false, new ClaimsIdentidad(
                                            "sub", "email", "preferred_username", "name", "")))), GESTOR))),
                    mock(ProteccionCsrf.class), aplicadorDecision, listaNegra, gestorTraza, rutas,
                    entryPoint, denegado, mock(HandlerExceptionResolver.class), mock(AppLogger.class),
                    DECISION_HABILITADA[0]);
            return new SeguridadConfig(mock(AppLogger.class), denegado, entryPoint, gestorTraza, rutas, propiedades)
                    .seguridadFilterChain(http, filtro);
        }
    }

    @Autowired
    private WebApplicationContext contexto;

    private MockMvc mockMvc;

    @BeforeEach
    void preparar() {
        mockMvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
        reset(INTERACTOR, GESTOR);
    }

    @Test
    void debeResponder401SinConsultarAlPep_cuandoNoHayCredencialEnRutaProtegida() throws Exception {
        // Act / Assert
        mockMvc.perform(get("/ruta/1")).andExpect(status().isUnauthorized());
        verify(INTERACTOR, never()).ejecutar(any());
    }

    @Test
    void debeResponder401SinConsultarAlPep_cuandoBearerInvalido() throws Exception {
        // Arrange
        when(GESTOR.authenticate(any())).thenThrow(new BadCredentialsException("invalido"));

        // Act / Assert
        mockMvc.perform(get("/ruta/1").header("Authorization", "Bearer malo")).andExpect(status().isUnauthorized());
        verify(INTERACTOR, never()).ejecutar(any());
    }

    @Test
    void debeAutenticarYDecidirConElEmisorDelToken_cuandoBearerValido() throws Exception {
        // Arrange
        when(GESTOR.authenticate(any())).thenReturn(new BearerAuthenticationToken("principal", "sub-1",
                "a@uco.edu.co", "a@uco.edu.co", List.of(), "jti-1", null));
        when(INTERACTOR.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Permitida("dec-7"));

        // Act / Assert
        mockMvc.perform(get("/ruta/1").header("Authorization", "Bearer bueno"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Decision-Id", "dec-7"));
        var captor = ArgumentCaptor.forClass(AutorizarAccesoCommand.class);
        verify(INTERACTOR).ejecutar(captor.capture());
        assertThat(captor.getValue().emisor()).isEqualTo("principal");
        assertThat(captor.getValue().credencial()).isEqualTo("bueno");
    }

    @Test
    void debeNoActuar_cuandoLaRutaEsPublica() throws Exception {
        // Act / Assert
        mockMvc.perform(get("/publica")).andExpect(status().isOk());
        verify(INTERACTOR, never()).ejecutar(any());
    }
}
