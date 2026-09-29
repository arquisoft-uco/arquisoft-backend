package com.arquisoft.usuarios.infrastructure.administrador.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.administrador.query.primaryport.interactor.ConsultarAdministradoresAdministradorInteractor;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarAdministradoresAdministradorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarAdministradoresAdministradorControllerTest.TestSecurityConfig.class})
class ConsultarAdministradoresAdministradorControllerTest {

    private static final String RUTA = "/usuarios/administradores/administrador";

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
    private ConsultarAdministradoresAdministradorInteractor consultarAdministradoresAdministradorInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ADMINISTRADOR_ADMINISTRADOR_VIEW));
    }

    @Test
    void debe200ConEstadoYVigente_cuandoBodyVacio() throws Exception {
        // Arrange
        var readModel = new AdministradorReadModel(UtilUUID.generarNuevoUUID(), "1001", "Ana Ramirez",
                "ana.ramirez@uco.edu.co", "3000000000", "INACTIVO", false);
        when(consultarAdministradoresAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel), 0, 10, 1L));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].estado").value("INACTIVO"))
                .andExpect(jsonPath("$.content[0].vigente").value(false));
    }

    @Test
    void debe200_cuandoConsultaConFiltroValido() throws Exception {
        // Arrange
        when(consultarAdministradoresAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "vigente",
                    "operador": "ES",
                    "valor": "false"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(administrador()))
                .andExpect(status().isOk());
    }

    @Test
    void debe400_cuandoElFiltroNoEsValido() throws Exception {
        // Arrange
        when(consultarAdministradoresAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenThrow(new FiltroException("campo de filtro no permitido: campoInventado",
                        "usuarios.consulta.campo-filtro-no-permitido"));

        var body = """
                {
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "campoInventado",
                    "operador": "ES",
                    "valor": "x"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(administrador()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoNoTieneElClientRoleDelEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ASESOR_ADMINISTRADOR_VIEW))))
                .andExpect(status().isForbidden());
    }
}
