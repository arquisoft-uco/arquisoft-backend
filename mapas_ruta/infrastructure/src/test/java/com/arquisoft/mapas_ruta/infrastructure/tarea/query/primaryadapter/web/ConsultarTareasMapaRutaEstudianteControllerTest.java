package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.interactor.ConsultarTareasMapaRutaEstudianteInteractor;
import com.arquisoft.mapas_ruta.application.tarea.query.primaryport.model.ConsultarTareasMapaRutaEstudianteQuery;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarTareasMapaRutaEstudianteController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarTareasMapaRutaEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarTareasMapaRutaEstudianteControllerTest {

    private static final String RUTA = "/mapas-ruta/{mapaRutaId}/tareas/estudiante";

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
    private ConsultarTareasMapaRutaEstudianteInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtEstudiante() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }

    @Test
    void debeResponder200_conLaPaginaDeTareas() throws Exception {
        // Arrange
        var mapa = UtilUUID.generarNuevoUUID();
        var tarea = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new TareaReadModel(tarea, "Redactar capitulo", mapa, LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 1), "NUEVA", "Nueva", asesor, "Ana Perez", "ana@uco.edu.co");
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(readModel), 1, 5, 6));
        var cuerpo = "{\"pagina\":1,\"tamanio\":5,\"ordenamiento\":[\"estadoItem:DESC\"]}";

        // Act
        mockMvc.perform(post(RUTA, mapa).with(jwtEstudiante())
                        .contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(tarea.toString()))
                .andExpect(jsonPath("$.content[0].descripcion").value("Redactar capitulo"))
                .andExpect(jsonPath("$.content[0].mapaRuta").value(mapa.toString()))
                .andExpect(jsonPath("$.content[0].estadoItem").value("NUEVA"))
                .andExpect(jsonPath("$.content[0].asesor").value(asesor.toString()))
                .andExpect(jsonPath("$.content[0].emailAsesor").value("ana@uco.edu.co"))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(6));
        var captor = ArgumentCaptor.forClass(ConsultarTareasMapaRutaEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().mapaRuta()).isEqualTo(mapa);
        assertThat(captor.getValue().criterio().pagina()).isEqualTo(1);
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(5);
    }

    @Test
    void debeResponder200ConPaginaPorDefecto_cuandoNoHayCuerpo() throws Exception {
        // Arrange
        var mapa = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0));

        // Act
        mockMvc.perform(post(RUTA, mapa).with(jwtEstudiante()))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.empty").value(true));
        var captor = ArgumentCaptor.forClass(ConsultarTareasMapaRutaEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().criterio().pagina()).isZero();
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(10);
    }

    @Test
    void debeResponder400_cuandoMapaRutaIdInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, "no-es-un-uuid").with(jwtEstudiante()))
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
