package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.primaryport.interactor.RegistrarEvaluacionFichaPerfilInteractor;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarEvaluacionFichaPerfilController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RegistrarEvaluacionFichaPerfilControllerTest.TestSecurityConfig.class})
class RegistrarEvaluacionFichaPerfilControllerTest {

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
    private RegistrarEvaluacionFichaPerfilInteractor registrarEvaluacionFichaPerfilInteractor;

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID evaluacionId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();

        when(registrarEvaluacionFichaPerfilInteractor.ejecutar(any())).thenReturn(evaluacionId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(representanteId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.EVALUACION_FICHA_PERFIL_CREATE))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(evaluacionId.toString()));
    }

    @Test
    void debe400_cuandoFichaIdMalformado() throws Exception {
        // Arrange
        String fichaIdMalformado = "no-es-uuid";

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaIdMalformado)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.EVALUACION_FICHA_PERFIL_CREATE))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID representanteId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaId}/evaluaciones", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(representanteId.toString()))
                                .authorities(new SimpleGrantedAuthority("otro:authority"))))
                .andExpect(status().isForbidden());
    }
}
