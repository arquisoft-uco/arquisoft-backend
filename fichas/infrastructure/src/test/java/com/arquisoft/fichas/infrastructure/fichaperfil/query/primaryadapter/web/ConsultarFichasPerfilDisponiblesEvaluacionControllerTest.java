package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilDisponiblesEvaluacionInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilDisponiblesEvaluacionQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
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

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarFichasPerfilDisponiblesEvaluacionController.class)
@Import({AppLoggerConfig.class, IdentidadActualTestConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarFichasPerfilDisponiblesEvaluacionControllerTest.TestSecurityConfig.class})
class ConsultarFichasPerfilDisponiblesEvaluacionControllerTest {

    private static final String RUTA = "/fichas-perfil/disponibles-evaluacion";

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
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarFichasPerfilDisponiblesEvaluacionInteractor consultarFichasPerfilDisponiblesEvaluacionInteractor;

    @Test
    void debeResponder200ConLasFichas_cuandoHayResultados() throws Exception {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        var asesor = new AsesorFichaReadModel(UtilUUID.generarNuevoUUID(), "DOC-001", "Laura Gomez",
                "laura@soyuco.edu.co");
        var estado = new EstadoFichaPerfilReadModel("DISPONIBLE_PARA_EVALUACION",
                "Disponible Para Evaluacion", Instant.parse("2026-11-01T10:00:00Z"));
        var ficha = new FichaPerfilReadModel(UtilUUID.generarNuevoUUID(), "Arquisoft Backend", asesor, estado);
        when(consultarFichasPerfilDisponiblesEvaluacionInteractor
                .ejecutar(any(ConsultarFichasPerfilDisponiblesEvaluacionQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(ficha), 0, 10, 1L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(representante.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tituloProyecto").value("Arquisoft Backend"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void debeResponder200ConPaginaVacia_cuandoNoHayFichas() throws Exception {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        when(consultarFichasPerfilDisponiblesEvaluacionInteractor
                .ejecutar(any(ConsultarFichasPerfilDisponiblesEvaluacionQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(representante.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debePasarLaIdentidadComoRepresentante_cuandoConsulta() throws Exception {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        when(consultarFichasPerfilDisponiblesEvaluacionInteractor
                .ejecutar(any(ConsultarFichasPerfilDisponiblesEvaluacionQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(representante.toString()))))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarFichasPerfilDisponiblesEvaluacionQuery.class);
        verify(consultarFichasPerfilDisponiblesEvaluacionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().representanteComite()).isEqualTo(representante);
    }

    @Test
    void debeResponder400_cuandoElFiltroUsaEstadoFicha() throws Exception {
        // Arrange
        var representante = UtilUUID.generarNuevoUUID();
        when(consultarFichasPerfilDisponiblesEvaluacionInteractor
                .ejecutar(any(ConsultarFichasPerfilDisponiblesEvaluacionQuery.class)))
                .thenThrow(new FiltroException("campo no filtrable: estadoFicha",
                        "fichas.consulta.campo-no-filtrable"));
        var body = """
                {
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "estadoFicha",
                    "operador": "ES",
                    "valor": "APROBADA"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(representante.toString()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder401_cuandoNoHayCredenciales() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
