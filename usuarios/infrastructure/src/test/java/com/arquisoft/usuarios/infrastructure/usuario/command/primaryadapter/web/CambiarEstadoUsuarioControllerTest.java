package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.usuario.command.primaryport.interactor.CambiarEstadoUsuarioInteractor;
import com.arquisoft.usuarios.application.usuario.command.primaryport.model.CambiarEstadoUsuarioCommand;
import com.arquisoft.usuarios.domain.usuario.exception.EstadoUsuarioSinCambioException;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarEstadoUsuarioController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        CambiarEstadoUsuarioControllerTest.TestSecurityConfig.class})
class CambiarEstadoUsuarioControllerTest {

    private static final String RUTA = "/usuarios/{usuarioId}/estado";
    private static final String BODY_INACTIVO = "{\"estado\":\"INACTIVO\"}";

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
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
    private CambiarEstadoUsuarioInteractor cambiarEstadoUsuarioInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_ESTADO_UPDATE));
    }

    @Test
    void debe204YDelegarConUsuarioYEstado_cuandoPeticionValida() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, usuario).with(administrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_INACTIVO))
                .andExpect(status().isNoContent());
        verify(cambiarEstadoUsuarioInteractor).ejecutar(new CambiarEstadoUsuarioCommand(usuario, "INACTIVO"));
    }

    @Test
    void debe400SinInvocarInteractor_cuandoElBodyNoTraeEstado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID()).with(administrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value(UsuariosFields.Usuario.ESTADO));
        verify(cambiarEstadoUsuarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe400SinInvocarInteractor_cuandoUsuarioIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, "no-es-uuid").with(administrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_INACTIVO))
                .andExpect(status().isBadRequest());
        verify(cambiarEstadoUsuarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_INACTIVO))
                .andExpect(status().isUnauthorized());
        verify(cambiarEstadoUsuarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoSoloTieneElClientRoleDeModificarUsuario() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_INACTIVO))
                .andExpect(status().isForbidden());
        verify(cambiarEstadoUsuarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422ConSuCodigo_cuandoElEstadoEsElActual() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        doThrow(new EstadoUsuarioSinCambioException(usuario, "Inactivo"))
                .when(cambiarEstadoUsuarioInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, usuario).with(administrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_INACTIVO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Usuario.ESTADO_SIN_CAMBIO));
    }

    @Test
    void debe503_cuandoElProveedorDeIdentidadNoEstaDisponible() throws Exception {
        // Arrange
        doThrow(new ProveedorIdentidadUsuarioNoDisponibleException("no disponible"))
                .when(cambiarEstadoUsuarioInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID()).with(administrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_INACTIVO))
                .andExpect(status().isServiceUnavailable());
    }
}
