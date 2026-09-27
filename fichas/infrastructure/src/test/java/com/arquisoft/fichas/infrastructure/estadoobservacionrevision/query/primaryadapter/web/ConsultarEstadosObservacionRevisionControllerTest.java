package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.query.primaryadapter.web;

import com.arquisoft.fichas.application.estadoobservacionrevision.query.primaryport.interactor.ConsultarEstadosObservacionRevisionInteractor;
import com.arquisoft.fichas.application.estadoobservacionrevision.query.readmodel.EstadoObservacionRevisionReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosObservacionRevisionController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosObservacionRevisionControllerTest.TestSecurityConfig.class})
class ConsultarEstadosObservacionRevisionControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarEstadosObservacionRevisionInteractor consultarEstadosObservacionRevisionInteractor;

    private static final String RUTA = "/fichas-perfil/estados-observacion-revision";

    private static List<EstadoObservacionRevisionReadModel> catalogoCompleto() {
        return List.of(
                new EstadoObservacionRevisionReadModel("PENDIENTE", "Pendiente",
                        "La observacion revisión ha sido registrada, pero aun no se ha iniciado ninguna accion sobre ella."),
                new EstadoObservacionRevisionReadModel("EN_PROGRESO", "En Progreso",
                        "La observacion revisión esta siendo trabajada activamente."),
                new EstadoObservacionRevisionReadModel("CERRADO", "Cerrado",
                        "La observacion revisión ha sido completada y no requiere mas acciones.")
        );
    }

    @Test
    void debe200ConCatalogo_cuandoConsultaExitosa() throws Exception {
        // Arrange
        when(consultarEstadosObservacionRevisionInteractor.ejecutar()).thenReturn(catalogoCompleto());

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_OBSERVACION_REVISION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].nombre").value("Pendiente"))
                .andExpect(jsonPath("$[0].descripcion").value(
                        "La observacion revisión ha sido registrada, pero aun no se ha iniciado ninguna accion sobre ella."));
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(consultarEstadosObservacionRevisionInteractor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_OBSERVACION_REVISION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornarJsonConEstructuraCorrecta_cuandoHayElementos() throws Exception {
        // Arrange
        var estados = List.of(
                new EstadoObservacionRevisionReadModel("CERRADO", "Cerrado",
                        "La observacion revisión ha sido completada y no requiere mas acciones."),
                new EstadoObservacionRevisionReadModel("EN_PROGRESO", "En Progreso",
                        "La observacion revisión esta siendo trabajada activamente.")
        );
        when(consultarEstadosObservacionRevisionInteractor.ejecutar()).thenReturn(estados);

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_OBSERVACION_REVISION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("CERRADO"))
                .andExpect(jsonPath("$[0].nombre").value("Cerrado"))
                .andExpect(jsonPath("$[0].descripcion").value(
                        "La observacion revisión ha sido completada y no requiere mas acciones."))
                .andExpect(jsonPath("$[1].id").value("EN_PROGRESO"));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoClientRoleInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_REVISION_VIEW))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeInvocarInteractorUnaVez_cuandoEndpointEsLlamado() throws Exception {
        // Arrange
        when(consultarEstadosObservacionRevisionInteractor.ejecutar()).thenReturn(catalogoCompleto());

        // Act
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_OBSERVACION_REVISION_VIEW))))
                .andExpect(status().isOk());

        // Assert
        verify(consultarEstadosObservacionRevisionInteractor, times(1)).ejecutar();
    }
}
