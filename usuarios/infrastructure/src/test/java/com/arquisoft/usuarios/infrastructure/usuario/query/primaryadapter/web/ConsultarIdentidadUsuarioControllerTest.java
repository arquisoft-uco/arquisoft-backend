package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarIdentidadUsuarioInteractor;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarIdentidadUsuarioQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarIdentidadUsuarioController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarIdentidadUsuarioControllerTest.TestSecurityConfig.class})
class ConsultarIdentidadUsuarioControllerTest {

    private static final String RUTA = "/usuarios/{usuarioId}";

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
    private ConsultarIdentidadUsuarioInteractor consultarIdentidadUsuarioInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_IDENTIDAD_VIEW));
    }

    @Test
    void debe200ConNombresYApellidos_cuandoAutorizado() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(consultarIdentidadUsuarioInteractor.ejecutar(any()))
                .thenReturn(new IdentidadUsuarioReadModel("Ana María", "Ramírez Díaz"));

        // Act & Assert
        mockMvc.perform(get(RUTA, usuario).with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombres").value("Ana María"))
                .andExpect(jsonPath("$.apellidos").value("Ramírez Díaz"));
        verify(consultarIdentidadUsuarioInteractor).ejecutar(new ConsultarIdentidadUsuarioQuery(usuario));
    }

    @Test
    void debe400SinInvocarInteractor_cuandoUsuarioIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid").with(administrador()))
                .andExpect(status().isBadRequest());
        verify(consultarIdentidadUsuarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(consultarIdentidadUsuarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoSoloTieneElClientRoleDeUsuarioAdministradorView() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_ADMINISTRADOR_VIEW))))
                .andExpect(status().isForbidden());
        verify(consultarIdentidadUsuarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422ConSuCodigo_cuandoLaReglaDeDominioRechazaAlUsuario() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(consultarIdentidadUsuarioInteractor.ejecutar(any()))
                .thenThrow(new UsuarioNoEncontradoException(usuario));

        // Act & Assert
        mockMvc.perform(get(RUTA, usuario).with(administrador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Usuario.NO_ENCONTRADO));
    }

    @Test
    void debe503_cuandoElProveedorDeIdentidadNoEstaDisponible() throws Exception {
        // Arrange
        when(consultarIdentidadUsuarioInteractor.ejecutar(any()))
                .thenThrow(new ProveedorIdentidadUsuarioNoDisponibleException("no disponible"));

        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()).with(administrador()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Usuario.IDP_NO_DISPONIBLE));
    }
}
