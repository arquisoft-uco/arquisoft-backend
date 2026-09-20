package com.arquisoft.usuarios.infrastructure.estudiante.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.estudiante.command.primaryport.interactor.RemoverEstudianteInteractor;
import com.arquisoft.usuarios.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteNoEncontradoException;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverEstudianteController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverEstudianteControllerTest.TestSecurityConfig.class})
class RemoverEstudianteControllerTest {

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
    private RemoverEstudianteInteractor removerEstudianteInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ESTUDIANTE_DELETE));
    }

    @Test
    void debe204YDelegarConElUsuario_cuandoPeticionValida() throws Exception {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/usuarios/{usuarioId}/estudiante", usuario).with(administrador()))
                .andExpect(status().isNoContent());
        verify(removerEstudianteInteractor).ejecutar(new RemoverEstudianteCommand(usuario));
    }

    @Test
    void debe400SinInvocarInteractor_cuandoUsuarioIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/usuarios/{usuarioId}/estudiante", "no-es-uuid").with(administrador()))
                .andExpect(status().isBadRequest());
        verify(removerEstudianteInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/usuarios/{usuarioId}/estudiante", UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoFaltaElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/usuarios/{usuarioId}/estudiante", UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_CREATE))))
                .andExpect(status().isForbidden());
        verify(removerEstudianteInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422ConSuCodigo_cuandoEstudianteNoEstaVigente() throws Exception {
        // Arrange
        var usuario = UUID.randomUUID();
        doThrow(new EstudianteNoEncontradoException(usuario)).when(removerEstudianteInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete("/usuarios/{usuarioId}/estudiante", usuario).with(administrador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Estudiante.ESTUDIANTE_NO_ENCONTRADO));
    }

    @Test
    void debe503_cuandoElProveedorDeIdentidadNoEstaDisponible() throws Exception {
        // Arrange
        doThrow(new ProveedorIdentidadUsuarioNoDisponibleException("no disponible"))
                .when(removerEstudianteInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete("/usuarios/{usuarioId}/estudiante", UUID.randomUUID()).with(administrador()))
                .andExpect(status().isServiceUnavailable());
    }
}
