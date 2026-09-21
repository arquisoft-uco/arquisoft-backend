package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.ModificarObservacionItemJuradoInteractor;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.model.ModificarObservacionItemJuradoCommand;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.ObservacionItemJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarObservacionItemJuradoController.class)
@Import({
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class,
        ModificarObservacionItemJuradoControllerTest.TestSecurityConfig.class
})
class ModificarObservacionItemJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/observaciones-item-jurado/{observacionId}";
    private static final String BODY_VALIDO = """
            { "descripcion": "Nueva descripción de la observación" }
            """;

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
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
    private ModificarObservacionItemJuradoInteractor interactor;

    @Test
    void debeRetornar204SinCuerpoYDelegarCommand_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var observacionId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacionId)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        verify(interactor).ejecutar(ModificarObservacionItemJuradoCommand.crear(
                observacionId, "Nueva descripción de la observación"));
    }

    @Test
    void debeRetornar400ConFieldErrorsYNoInvocarInteractor_cuandoDescripcionEstaEnBlanco() throws Exception {
        // Arrange
        var observacionId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacionId)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descripcion\": \"   \" }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(EvaluacionesFields.ObservacionItemJurado.DESCRIPCION));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401YNoInvocarInteractor_cuandoNoEstaAutenticado() throws Exception {
        // Arrange
        var observacionId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar403YNoInvocarInteractor_cuandoNoTieneElClientRoleDeModificacion() throws Exception {
        // Arrange
        var observacionId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacionId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        EvaluacionesAuthorities.OBSERVACION_ITEM_JURADO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoLaObservacionNoExiste() throws Exception {
        // Arrange
        var observacionId = UUID.randomUUID();
        doThrow(new ObservacionItemJuradoNoEncontradaException(observacionId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacionId)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.ObservacionItemJurado.NO_ENCONTRADA));
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.OBSERVACION_ITEM_JURADO_UPDATE));
    }
}
