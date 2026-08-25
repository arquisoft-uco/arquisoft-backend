package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.ValidarTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.ValidarTokenCommand;
import com.arquisoft.seguridad.infrastructure.filter.JwtBlacklistFilter;
import com.arquisoft.seguridad.infrastructure.filter.LimitadorSolicitudesFilter;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ValidarTokenController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtBlacklistFilter.class, LimitadorSolicitudesFilter.class}))
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class, SeguridadWebTestConfig.class, AppLoggerConfig.class})
class ValidarTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ValidarTokenInteractor validarTokenInteractor;

    @Test
    void debeRetornar200YEnlazarToken_cuandoParametroEsValido() throws Exception {
        // Arrange
        when(validarTokenInteractor.ejecutar(any())).thenReturn(
                new ValidacionTokenResult.Valida("uuid-estudiante-123", "estudiante@uco.edu.co"));
        ArgumentCaptor<ValidarTokenCommand> captor = ArgumentCaptor.forClass(ValidarTokenCommand.class);

        // Act
        mockMvc.perform(post("/auth/validate").param("token", "eyJhbGc-token-valido..."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.identidadId").value("uuid-estudiante-123"))
                .andExpect(jsonPath("$.correo").value("estudiante@uco.edu.co"));

        // Assert
        verify(validarTokenInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().token()).isEqualTo("eyJhbGc-token-valido...");
    }

    @Test
    void debeRetornar200ConValidoFalso_cuandoElTokenNoEsValido() throws Exception {
        // Arrange — un token expirado es el final normal de una sesion, no un error HTTP
        when(validarTokenInteractor.ejecutar(any())).thenReturn(new ValidacionTokenResult.Invalida());

        // Act / Assert
        mockMvc.perform(post("/auth/validate").param("token", "eyJhbGc-token-expirado..."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(false))
                .andExpect(jsonPath("$.identidadId").value(nullValue()))
                .andExpect(jsonPath("$.correo").value(nullValue()))
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    void debeRetornar400_cuandoFaltaElParametroToken() throws Exception {
        mockMvc.perform(post("/auth/validate"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoElTokenEstaEnBlanco() throws Exception {
        // Un token en blanco es un contrato de peticion incumplido, no una invariante de negocio:
        // ValidarTokenCommand.crear lo rechaza como ApplicationValidationException (400),
        // igual que RefrescarTokenCommand — antes se construia el dominio en la capa web y salia 422.
        mockMvc.perform(post("/auth/validate").param("token", "   "))
                .andExpect(status().isBadRequest());
    }
}

