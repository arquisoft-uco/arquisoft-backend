package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.ModificarInformacionTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.ModificarInformacionTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaDuplicadaException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarInformacionTareaController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ModificarInformacionTareaControllerTest.TestSecurityConfig.class})
class ModificarInformacionTareaControllerTest {

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
    private ModificarInformacionTareaInteractor interactor;

    private static String body(String descripcion, String asesor) {
        return """
                {
                  "descripcion": "%s",
                  "asesor": "%s"
                }
                """.formatted(descripcion, asesor);
    }

    @Test
    void debeResponder204_cuandoDatosValidos() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        mockMvc.perform(patch(RUTA, tarea)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Redactar capitulo", asesor.toString())))
                // Assert
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(ModificarInformacionTareaCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().tarea()).isEqualTo(tarea);
        assertThat(captor.getValue().descripcion()).isEqualTo("Redactar capitulo");
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeResponder400ConFieldErrors_cuandoDescripcionVacia() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(" ", UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='" + MapasRutaFields.Tarea.DESCRIPCION + "')]")
                        .exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400_cuandoTareaIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, "no-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Redactar capitulo", UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Redactar capitulo", UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoTareaNoExiste() throws Exception {
        // Arrange
        var tarea = UtilUUID.generarNuevoUUID();
        doThrow(new TareaNoEncontradaException(tarea)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, tarea)
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Redactar capitulo", UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.Tarea.TAREA_NO_ENCONTRADA));
    }

    @Test
    void debeResponder422_cuandoDescripcionDuplicada() throws Exception {
        // Arrange
        doThrow(new TareaDuplicadaException("Redactar capitulo", UtilUUID.generarNuevoUUID()))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Redactar capitulo", UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.Tarea.TAREA_DUPLICADA));
    }
}
