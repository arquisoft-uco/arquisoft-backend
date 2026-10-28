package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.RemoverInformacionTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.RemoverInformacionTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoRemovibleException;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;
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

@WebMvcTest(RemoverInformacionTareaController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverInformacionTareaControllerTest.TestSecurityConfig.class})
class RemoverInformacionTareaControllerTest {

    private static final String RUTA = "/tareas/{tareaId}";

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
    private RemoverInformacionTareaInteractor interactor;

    @Test
    void debeRetornar204_cuandoTareaRemovida() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();

        // Act
        mockMvc.perform(delete(RUTA, tarea).with(SecurityMockMvcRequestPostProcessors.jwt()))
                // Assert
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(RemoverInformacionTareaCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().tarea()).isEqualTo(tarea);
    }

    @Test
    void debeRetornar400_cuandoTareaIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-uuid").with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoRuleRechaza() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        doThrow(new TareaNoRemovibleException(tarea, EstadoItem.FINALIZADA))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, tarea).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.Tarea.TAREA_NO_REMOVIBLE));
    }
}
