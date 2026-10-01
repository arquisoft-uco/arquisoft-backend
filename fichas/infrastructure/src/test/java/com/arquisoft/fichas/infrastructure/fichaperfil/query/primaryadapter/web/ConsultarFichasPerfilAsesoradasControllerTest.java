package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilAsesoradasInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilAsesoradasQuery;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarFichasPerfilAsesoradasController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarFichasPerfilAsesoradasControllerTest.TestSecurityConfig.class})
class ConsultarFichasPerfilAsesoradasControllerTest {

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
    private ConsultarFichasPerfilAsesoradasInteractor consultarFichasPerfilAsesoradasInteractor;

    private static final UUID ASESOR_ID = UUID.randomUUID();

    @Test
    void debeRetornar200_cuandoConsultaSinBody() throws Exception {
        // Arrange
        when(consultarFichasPerfilAsesoradasInteractor.ejecutar(any(ConsultarFichasPerfilAsesoradasQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_ASESOR_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void debeRetornar200ConPaginaVacia_cuandoAsesorSinFichas() throws Exception {
        // Arrange
        when(consultarFichasPerfilAsesoradasInteractor.ejecutar(any(ConsultarFichasPerfilAsesoradasQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_ASESOR_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeRetornar200_cuandoConsultaConFiltroYOrdenamientoValidos() throws Exception {
        // Arrange
        when(consultarFichasPerfilAsesoradasInteractor.ejecutar(any(ConsultarFichasPerfilAsesoradasQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 5, 0L));

        String body = """
                {
                  "pagina": 0,
                  "tamanio": 5,
                  "ordenamiento": ["tituloProyecto:ASC"],
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "tituloProyecto",
                    "operador": "CONTIENE",
                    "valor": "web"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_ASESOR_VIEW))))
                .andExpect(status().isOk());
    }

    @Test
    void debeRetornar400_cuandoFiltroTieneOperadorInvalido() throws Exception {
        // Arrange
        when(consultarFichasPerfilAsesoradasInteractor.ejecutar(any(ConsultarFichasPerfilAsesoradasQuery.class)))
                .thenThrow(new FiltroException("operador no permitido: INVALIDO",
                        "fichas.consulta.operador-no-permitido"));

        String body = """
                {
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "tituloProyecto",
                    "operador": "INVALIDO",
                    "valor": "web"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_ASESOR_VIEW))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/asesor")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSinClientRoleFichaPerfilAsesorView() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("otro:permiso"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeExtraerAsesorFichaDelSubjectDelJwt_yPasarloAlInteractor() throws Exception {
        // Arrange
        when(consultarFichasPerfilAsesoradasInteractor.ejecutar(any(ConsultarFichasPerfilAsesoradasQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        mockMvc.perform(post("/fichas-perfil/asesor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_ASESOR_VIEW))))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarFichasPerfilAsesoradasQuery.class);
        verify(consultarFichasPerfilAsesoradasInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().asesorFicha()).isEqualTo(ASESOR_ID);
    }
}
