package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.ConsultarEstadosFichaPerfilAsesorInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilAsesorQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosFichaPerfilAsesorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosFichaPerfilAsesorControllerTest.TestSecurityConfig.class})
class ConsultarEstadosFichaPerfilAsesorControllerTest {

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
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarEstadosFichaPerfilAsesorInteractor consultarEstadosFichaPerfilAsesorInteractor;

    private static final UUID ASESOR_ID = UUID.randomUUID();

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(ASESOR_ID.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeRetornar200ConContenido_cuandoInteractorRetornaResultados() throws Exception {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var fecha = Instant.parse("2026-09-07T14:03:00Z");
        var estado = new EstadoFichaPerfilAsesorReadModel(
                fichaPerfil, "Sistema de gestion", "APROBADA", "Aprobada", fecha);
        when(consultarEstadosFichaPerfilAsesorInteractor.ejecutar(any(ConsultarEstadosFichaPerfilAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(estado), 0, 10, 1L));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estados-ficha/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_ASESOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].fichaPerfil").value(fichaPerfil.toString()))
                .andExpect(jsonPath("$.content[0].tituloProyecto").value("Sistema de gestion"))
                .andExpect(jsonPath("$.content[0].estadoId").value("APROBADA"))
                .andExpect(jsonPath("$.content[0].estadoNombre").value("Aprobada"));
    }

    @Test
    void debeRetornar200ConPaginaVacia_cuandoAsesorSinFichasOFiltroSinCoincidencias() throws Exception {
        // Arrange
        when(consultarEstadosFichaPerfilAsesorInteractor.ejecutar(any(ConsultarEstadosFichaPerfilAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estados-ficha/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_ASESOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeRetornar400_cuandoFiltroInvalido() throws Exception {
        // Arrange
        when(consultarEstadosFichaPerfilAsesorInteractor.ejecutar(any(ConsultarEstadosFichaPerfilAsesorQuery.class)))
                .thenThrow(new FiltroException("operador no permitido: INVALIDO",
                        "fichas.consulta.operador-no-permitido"));

        String body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "estadoFicha",
                    "operador": "INVALIDO",
                    "valor": "APROBADA"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estados-ficha/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_ASESOR_VIEW)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estados-ficha/asesor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSinClientRoleEstadoFichaPerfilAsesorView() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estados-ficha/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtConRol("otro:permiso")))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeExtraerAsesorFichaDelSubjectDelJwt_yPasarloAlInteractor_noDelBody() throws Exception {
        // Arrange
        when(consultarEstadosFichaPerfilAsesorInteractor.ejecutar(any(ConsultarEstadosFichaPerfilAsesorQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));
        var otroAsesorEnElBody = UUID.randomUUID();
        String body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "asesorFicha",
                    "operador": "ES",
                    "valor": "%s"
                  }
                }
                """.formatted(otroAsesorEnElBody);

        // Act
        mockMvc.perform(post("/fichas-perfil/estados-ficha/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_ASESOR_VIEW)))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarEstadosFichaPerfilAsesorQuery.class);
        verify(consultarEstadosFichaPerfilAsesorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().asesorFicha()).isEqualTo(ASESOR_ID);
    }
}
