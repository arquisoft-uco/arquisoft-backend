package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.interactor.ConsultarPlanesEstimadosProyectoGradoAsesorInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.query.primaryport.model.ConsultarPlanesEstimadosProyectoGradoAsesorQuery;
import com.arquisoft.mapas_ruta.application.planestimado.query.readmodel.PlanEstimadoProyectoReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
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

@WebMvcTest(ConsultarPlanesEstimadosProyectoGradoAsesorController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarPlanesEstimadosProyectoGradoAsesorControllerTest.TestSecurityConfig.class})
class ConsultarPlanesEstimadosProyectoGradoAsesorControllerTest {

    private static final String RUTA = "/mapas-ruta/proyectos-grado/{proyectoGradoId}/planes-estimados/asesor";

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
    private ConsultarPlanesEstimadosProyectoGradoAsesorInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }

    @Test
    void debeResponder200_conLaPaginaDePlanes() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var plan = UtilUUID.generarNuevoUUID();
        var tarea = UtilUUID.generarNuevoUUID();
        var mapa = UtilUUID.generarNuevoUUID();
        var readModel = new PlanEstimadoProyectoReadModel(plan, tarea, "Redactar capitulo", mapa, 5, "Revisar");
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(readModel), 1, 5, 6));
        var cuerpo = "{\"pagina\":1,\"tamanio\":5,\"ordenamiento\":[\"descripcionTarea:DESC\"]}";

        // Act
        mockMvc.perform(post(RUTA, proyecto).with(jwtAsesor())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(plan.toString()))
                .andExpect(jsonPath("$.content[0].tarea").value(tarea.toString()))
                .andExpect(jsonPath("$.content[0].descripcionTarea").value("Redactar capitulo"))
                .andExpect(jsonPath("$.content[0].mapaRuta").value(mapa.toString()))
                .andExpect(jsonPath("$.content[0].tiempoEstimadoDias").value(5))
                .andExpect(jsonPath("$.content[0].descripcionEstimado").value("Revisar"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(6));
        var captor = ArgumentCaptor.forClass(ConsultarPlanesEstimadosProyectoGradoAsesorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().criterio().pagina()).isEqualTo(1);
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(5);
    }

    @Test
    void debeResponder200ConPaginaPorDefecto_cuandoNoHayCuerpo() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0));

        // Act
        mockMvc.perform(post(RUTA, proyecto).with(jwtAsesor()))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.empty").value(true));
        var captor = ArgumentCaptor.forClass(ConsultarPlanesEstimadosProyectoGradoAsesorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().criterio().pagina()).isZero();
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(10);
    }

    @Test
    void debeResponder400_cuandoProyectoGradoIdInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, "no-es-un-uuid").with(jwtAsesor()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
