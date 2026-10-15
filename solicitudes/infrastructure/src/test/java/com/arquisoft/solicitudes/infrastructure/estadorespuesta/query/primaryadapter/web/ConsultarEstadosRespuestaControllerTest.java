package com.arquisoft.solicitudes.infrastructure.estadorespuesta.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.estadorespuesta.query.primaryport.interactor.ConsultarEstadosRespuestaInteractor;
import com.arquisoft.solicitudes.application.estadorespuesta.query.readmodel.EstadoRespuestaReadModel;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
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

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosRespuestaController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosRespuestaControllerTest.TestSecurityConfig.class})
class ConsultarEstadosRespuestaControllerTest {

    private static final String RUTA = "/solicitudes/estados-respuesta";

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
    private ConsultarEstadosRespuestaInteractor consultarEstadosRespuestaInteractor;

    @Test
    void debe200ConLosEstadosEnOrden_cuandoConsultaExitosa() throws Exception {
        // Arrange
        var estados = List.of(
                new EstadoRespuestaReadModel("APROBADA", "Aprobada", "Cumple los criterios"),
                new EstadoRespuestaReadModel("EN_REVISION", "En revisión", "En proceso de evaluación"),
                new EstadoRespuestaReadModel("NO_APROBADA", "No aprobada", "No cumple los criterios"));
        when(consultarEstadosRespuestaInteractor.ejecutar()).thenReturn(estados);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermiso()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("APROBADA"))
                .andExpect(jsonPath("$[0].nombre").value("Aprobada"))
                .andExpect(jsonPath("$[0].descripcion").value("Cumple los criterios"))
                .andExpect(jsonPath("$[1].id").value("EN_REVISION"))
                .andExpect(jsonPath("$[2].id").value("NO_APROBADA"));
        verify(consultarEstadosRespuestaInteractor, times(1)).ejecutar();
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(consultarEstadosRespuestaInteractor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermiso()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
        verify(consultarEstadosRespuestaInteractor, never()).ejecutar();
    }

    @Test
    void debe403_cuandoElClientRoleEsElDelCatalogoHermano() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW))))
                .andExpect(status().isForbidden());
        verify(consultarEstadosRespuestaInteractor, never()).ejecutar();
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(SolicitudesAuthorities.ESTADO_RESPUESTA_VIEW));
    }
}
