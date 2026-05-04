package com.arquisoft.fichas.infrastructure.adapter.in.web;

import com.arquisoft.fichas.domain.model.FichaPerfil;
import com.arquisoft.fichas.domain.port.in.ConsultarFichasPerfilUseCase;
import com.arquisoft.shared.domain.Page;
import com.arquisoft.shared.web.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FichaPerfilController.class)
@Import({FichasGlobalExceptionHandler.class, GlobalAppExceptionHandler.class,
        FichaPerfilControllerTest.TestSecurityConfig.class})
class FichaPerfilControllerTest {

    /**
     * Configuración de seguridad mínima para tests de slice (@WebMvcTest).
     * @EnableWebSecurity provee el bean HttpSecurity.
     * @EnableMethodSecurity activa @PreAuthorize en el controller.
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
                        response.sendError(401, "Unauthorized")));
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
        Page<FichaPerfil> paginaVacia = Page.empty(0, 10);
        when(consultarFichasPerfilUseCase.ejecutar(anyInt(), anyInt())).thenReturn(paginaVacia);

        // Act & Assert — usuario con rol COORDINADOR
        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .param("page", "0")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .roles("COORDINADOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debe400_cuandoParametrosPaginacionInvalidos() throws Exception {
        // Arrange — page=-1 viola @Min(0) en el controller (@Validated)

        // Act & Assert — usuario COORDINADOR, pero parámetros inválidos → 400
        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .param("page", "-1")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user("coordinador")
                                .roles("COORDINADOR")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange — sin autenticación

        // Act & Assert — Spring Security rechaza antes de llegar al controller
        mockMvc.perform(get("/fichas-perfil/coordinador"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Arrange — usuario autenticado pero sin rol COORDINADOR
        when(consultarFichasPerfilUseCase.ejecutar(anyInt(), anyInt()))
                .thenReturn(Page.empty(0, 10));

        // Act & Assert — @PreAuthorize("hasRole('COORDINADOR')") rechaza con 403
        mockMvc.perform(get("/fichas-perfil/coordinador")
                        .param("page", "0")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user("estudiante")
                                .roles("ESTUDIANTE")))
                .andExpect(status().isForbidden());
    }
}
