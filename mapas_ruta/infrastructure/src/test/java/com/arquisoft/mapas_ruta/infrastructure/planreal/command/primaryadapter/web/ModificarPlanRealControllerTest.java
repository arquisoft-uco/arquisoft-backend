package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.ModificarPlanRealInteractor;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.ModificarPlanRealCommand;
import com.arquisoft.mapas_ruta.domain.planreal.exception.PlanRealNoEncontradoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.message.constant.MapasRutaLimits;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarPlanRealController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ModificarPlanRealControllerTest.TestSecurityConfig.class})
class ModificarPlanRealControllerTest {

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
    private ModificarPlanRealInteractor interactor;

    @Test
    void debeResponder204_cuandoDatosValidos() throws Exception {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();

        // Act
        mockMvc.perform(patch(RUTA, plan)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tiempoRealDias\": 5, \"descripcionReal\": \"  Nueva descripcion  \"}"))
                // Assert
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(ModificarPlanRealCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().planReal()).isEqualTo(plan);
        assertThat(captor.getValue().tiempoRealDias()).isEqualTo(5);
        assertThat(captor.getValue().descripcionReal()).isEqualTo("Nueva descripcion");
    }

    @Test
    void debeResponder204_cuandoDescripcionNoSeEnvia() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tiempoRealDias\": 1}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void debeResponder400ConFieldErrors_cuandoTiempoAusente() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='"
                        + MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS + "')]").exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400ConFieldErrors_cuandoTiempoMenorAlMinimoYDescripcionLarga() throws Exception {
        // Arrange
        var larga = "a".repeat(MapasRutaLimits.PlanReal.DESCRIPCION_REAL_MAX + 1);

        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tiempoRealDias\": 0, \"descripcionReal\": \"" + larga + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='"
                        + MapasRutaFields.PlanReal.TIEMPO_REAL_DIAS + "')]").exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='"
                        + MapasRutaFields.PlanReal.DESCRIPCION_REAL + "')]").exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tiempoRealDias\": 1}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoPlanNoEncontrado() throws Exception {
        // Arrange
        var plan = UtilUUID.generarNuevoUUID();
        doThrow(new PlanRealNoEncontradoException(plan)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, plan)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tiempoRealDias\": 1}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.PlanReal.PLAN_REAL_NO_ENCONTRADO));
    }
}
