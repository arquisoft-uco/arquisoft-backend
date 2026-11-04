package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.interactor.ConsultarVersionesRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.primaryport.model.ConsultarVersionesRepositorioArtefactoQuery;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.VersionRepositorioArtefactoReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
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

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarVersionesRepositorioArtefactoController.class)
@Import({IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarVersionesRepositorioArtefactoControllerTest.TestSecurityConfig.class
})
class ConsultarVersionesRepositorioArtefactoControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint((request, response, error) ->
                                    response.sendError(401, "Unauthorized"))
                            .accessDeniedHandler((request, response, error) ->
                                    response.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarVersionesRepositorioArtefactoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static String ruta(Object repositorio) {
        return "/repositorio-artefactos/" + repositorio + "/versiones";
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()));
    }

    @Test
    void debeRetornar200ConLista_cuandoExistenVersiones() throws Exception {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of(new VersionRepositorioArtefactoReadModel(
                id, "Formato A", 2, Instant.parse("2026-10-20T10:00:00Z"), "application/pdf", 2048L,
                "formato.pdf", "a".repeat(64))));

        // Act & Assert
        mockMvc.perform(get(ruta(repositorio)).with(jwtConPermiso()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].nombreDocumento").value("Formato A"))
                .andExpect(jsonPath("$[0].numeroVersion").value(2))
                .andExpect(jsonPath("$[0].tipoContenido").value("application/pdf"))
                .andExpect(jsonPath("$[0].tamanoBytes").value(2048))
                .andExpect(jsonPath("$[0].nombreArchivo").value("formato.pdf"))
                .andExpect(jsonPath("$[0].hashSha256").value("a".repeat(64)))
                .andExpect(jsonPath("$[0].fechaCreacion").exists())
                .andExpect(jsonPath("$[0].uriArchivo").doesNotExist());
        var captor = ArgumentCaptor.forClass(ConsultarVersionesRepositorioArtefactoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().repositorioArtefacto()).isEqualTo(repositorio);
    }

    @Test
    void debeRetornar200Vacio_cuandoNoHayVersiones() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID())).with(jwtConPermiso()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeRetornar400_cuandoRepositorioIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta("no-es-uuid")).with(jwtConPermiso()))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(interactor);
    }
}
