package com.arquisoft.fichas.infrastructure.estadorevision.query.primaryadapter.web;

import com.arquisoft.fichas.application.estadorevision.query.primaryport.interactor.ConsultarEstadosRevisionInteractor;
import com.arquisoft.fichas.application.estadorevision.query.readmodel.EstadoRevisionReadModel;
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

@WebMvcTest(ConsultarEstadosRevisionController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosRevisionControllerTest.TestSecurityConfig.class})
class ConsultarEstadosRevisionControllerTest {

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
    private ConsultarEstadosRevisionInteractor consultarEstadosRevisionInteractor;

    private static final String RUTA = "/fichas-perfil/estados-revision";

    private static List<EstadoRevisionReadModel> catalogoCompleto() {
        return List.of(
                new EstadoRevisionReadModel("NUEVA", "Nueva", "Creada recientemente, aun no revisada."),
                new EstadoRevisionReadModel("VISUALIZADA", "Visualizada", "Vista por el estudiante, sin accion aun."),
                new EstadoRevisionReadModel("EN_PROGRESO", "En Progreso", "En desarrollo, acciones en curso."),
                new EstadoRevisionReadModel("CORRECCION_DISPONIBLE", "Correccion Disponible",
                        "Trabajo completado, pendiente de validacion."),
                new EstadoRevisionReadModel("CERRADA", "Cerrada", "Completada y aprobada; estado terminal.")
        );
    }

    @Test
    void debe200ConCatalogo_cuandoConsultaExitosa() throws Exception {
        // Arrange
        when(consultarEstadosRevisionInteractor.ejecutar()).thenReturn(catalogoCompleto());

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_REVISION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].id").value("NUEVA"))
                .andExpect(jsonPath("$[0].nombre").value("Nueva"))
                .andExpect(jsonPath("$[0].descripcion").value("Creada recientemente, aun no revisada."));
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(consultarEstadosRevisionInteractor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_REVISION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornarJsonConEstructuraCorrecta_cuandoHayElementos() throws Exception {
        // Arrange
        var estados = List.of(
                new EstadoRevisionReadModel("CERRADA", "Cerrada", "Completada y aprobada; estado terminal."),
                new EstadoRevisionReadModel("EN_PROGRESO", "En Progreso", "En desarrollo, acciones en curso.")
        );
        when(consultarEstadosRevisionInteractor.ejecutar()).thenReturn(estados);

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_REVISION_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("CERRADA"))
                .andExpect(jsonPath("$[0].nombre").value("Cerrada"))
                .andExpect(jsonPath("$[0].descripcion").value("Completada y aprobada; estado terminal."))
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
                                .authorities(new SimpleGrantedAuthority("otro-permiso"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeInvocarInteractorUnaVez_cuandoEndpointEsLlamado() throws Exception {
        // Arrange
        when(consultarEstadosRevisionInteractor.ejecutar()).thenReturn(catalogoCompleto());

        // Act
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_REVISION_VIEW))))
                .andExpect(status().isOk());

        // Assert
        verify(consultarEstadosRevisionInteractor, times(1)).ejecutar();
    }
}
