package com.arquisoft.usuarios.infrastructure.coordinador.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.coordinador.query.primaryport.interactor.ConsultarCoordinadoresVigentesInteractor;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarCoordinadoresVigentesController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarCoordinadoresVigentesControllerTest.TestSecurityConfig.class})
class ConsultarCoordinadoresVigentesControllerTest {

    private static final String RUTA = "/usuarios/coordinadores/vigentes";

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
    private ConsultarCoordinadoresVigentesInteractor consultarCoordinadoresVigentesInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor vigente() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.COORDINADOR_VIGENTE_VIEW));
    }

    @Test
    void debe200ConEstadoSinVigente_cuandoBodyVacio() throws Exception {
        // Arrange
        var readModel = new CoordinadorVigenteReadModel(UUID.randomUUID(), "1001", "Ana Ramirez",
                "ana.ramirez@uco.edu.co", "3000000000", "ACTIVO");
        when(consultarCoordinadoresVigentesInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel), 0, 10, 1L));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(vigente()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Ana Ramirez"))
                .andExpect(jsonPath("$.content[0].estado").value("ACTIVO"))
                .andExpect(jsonPath("$.content[0].vigente").doesNotExist());
    }

    @Test
    void debe400_cuandoFiltraPorVigente() throws Exception {
        // Arrange — vigente no existe en la whitelist de este endpoint
        when(consultarCoordinadoresVigentesInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenThrow(new FiltroException("campo de filtro no permitido: vigente",
                        "usuarios.consulta.campo-filtro-no-permitido"));

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
                        .with(vigente()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe200_cuandoFiltraPorEstado() throws Exception {
        // Arrange
        when(consultarCoordinadoresVigentesInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));
        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);

        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "estado",
                    "operador": "ES",
                    "valor": "INACTIVO"
                  }
                }
                """;

        // Act
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(vigente()))
                .andExpect(status().isOk());

        // Assert
        verify(consultarCoordinadoresVigentesInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().raiz())
                .isEqualTo(NodoFiltro.predicado("estado", FiltroOperador.ES, "INACTIVO"));
    }

    @Test
    void debe200_cuandoConsultaConFiltroValido() throws Exception {
        // Arrange
        when(consultarCoordinadoresVigentesInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "email",
                    "operador": "CONTIENE",
                    "valor": "uco.edu.co"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(vigente()))
                .andExpect(status().isOk());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoSoloTieneElClientRoleDeAdministrador() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.COORDINADOR_ADMINISTRADOR_VIEW))))
                .andExpect(status().isForbidden());
    }
}
