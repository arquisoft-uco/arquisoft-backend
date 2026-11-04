package com.arquisoft.usuarios.infrastructure.jurado.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.jurado.command.primaryport.interactor.RemoverJuradoInteractor;
import com.arquisoft.usuarios.application.jurado.command.primaryport.model.RemoverJuradoCommand;
import com.arquisoft.usuarios.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

@WebMvcTest(RemoverJuradoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverJuradoControllerTest.TestSecurityConfig.class})
class RemoverJuradoControllerTest {

    private static final String RUTA = "/usuarios/{usuarioId}/jurado";

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
    private RemoverJuradoInteractor removerJuradoInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }

    @Test
    void debeResponder204YDelegarConElUsuario_cuandoSeRemueve() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isNoContent());
        verify(removerJuradoInteractor).ejecutar(new RemoverJuradoCommand(usuario));
    }

    @Test
    void debeResponder400_cuandoUsuarioIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-es-uuid").with(administrador()))
                .andExpect(status().isBadRequest());
        verify(removerJuradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(removerJuradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422ConSuCodigo_cuandoElJuradoNoEstaVigente() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        doThrow(new JuradoNoEncontradoException(usuario, "Ana Pérez")).when(removerJuradoInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Jurado.JURADO_NO_ENCONTRADO));
    }

    @Test
    void debeResponder422ConSuCodigo_cuandoElUsuarioNoExiste() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        doThrow(new UsuarioNoEncontradoException(usuario)).when(removerJuradoInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Usuario.NO_ENCONTRADO));
    }
}
