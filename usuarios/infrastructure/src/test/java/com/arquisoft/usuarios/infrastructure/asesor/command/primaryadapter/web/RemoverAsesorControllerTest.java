package com.arquisoft.usuarios.infrastructure.asesor.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.asesor.command.primaryport.interactor.RemoverAsesorInteractor;
import com.arquisoft.usuarios.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.usuarios.domain.asesor.exception.AsesorNoEncontradoException;
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

@WebMvcTest(RemoverAsesorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverAsesorControllerTest.TestSecurityConfig.class})
class RemoverAsesorControllerTest {

    private static final String RUTA = "/usuarios/{usuarioId}/asesor";

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
    private RemoverAsesorInteractor removerAsesorInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ASESOR_DELETE));
    }

    @Test
    void debe204YDelegarConElUsuario_cuandoPeticionValida() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isNoContent());
        verify(removerAsesorInteractor).ejecutar(new RemoverAsesorCommand(usuario));
    }

    @Test
    void debe400SinInvocarInteractor_cuandoUsuarioIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-es-uuid").with(administrador()))
                .andExpect(status().isBadRequest());
        verify(removerAsesorInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(removerAsesorInteractor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoSoloTieneElClientRoleDeRemoverEstudiante() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ESTUDIANTE_DELETE))))
                .andExpect(status().isForbidden());
        verify(removerAsesorInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422ConSuCodigo_cuandoAsesorNoEstaVigente() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        doThrow(new AsesorNoEncontradoException(usuario)).when(removerAsesorInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Asesor.ASESOR_NO_ENCONTRADO));
    }

    @Test
    void debe503_cuandoElProveedorDeIdentidadNoEstaDisponible() throws Exception {
        // Arrange
        doThrow(new ProveedorIdentidadUsuarioNoDisponibleException("no disponible"))
                .when(removerAsesorInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()).with(administrador()))
                .andExpect(status().isServiceUnavailable());
    }
}
