package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.ConsultarPlanesEstimadosTareaEstudianteInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosTareaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarPlanesEstimadosTareaEstudianteController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarPlanesEstimadosTareaEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarPlanesEstimadosTareaEstudianteControllerTest {

    private static final String RUTA = "/tareas/{tareaId}/planes-estimados/estudiante";

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
    private ConsultarPlanesEstimadosTareaEstudianteInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtEstudiante() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }

    @Test
    void debeResponder200_cuandoConsultaValida() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var plan = UtilUUID.generarNuevoUUID();
        var readModel = new PlanEstimadoReadModel(plan, tarea, 7, "Revisar capitulo");
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(readModel), 1, 5, 6));
        var cuerpo = "{\"pagina\":1,\"tamanio\":5,\"ordenamiento\":[\"tiempoEstimadoDias:DESC\"]}";

        // Act
        mockMvc.perform(post(RUTA, tarea).with(jwtEstudiante())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(plan.toString()))
                .andExpect(jsonPath("$.content[0].tarea").value(tarea.toString()))
                .andExpect(jsonPath("$.content[0].tiempoEstimadoDias").value(7))
                .andExpect(jsonPath("$.content[0].descripcionEstimado").value("Revisar capitulo"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(6));
        var captor = ArgumentCaptor.forClass(ConsultarPlanesEstimadosTareaEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().tarea()).isEqualTo(tarea);
        assertThat(captor.getValue().criterio().pagina()).isEqualTo(1);
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(5);
    }

    @Test
    void debeResponder200_cuandoSinCuerpo() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0));

        // Act
        mockMvc.perform(post(RUTA, tarea).with(jwtEstudiante()))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.empty").value(true));
        var captor = ArgumentCaptor.forClass(ConsultarPlanesEstimadosTareaEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().criterio().pagina()).isZero();
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(10);
    }

    @Test
    void debeResponder400_cuandoTareaIdMalFormado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, "no-es-un-uuid").with(jwtEstudiante()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400_cuandoFiltroInvalido() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new FiltroInvalidoException("campo no permitido"));

        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID()).with(jwtEstudiante())
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
