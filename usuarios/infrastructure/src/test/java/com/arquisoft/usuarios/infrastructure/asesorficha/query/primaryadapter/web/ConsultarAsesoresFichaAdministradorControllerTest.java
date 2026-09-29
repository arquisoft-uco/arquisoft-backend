package com.arquisoft.usuarios.infrastructure.asesorficha.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.asesorficha.query.primaryport.interactor.ConsultarAsesoresFichaAdministradorInteractor;
import com.arquisoft.usuarios.application.asesorficha.query.readmodel.AsesorFichaReadModel;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarAsesoresFichaAdministradorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarAsesoresFichaAdministradorControllerTest.TestSecurityConfig.class})
class ConsultarAsesoresFichaAdministradorControllerTest {

    private static final String RUTA = "/usuarios/asesores-ficha/administrador";

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
    private ConsultarAsesoresFichaAdministradorInteractor consultarAsesoresFichaAdministradorInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ASESOR_FICHA_ADMINISTRADOR_VIEW));
    }

    @Test
    void debe200ConEstadoYVigente_cuandoConsultaSinBody() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var readModel = new AsesorFichaReadModel(id, "1001", "Ana Ramirez",
                "ana.ramirez@uco.edu.co", "3000000000", "INACTIVO", false);
        when(consultarAsesoresFichaAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel), 0, 10, 1L));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(administrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].identificador").value("1001"))
                .andExpect(jsonPath("$.content[0].email").value("ana.ramirez@uco.edu.co"))
                .andExpect(jsonPath("$.content[0].contacto").value("3000000000"))
                .andExpect(jsonPath("$.content[0].estado").value("INACTIVO"))
                .andExpect(jsonPath("$.content[0].vigente").value(false));
    }

    @Test
    void debe200_cuandoFiltraPorVigente() throws Exception {
        // Arrange
        when(consultarAsesoresFichaAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
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
    void debe400_cuandoOrdenaPorUnCampoNoOrdenable() throws Exception {
        // Arrange
        when(consultarAsesoresFichaAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenThrow(new FiltroException("campo de orden no permitido: vigente",
                        "usuarios.consulta.campo-orden-no-permitido"));

        var body = """
                {
                  "ordenamiento": ["vigente:asc"]
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
    void debe401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(consultarAsesoresFichaAdministradorInteractor);
    }

    @Test
    void debe403_cuandoSoloTieneElClientRoleDeVigentes() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ASESOR_FICHA_VIGENTE_VIEW))))
                .andExpect(status().isForbidden());
        verifyNoInteractions(consultarAsesoresFichaAdministradorInteractor);
    }
}
