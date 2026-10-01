package com.arquisoft.usuarios.infrastructure.estadousuario.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.estadousuario.query.primaryport.interactor.ConsultarEstadosUsuarioInteractor;
import com.arquisoft.usuarios.application.estadousuario.query.readmodel.EstadoUsuarioReadModel;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
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

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosUsuarioController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosUsuarioControllerTest.TestSecurityConfig.class})
class ConsultarEstadosUsuarioControllerTest {

    private static final String RUTA = "/usuarios/estados";

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
    private ConsultarEstadosUsuarioInteractor consultarEstadosUsuarioInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ESTADO_USUARIO_VIEW));
    }

    @Test
    void debe200ConLosEstados_cuandoTieneElClientRole() throws Exception {
        // Arrange
        when(consultarEstadosUsuarioInteractor.ejecutar()).thenReturn(List.of(
                new EstadoUsuarioReadModel("ACTIVO", "Activo", "Indica que un usuario puede desempeñarse"),
                new EstadoUsuarioReadModel("INACTIVO", "Inactivo", "Indica que un usuario no puede desempeñarse")));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("ACTIVO"))
                .andExpect(jsonPath("$[0].nombre").value("Activo"))
                .andExpect(jsonPath("$[0].descripcion").value("Indica que un usuario puede desempeñarse"))
                .andExpect(jsonPath("$[1].id").value("INACTIVO"));
        verify(consultarEstadosUsuarioInteractor).ejecutar();
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(consultarEstadosUsuarioInteractor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(administrador()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoSoloTieneElClientRoleDeCambiarEstado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_ESTADO_UPDATE))))
                .andExpect(status().isForbidden());
    }
}
