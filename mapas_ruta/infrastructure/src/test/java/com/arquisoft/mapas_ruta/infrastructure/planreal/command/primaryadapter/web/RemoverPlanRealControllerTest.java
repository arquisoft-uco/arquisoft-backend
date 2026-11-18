package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.RemoverPlanRealInteractor;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.RemoverPlanRealCommand;
import com.arquisoft.mapas_ruta.domain.planreal.exception.PlanRealNoEncontradoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverPlanRealController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverPlanRealControllerTest.TestSecurityConfig.class})
class RemoverPlanRealControllerTest {

    private static final String RUTA = "/mapas-ruta/planes-reales/{planRealId}";

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
    private RemoverPlanRealInteractor interactor;

    @Test
    void debeResponder204_cuandoPlanExiste() throws Exception {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();

        // Act
        mockMvc.perform(delete(RUTA, plan).with(SecurityMockMvcRequestPostProcessors.jwt()))
                // Assert
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(RemoverPlanRealCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().planReal()).isEqualTo(plan);
    }

    @Test
    void debeResponder422_cuandoPlanNoExiste() throws Exception {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        doThrow(new PlanRealNoEncontradoException(plan)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, plan).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(MapasRutaCodes.PlanReal.PLAN_REAL_NO_ENCONTRADO));
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400_cuandoIdNoEsUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-uuid").with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }
}
