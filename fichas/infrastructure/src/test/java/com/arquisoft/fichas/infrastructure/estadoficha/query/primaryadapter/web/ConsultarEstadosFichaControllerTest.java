package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.fichas.application.estadoficha.query.primaryport.interactor.ConsultarEstadosFichaInteractor;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
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

@WebMvcTest(ConsultarEstadosFichaController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosFichaControllerTest.TestSecurityConfig.class})
class ConsultarEstadosFichaControllerTest {

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
    private ConsultarEstadosFichaInteractor consultarEstadosFichaInteractor;

    @Test
    void debe200_cuandoConsultaExitosa() throws Exception {
        // Arrange
        List<EstadoFichaReadModel> estados = List.of(
                new EstadoFichaReadModel("EN_CONSTRUCCION", "En Construccion", "Ficha en desarrollo"),
                new EstadoFichaReadModel("APROBADA", "Aprobada", "Ficha aprobada")
        );
        when(consultarEstadosFichaInteractor.ejecutar()).thenReturn(estados);

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estados-ficha")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_FICHA_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("EN_CONSTRUCCION"))
                .andExpect(jsonPath("$[0].nombre").value("En Construccion"))
                .andExpect(jsonPath("$[0].descripcion").value("Ficha en desarrollo"))
                .andExpect(jsonPath("$[1].id").value("APROBADA"))
                .andExpect(jsonPath("$[1].nombre").value("Aprobada"))
                .andExpect(jsonPath("$[1].descripcion").value("Ficha aprobada"));
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(consultarEstadosFichaInteractor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estados-ficha")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_FICHA_VIEW))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornarJsonCorrecto_cuandoListaTieneElementos() throws Exception {
        // Arrange
        List<EstadoFichaReadModel> estados = List.of(
                new EstadoFichaReadModel("NO_APROBADA", "No Aprobada", "Ficha rechazada"),
                new EstadoFichaReadModel("DISPONIBLE_PARA_EVALUACION", "Disponible para Evaluacion", "Lista para evaluar"),
                new EstadoFichaReadModel("APROBADA_CON_OBSERVACIONES", "Aprobada con Observaciones", "Aprobada condicionalmente")
        );
        when(consultarEstadosFichaInteractor.ejecutar()).thenReturn(estados);

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estados-ficha")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_FICHA_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("NO_APROBADA"))
                .andExpect(jsonPath("$[1].id").value("DISPONIBLE_PARA_EVALUACION"))
                .andExpect(jsonPath("$[2].id").value("APROBADA_CON_OBSERVACIONES"));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estados-ficha"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estados-ficha")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("otro-permiso-incorrecto"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeInvocarInteractor_cuandoEndpointEsLlamado() throws Exception {
        // Arrange
        List<EstadoFichaReadModel> estados = List.of(
                new EstadoFichaReadModel("EN_CONSTRUCCION", "En Construccion", "Ficha en desarrollo")
        );
        when(consultarEstadosFichaInteractor.ejecutar()).thenReturn(estados);

        // Act
        mockMvc.perform(get("/fichas-perfil/estados-ficha")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_FICHA_VIEW))))
                .andExpect(status().isOk());

        // Assert
        verify(consultarEstadosFichaInteractor, times(1)).ejecutar();
    }

    @Test
    void debeUsarPreAuthorizeConClientRole_cuandoEndpointRequiereAutorizacion() throws Exception {
        // Arrange
        List<EstadoFichaReadModel> estados = List.of(
                new EstadoFichaReadModel("APROBADA", "Aprobada", "Ficha aprobada")
        );
        when(consultarEstadosFichaInteractor.ejecutar()).thenReturn(estados);

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estados-ficha")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_FICHA_VIEW))))
                .andExpect(status().isOk());
    }
}
