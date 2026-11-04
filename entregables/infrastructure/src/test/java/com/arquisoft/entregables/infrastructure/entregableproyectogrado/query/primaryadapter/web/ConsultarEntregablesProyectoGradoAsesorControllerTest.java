package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesProyectoGradoAsesorInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoAsesorQuery;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEntregablesProyectoGradoAsesorController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEntregablesProyectoGradoAsesorControllerTest.TestSecurityConfig.class})
class ConsultarEntregablesProyectoGradoAsesorControllerTest {

    private static final String RUTA = "/entregables-proyecto-grado/asesor";

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
    private ConsultarEntregablesProyectoGradoAsesorInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtAsesor(UUID asesor) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString()));
    }

    @Test
    void debeResponder200_cuandoNoHayCuerpo() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesorLlamante = UtilUUID.generarNuevoUUID();
        var readModel = new EntregableProyectoGradoReadModel(id, 2,
                new EntregableProyectoGradoReadModel.ProyectoGradoResumen(UtilUUID.generarNuevoUUID(), "Proyecto"),
                new EntregableProyectoGradoReadModel.AsesorResumen(UtilUUID.generarNuevoUUID(), "Ana", "ana@uco.edu.co"),
                new EntregableProyectoGradoReadModel.EstadoEntregableResumen("APROBADO", "Aprobado"));
        when(interactor.ejecutar(any(ConsultarEntregablesProyectoGradoAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel), 0, 10, 1L));

        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).with(jwtAsesor(asesorLlamante)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].versionEntregable").value(2))
                .andExpect(jsonPath("$.content[0].proyectoGrado.nombre").value("Proyecto"))
                .andExpect(jsonPath("$.content[0].asesor.email").value("ana@uco.edu.co"))
                .andExpect(jsonPath("$.content[0].estadoEntregable.id").value("APROBADO"));
        var captor = ArgumentCaptor.forClass(ConsultarEntregablesProyectoGradoAsesorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().asesor()).isEqualTo(asesorLlamante);
    }

    @Test
    void debeResponder200_conFiltros() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultarEntregablesProyectoGradoAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "ordenamiento": ["versionEntregable:desc"],
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "estadoEntregable",
                    "operador": "ES",
                    "valor": "APROBADO"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(body).with(jwtAsesor(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void debeResponder400_cuandoElFiltroEsInvalido() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultarEntregablesProyectoGradoAsesorQuery.class)))
                .thenThrow(new FiltroException("campo no permitido", "entregables.consulta.campo-filtro-no-permitido"));
        var body = """
                {"filtros": {"tipo": "PREDICADO", "campo": "campoInventado", "operador": "ES", "valor": "x"}}
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(body).with(jwtAsesor(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any(ConsultarEntregablesProyectoGradoAsesorQuery.class));
    }
}
