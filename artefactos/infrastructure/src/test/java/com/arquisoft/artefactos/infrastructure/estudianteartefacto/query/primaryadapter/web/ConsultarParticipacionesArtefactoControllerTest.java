package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.interactor.ConsultarParticipacionesArtefactoInteractor;
import com.arquisoft.artefactos.application.estudianteartefacto.query.primaryport.model.ConsultarParticipacionesArtefactoQuery;
import com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel.ParticipacionArtefactoReadModel;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarParticipacionesArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarParticipacionesArtefactoControllerTest.TestSecurityConfig.class})
class ConsultarParticipacionesArtefactoControllerTest {

    private static final String RUTA = "/artefactos/estudiante/participaciones";

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
    private ConsultarParticipacionesArtefactoInteractor interactor;

    @Test
    void debe200ConLaLista_cuandoHayArtefactos() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var readModel = new ParticipacionArtefactoReadModel(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Proyecto Alfa", UtilUUID.generarNuevoUUID(), "Informe", 2, "Repo",
                asesor, "Ana Asesora", false);
        when(interactor.ejecutar(any(ConsultarParticipacionesArtefactoQuery.class))).thenReturn(List.of(readModel));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(
                        SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(estudiante.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(readModel.id().toString()))
                .andExpect(jsonPath("$[0].proyectoGrado").value(readModel.proyectoGrado().toString()))
                .andExpect(jsonPath("$[0].nombreProyecto").value("Proyecto Alfa"))
                .andExpect(jsonPath("$[0].versionRepositorioArtefacto")
                        .value(readModel.versionRepositorioArtefacto().toString()))
                .andExpect(jsonPath("$[0].nombreDocumento").value("Informe"))
                .andExpect(jsonPath("$[0].versionDocumento").value(2))
                .andExpect(jsonPath("$[0].nombreRepositorio").value("Repo"))
                .andExpect(jsonPath("$[0].asesor").value(asesor.toString()))
                .andExpect(jsonPath("$[0].nombreAsesor").value("Ana Asesora"));
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayArtefactos() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarParticipacionesArtefactoQuery.class))).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(
                        SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(estudiante.toString()))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debePasarElSujetoDelTokenAlQuery_cuandoConsulta() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarParticipacionesArtefactoQuery.class))).thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA).with(
                        SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(estudiante.toString()))))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarParticipacionesArtefactoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());

        verify(interactor, never()).ejecutar(any());
    }
}
