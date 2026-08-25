package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.RefrescarTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.RefrescarTokenCommand;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.infrastructure.filter.JwtBlacklistFilter;
import com.arquisoft.seguridad.infrastructure.filter.LimitadorSolicitudesFilter;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RefrescarTokenController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtBlacklistFilter.class, LimitadorSolicitudesFilter.class}))
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class, SeguridadWebTestConfig.class, AppLoggerConfig.class})
class RefrescarTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RefrescarTokenInteractor refrescarTokenInteractor;

    @Test
    void debeRetornar200YEnlazarRefreshToken_cuandoBodyEsValido() throws Exception {
        // Arrange
        when(refrescarTokenInteractor.ejecutar(any())).thenReturn(
                new RefrescoTokenResult("access-new...", "refresh-new...", 3600L, "Bearer", "openid"));

        // Act
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-old..."
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-new..."));

        // Assert — el DTO recibio el valor del JSON, no null
        verify(refrescarTokenInteractor).ejecutar(new RefrescarTokenCommand("refresh-old..."));
    }

    @Test
    void debeRetornar400_cuandoRefreshTokenEstaVacio() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
