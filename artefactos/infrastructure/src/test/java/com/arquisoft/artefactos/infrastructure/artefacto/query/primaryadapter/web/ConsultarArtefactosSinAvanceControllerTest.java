package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.interactor.ConsultarArtefactosSinAvanceInteractor;
import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosSinAvanceQuery;
import com.arquisoft.artefactos.application.artefacto.query.readmodel.ArtefactoSinAvanceReadModel;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
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

@WebMvcTest(ConsultarArtefactosSinAvanceController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarArtefactosSinAvanceControllerTest.TestSecurityConfig.class})
class ConsultarArtefactosSinAvanceControllerTest {

    private static final String RUTA = "/artefactos/asesor/sin-avance";

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
    private ConsultarArtefactosSinAvanceInteractor consultarArtefactosSinAvanceInteractor;

    private static final UUID ASESOR_ID = UtilUUID.generarNuevoUUID();

    private static RequestPostProcessor tokenAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sub));
    }

    @Test
    void debeRetornar200ConResponseDTO_yConsultarConElSubjectDelToken() throws Exception {
        // Arrange
        var fecha = Instant.parse("2026-09-01T10:00:00Z");
        var artefacto = new ArtefactoSinAvanceReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Proyecto Alfa",
                UtilUUID.generarNuevoUUID(), "Documento", 2, "Repositorio", fecha, 61L, "Pendiente");
        var sinVersion = new ArtefactoSinAvanceReadModel(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Proyecto Beta",
                UtilUUID.generarNuevoUUID(), "Informe", 1, "Repositorio", null, null, null);
        when(consultarArtefactosSinAvanceInteractor.ejecutar(any(ConsultarArtefactosSinAvanceQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(artefacto, sinVersion), 0, 10, 2L));
        var body = """
                {
                  "diasSinAvance": 30,
                  "criterio": { "pagina": 0, "tamanio": 10 }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(tokenAsesor(ASESOR_ID.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(artefacto.id().toString()))
                .andExpect(jsonPath("$.content[0].nombreProyecto").value("Proyecto Alfa"))
                .andExpect(jsonPath("$.content[0].versionDocumento").value(2))
                .andExpect(jsonPath("$.content[0].diasSinAvance").value(61))
                .andExpect(jsonPath("$.content[0].estadoUltimaRevision").value("Pendiente"))
                .andExpect(jsonPath("$.content[1].fechaUltimaVersion").doesNotExist())
                .andExpect(jsonPath("$.content[1].diasSinAvance").doesNotExist())
                .andExpect(jsonPath("$.content[1].estadoUltimaRevision").doesNotExist())
                .andExpect(jsonPath("$.totalElements").value(2));

        var captor = ArgumentCaptor.forClass(ConsultarArtefactosSinAvanceQuery.class);
        verify(consultarArtefactosSinAvanceInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR_ID);
        assertThat(captor.getValue().diasSinAvance()).isEqualTo(30);
    }

    @Test
    void debeRetornar400_cuandoDiasSinAvanceEstaAusenteOFueraDeRango() throws Exception {
        // Arrange
        var sinDias = "{ \"criterio\": { \"pagina\": 0, \"tamanio\": 10 } }";
        var fueraDeRango = "{ \"diasSinAvance\": 366 }";

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sinDias)
                        .with(tokenAsesor(ASESOR_ID.toString())))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fueraDeRango)
                        .with(tokenAsesor(ASESOR_ID.toString())))
                .andExpect(status().isBadRequest());
        verify(consultarArtefactosSinAvanceInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar400_cuandoLaPeticionNoTraeBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(tokenAsesor(ASESOR_ID.toString())))
                .andExpect(status().isBadRequest());
        verify(consultarArtefactosSinAvanceInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content("{\"diasSinAvance\":30}"))
                .andExpect(status().isUnauthorized());
    }
}
