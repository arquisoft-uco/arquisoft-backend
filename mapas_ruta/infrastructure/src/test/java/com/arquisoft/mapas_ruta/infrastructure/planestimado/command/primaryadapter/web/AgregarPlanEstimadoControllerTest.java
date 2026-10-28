package com.arquisoft.mapas_ruta.infrastructure.planestimado.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.interactor.AgregarPlanEstimadoInteractor;
import com.arquisoft.mapas_ruta.application.planestimado.command.primaryport.model.AgregarPlanEstimadoCommand;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.PlanEstimadoDuplicadoException;
import com.arquisoft.mapas_ruta.domain.planestimado.exception.TiempoPlanesExcedePlazoException;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaNoEncontradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgregarPlanEstimadoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AgregarPlanEstimadoControllerTest.TestSecurityConfig.class})
class AgregarPlanEstimadoControllerTest {

    private static final String RUTA = "/tareas/{tareaId}/planes-estimados";

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
    private AgregarPlanEstimadoInteractor interactor;

    private static String body(String tiempo, String descripcion) {
        return """
                {
                  "tiempoEstimadoDias": %s,
                  "descripcionEstimado": %s
                }
                """.formatted(tiempo, descripcion);
    }

    @Test
    void debeResponder201ConId_cuandoDatosValidos() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act
        mockMvc.perform(post(RUTA, tarea)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("5", "\"Revisar el capitulo\"")))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
        var captor = ArgumentCaptor.forClass(AgregarPlanEstimadoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().tarea()).isEqualTo(tarea);
        assertThat(captor.getValue().tiempoEstimadoDias()).isEqualTo(5);
        assertThat(captor.getValue().descripcionEstimado()).isEqualTo("Revisar el capitulo");
    }

    @Test
    void debeResponder201_cuandoDescripcionNoSeEnvia() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(UtilUUID.generarNuevoUUID());

        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tiempoEstimadoDias\": 2}"))
                .andExpect(status().isCreated());
    }

    @Test
    void debeResponder400ConFieldErrors_cuandoTiempoInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("0", "null")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='"
                        + MapasRutaFields.PlanEstimado.TIEMPO_ESTIMADO_DIAS + "')]").exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400_cuandoTareaIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, "no-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("3", "null")))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("3", "null")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoTareaNoExiste() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        doThrow(new TareaNoEncontradaException(tarea)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA, tarea)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("3", "null")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.Tarea.TAREA_NO_ENCONTRADA));
    }

    @Test
    void debeResponder422_cuandoPlanDuplicado() throws Exception {
        // Arrange
        doThrow(new PlanEstimadoDuplicadoException("Redactar capitulo", UtilUUID.generarNuevoUUID()))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("3", "null")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_DUPLICADO));
    }

    @Test
    void debeResponder422_cuandoTiempoExcedeElPlazo() throws Exception {
        // Arrange
        doThrow(new TiempoPlanesExcedePlazoException(UtilUUID.generarNuevoUUID(), 8, 5, 10))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("5", "null")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(MapasRutaCodes.PlanEstimado.PLAN_ESTIMADO_TIEMPO_EXCEDE_PLAZO));
    }
}
