package com.arquisoft.usuarios.infrastructure.bibliotecario.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.bibliotecario.command.primaryport.interactor.RemoverBibliotecarioInteractor;
import com.arquisoft.usuarios.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioNoEncontradoException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverBibliotecarioController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverBibliotecarioControllerTest.TestSecurityConfig.class})
class RemoverBibliotecarioControllerTest {

    private static final String RUTA = "/usuarios/{usuarioId}/bibliotecario";

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
    private RemoverBibliotecarioInteractor removerBibliotecarioInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.BIBLIOTECARIO_DELETE));
    }

    @Test
    void debe204YDelegarConElUsuario_cuandoPeticionValida() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isNoContent());
        verify(removerBibliotecarioInteractor).ejecutar(new RemoverBibliotecarioCommand(usuario));
    }

    @Test
    void debe400SinInvocarInteractor_cuandoUsuarioIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-es-uuid").with(administrador()))
                .andExpect(status().isBadRequest());
        verify(removerBibliotecarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(removerBibliotecarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoSoloTieneElClientRoleDeRemoverCoordinador() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.COORDINADOR_DELETE))))
                .andExpect(status().isForbidden());
        verify(removerBibliotecarioInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422ConSuCodigo_cuandoBibliotecarioNoEstaVigente() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        doThrow(new BibliotecarioNoEncontradoException(usuario)).when(removerBibliotecarioInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Bibliotecario.BIBLIOTECARIO_NO_ENCONTRADO));
    }

    @Test
    void debe503_cuandoElProveedorDeIdentidadNoEstaDisponible() throws Exception {
        // Arrange
        doThrow(new ProveedorIdentidadUsuarioNoDisponibleException("no disponible"))
                .when(removerBibliotecarioInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()).with(administrador()))
                .andExpect(status().isServiceUnavailable());
    }
}
