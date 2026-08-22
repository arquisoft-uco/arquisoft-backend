package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.seguridad.application.auth.command.primaryport.interactor.AutenticarUsuarioInteractor;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.AutenticarUsuarioCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.infrastructure.filter.JwtBlacklistFilter;
import com.arquisoft.seguridad.infrastructure.filter.LimitadorSolicitudesFilter;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Se excluyen los filtros @Component del módulo: @WebMvcTest los registra por defecto y
// dependen de puertos de dominio que este slice no levanta.
@WebMvcTest(controllers = IniciarSesionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtBlacklistFilter.class, LimitadorSolicitudesFilter.class}))
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class, SeguridadWebTestConfig.class, AppLoggerConfig.class})
class IniciarSesionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutenticarUsuarioInteractor autenticarUsuarioInteractor;

    @Test
    void debeRetornar200YEnlazarCredenciales_cuandoBodyEsValido() throws Exception {
        // Arrange
        when(autenticarUsuarioInteractor.ejecutar(any())).thenReturn(
                new AutenticacionResult("access...", "refresh...", 3600L, "Bearer", "openid"));
        ArgumentCaptor<AutenticarUsuarioCommand> captor =
                ArgumentCaptor.forClass(AutenticarUsuarioCommand.class);

        // Act
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "estudiante@uco.edu.co",
                                  "contrasena": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access..."))
                .andExpect(jsonPath("$.expiresIn").value(3600));

        // Assert
        verify(autenticarUsuarioInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("estudiante@uco.edu.co");
        assertThat(captor.getValue().contrasena()).isEqualTo("password123");
    }

    @Test
    void debeRetornar400_cuandoEmailTieneFormatoInvalido() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "no-es-un-email",
                                  "contrasena": "password123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoContrasenaEsMuyCorta() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "estudiante@uco.edu.co",
                                  "contrasena": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoFaltaLaContrasena() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "estudiante@uco.edu.co"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
