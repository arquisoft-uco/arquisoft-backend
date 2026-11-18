package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.ConsultarAsesoresActualesProyectoGradoEstudianteInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresActualesProyectoGradoEstudianteQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarAsesoresActualesProyectoGradoEstudianteController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class,
        ConsultarAsesoresActualesProyectoGradoEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarAsesoresActualesProyectoGradoEstudianteControllerTest {

    private static final String RUTA = "/proyectos-grado/estudiante/asesores-actuales";
    private static final UUID ESTUDIANTE = UtilUUID.generarNuevoUUID();

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
                            .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarAsesoresActualesProyectoGradoEstudianteInteractor interactor;

    @Test
    void debeResponder200ConLista_cuandoHayAsesores() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of(
                new AsesorActualProyectoGradoReadModel(proyecto, "Sistema de gestion", asesor, "1001", "Ana",
                        "ana@soyuco.edu.co")));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(ESTUDIANTE.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].proyectoGradoId").value(proyecto.toString()))
                .andExpect(jsonPath("$[0].tituloProyecto").value("Sistema de gestion"))
                .andExpect(jsonPath("$[0].asesorId").value(asesor.toString()))
                .andExpect(jsonPath("$[0].identificador").value("1001"))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].email").value("ana@soyuco.edu.co"));
        var captor = ArgumentCaptor.forClass(ConsultarAsesoresActualesProyectoGradoEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().estudiante()).isEqualTo(ESTUDIANTE);
    }

    @Test
    void debeResponder200Vacio_cuandoNoHay() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(ESTUDIANTE.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)).andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
