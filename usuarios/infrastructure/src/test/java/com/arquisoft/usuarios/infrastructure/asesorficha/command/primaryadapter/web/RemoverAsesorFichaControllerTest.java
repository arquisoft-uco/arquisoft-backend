package com.arquisoft.usuarios.infrastructure.asesorficha.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.asesorficha.command.primaryport.interactor.RemoverAsesorFichaInteractor;
import com.arquisoft.usuarios.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaNoEncontradoException;
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

@WebMvcTest(RemoverAsesorFichaController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverAsesorFichaControllerTest.TestSecurityConfig.class})
class RemoverAsesorFichaControllerTest {

    private static final String RUTA = "/usuarios/{usuarioId}/asesor-ficha";

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
    private RemoverAsesorFichaInteractor removerAsesorFichaInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ASESOR_FICHA_DELETE));
    }

    @Test
    void debe204YDelegarConElUsuario_cuandoPeticionValida() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isNoContent());
        verify(removerAsesorFichaInteractor).ejecutar(new RemoverAsesorFichaCommand(usuario));
    }

    @Test
    void debe400SinInvocarInteractor_cuandoUsuarioIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-es-uuid").with(administrador()))
                .andExpect(status().isBadRequest());
        verify(removerAsesorFichaInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(removerAsesorFichaInteractor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoSoloTieneElClientRoleDeRemoverAsesor() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ASESOR_DELETE))))
                .andExpect(status().isForbidden());
        verify(removerAsesorFichaInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422ConSuCodigo_cuandoAsesorFichaNoEstaVigente() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        doThrow(new AsesorFichaNoEncontradoException(usuario)).when(removerAsesorFichaInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, usuario).with(administrador()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.AsesorFicha.ASESOR_FICHA_NO_ENCONTRADO));
    }

    @Test
    void debe503_cuandoElProveedorDeIdentidadNoEstaDisponible() throws Exception {
        // Arrange
        doThrow(new ProveedorIdentidadUsuarioNoDisponibleException("no disponible"))
                .when(removerAsesorFichaInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID()).with(administrador()))
                .andExpect(status().isServiceUnavailable());
    }
}
