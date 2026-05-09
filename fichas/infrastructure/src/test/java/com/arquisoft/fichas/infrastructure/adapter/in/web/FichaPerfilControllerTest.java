package com.arquisoft.fichas.infrastructure.adapter.in.web;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.in.ConsultarFichasPerfilUseCase;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FichaPerfilController.class)
@Import({GlobalAppExceptionHandler.class,
        FichaPerfilControllerTest.TestSecurityConfig.class})
class FichaPerfilControllerTest {

    /**
     * Configuración de seguridad mínima para tests de slice (@WebMvcTest).
     * @EnableWebSecurity provee el bean HttpSecurity.
     * @EnableMethodSecurity activa @PreAuthorize (hasAuthority) en el controller.
     */
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
    private ConsultarFichasPerfilUseCase consultarFichasPerfilUseCase;

    @Test
    void debe200_cuandoConsultaExitosa() throws Exception {
        // Arrange
        Page<FichaPerfil> paginaVacia = Page.empty();
        when(consultarFichasPerfilUseCase.ejecutar(any(Pageable.class))).thenReturn(paginaVacia);

        // Act & Assert — usuario con authority ficha:ficha:view (resource_access)
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
        // Arrange — page=-1 es normalizado a 0 por PageableHandlerMethodArgumentResolver
        // (Math.max(0, page)). El antiguo @Min(0) + @Validated fue eliminado al migrar
        // a Pageable; la validación de rango es responsabilidad del resolver de Spring.
        Page<FichaPerfil> paginaVacia = Page.empty();
        when(consultarFichasPerfilUseCase.ejecutar(any(Pageable.class))).thenReturn(paginaVacia);

        // Act & Assert — Spring acepta page=-1 tratándolo como page=0 → 200
        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .param("page", "-1")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .authorities(new SimpleGrantedAuthority("ficha:ficha:view"))))
                .andExpect(status().isOk());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange — sin autenticación

        // Act & Assert — Spring Security rechaza antes de llegar al controller
        mockMvc.perform(get("/fichas-perfil/coordinador"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoAuthorityInsuficiente() throws Exception {
        // Arrange — @PreAuthorize rechaza antes de invocar el use case; no hace falta mock

        // Act & Assert — @PreAuthorize("hasAuthority('ficha:ficha:view')") rechaza con 403
        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .param("page", "0")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user("estudiante")
                                .authorities(new SimpleGrantedAuthority("estudiante"))))
                .andExpect(status().isForbidden());
    }
}
