package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web;

import com.arquisoft.shared.web.config.CatalogoMensajesConfig;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.usecase.ConsultarFichasPerfilUseCase;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
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

@WebMvcTest(ConsultarFichasPerfilController.class)
@Import({GlobalAppExceptionHandler.class, CatalogoMensajesConfig.class,
        ConsultarFichasPerfilControllerTest.TestSecurityConfig.class})
class ConsultarFichasPerfilControllerTest {

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
    private ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;

    @Test
    void debe200_cuandoBodyVacio() throws Exception {
        when(consultarFichasPerfilUseCase.ejecutar(any(FichaPerfilCriteria.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        mockMvc.perform(post("/fichas-perfil/coordinador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debe200_cuandoConsultaConFiltroPredicado() throws Exception {
        when(consultarFichasPerfilUseCase.ejecutar(any(FichaPerfilCriteria.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        String body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "tituloProyecto",
                    "operador": "CONTIENE",
                    "valor": "web"
                  }
                }
                """;

        mockMvc.perform(post("/fichas-perfil/coordinador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_VIEW))))
                .andExpect(status().isOk());
    }

    @Test
    void debe200_cuandoConsultaConGrupoOR() throws Exception {
        when(consultarFichasPerfilUseCase.ejecutar(any(FichaPerfilCriteria.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 5, 0L));

        String body = """
                {
                  "pagina": 0,
                  "tamanio": 5,
                  "ordenamiento": ["tituloProyecto:ASC"],
                  "filtros": {
                    "tipo": "GRUPO",
                    "conector": "OR",
                    "nodos": [
                      { "tipo": "PREDICADO", "campo": "tituloProyecto", "operador": "CONTIENE", "valor": "web" },
                      { "tipo": "PREDICADO", "campo": "asesorNombre",   "operador": "CONTIENE", "valor": "juan" }
                    ]
                  }
                }
                """;

        mockMvc.perform(post("/fichas-perfil/coordinador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_VIEW))))
                .andExpect(status().isOk());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        mockMvc.perform(post("/fichas-perfil/coordinador")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoAuthorityInsuficiente() throws Exception {
        mockMvc.perform(post("/fichas-perfil/coordinador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.user("estudiante")
                                .authorities(new SimpleGrantedAuthority("estudiante"))))
                .andExpect(status().isForbidden());
    }
}
