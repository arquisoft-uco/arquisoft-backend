package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.interactor.RegistrarEvaluacionFichaPerfilInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import org.junit.jupiter.api.Test;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarEvaluacionFichaPerfilController.class)
@Import({IdentidadActualTestConfig.class, com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RegistrarEvaluacionFichaPerfilControllerTest.TestSecurityConfig.class})
class RegistrarEvaluacionFichaPerfilControllerTest {

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
    private RegistrarEvaluacionFichaPerfilInteractor registrarEvaluacionFichaPerfilInteractor;

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();

        when(registrarEvaluacionFichaPerfilInteractor.ejecutar(any())).thenReturn(new InicioEvaluacionFichaPerfilResult.Registrada(evaluacionId));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(representanteId.toString()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(evaluacionId.toString()));
    }

    @Test
    void debe200ConIdDeLaExistente_cuandoResultadoEsReactivada() throws Exception {
        // Arrange
        var fichaId = UtilUUID.generarNuevoUUID();
        var evaluacionId = UtilUUID.generarNuevoUUID();

        when(registrarEvaluacionFichaPerfilInteractor.ejecutar(any()))
                .thenReturn(new InicioEvaluacionFichaPerfilResult.Reactivada(evaluacionId));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(evaluacionId.toString()));
    }

    @Test
    void debe422_cuandoElInteractorLanzaUnaReglaDeDominio() throws Exception {
        // Arrange
        var fichaId = UtilUUID.generarNuevoUUID();
        var representanteId = UtilUUID.generarNuevoUUID();

        when(registrarEvaluacionFichaPerfilInteractor.ejecutar(any()))
                .thenThrow(new EvaluacionFichaPerfilDuplicadaException(representanteId, fichaId));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(representanteId.toString()))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe400_cuandoFichaIdMalformado() throws Exception {
        // Arrange
        String fichaIdMalformado = "no-es-uuid";

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaIdMalformado)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaId))
                .andExpect(status().isUnauthorized());
    }
}
