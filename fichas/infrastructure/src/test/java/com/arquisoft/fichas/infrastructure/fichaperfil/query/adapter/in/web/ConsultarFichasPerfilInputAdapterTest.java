package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.in.ConsultarFichasPerfilInputPort;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.shared.pagination.PaginatedResult;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarFichasPerfilInputAdapter.class)
@Import({GlobalAppExceptionHandler.class,
        ConsultarFichasPerfilInputAdapterTest.TestSecurityConfig.class})
class ConsultarFichasPerfilInputAdapterTest {

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
                    .authenticationEntryPoint((request, response, authException) ->
                        response.sendError(401, "Unauthorized"))
                    .accessDeniedHandler((request, response, accessDeniedException) ->
                        response.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarFichasPerfilInputPort consultarFichasPerfilInputPort;

    @Test
    void debe200_cuandoConsultaExitosa() throws Exception {
        PaginatedResult<FichaPerfilReadModel> resultadoVacio =
                PaginatedResult.of(List.of(), 0, 10, 0L);
        when(consultarFichasPerfilInputPort.ejecutar(any(FichaPerfilCriteria.class)))
                .thenReturn(resultadoVacio);

        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .param("page", "0")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .authorities(new SimpleGrantedAuthority("ficha:ficha:view"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeNormalizar_cuandoPageEsNegativo() throws Exception {
        PaginatedResult<FichaPerfilReadModel> resultadoVacio =
                PaginatedResult.of(List.of(), 0, 10, 0L);
        when(consultarFichasPerfilInputPort.ejecutar(any(FichaPerfilCriteria.class)))
                .thenReturn(resultadoVacio);

        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .param("page", "-1")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .authorities(new SimpleGrantedAuthority("ficha:ficha:view"))))
                .andExpect(status().isOk());
    }

    @Test
    void debe400_cuandoNoSeEnvianParametrosDePaginacion() throws Exception {
        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .authorities(new SimpleGrantedAuthority("ficha:ficha:view"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        mockMvc.perform(get("/fichas-perfil/coordinador"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoAuthorityInsuficiente() throws Exception {
        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .param("page", "0")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user("estudiante")
                                .authorities(new SimpleGrantedAuthority("estudiante"))))
                .andExpect(status().isForbidden());
    }
}
