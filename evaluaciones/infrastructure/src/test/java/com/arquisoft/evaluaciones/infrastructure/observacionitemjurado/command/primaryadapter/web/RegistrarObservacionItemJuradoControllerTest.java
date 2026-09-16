package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.primaryport.interactor.RegistrarObservacionItemJuradoInteractor;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.DescripcionObservacionItemJuradoDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarObservacionItemJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RegistrarObservacionItemJuradoControllerTest.TestSecurityConfig.class
})
class RegistrarObservacionItemJuradoControllerTest {

    private static final UUID EVALUACION_CUANTITATIVA_JURADO = UUID.randomUUID();
    private static final String RUTA =
            "/evaluaciones/evaluaciones-cuantitativas-jurado/" + EVALUACION_CUANTITATIVA_JURADO + "/observaciones";
    private static final String BODY_VALIDO = """
            { "descripcion": "Sustenta el puntaje otorgado" }
            """;

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint((request, response, error) ->
                                    response.sendError(401, "Unauthorized"))
                            .accessDeniedHandler((request, response, error) ->
                                    response.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarObservacionItemJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar201_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var id = UUID.randomUUID();
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void debeRetornar400_cuandoDescripcionEsInvalida() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"descripcion\": \" \" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoNoTienePermiso() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(
                                        "evaluaciones:evaluacion-cuantitativa-jurado:update")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionNoExiste() throws Exception {
        // Arrange
        doThrow(new EvaluacionCuantitativaJuradoNoEncontradaException(EVALUACION_CUANTITATIVA_JURADO))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeRetornar422_cuandoNoPerteneceAlJuradoAutenticado() throws Exception {
        // Arrange
        doThrow(new EvaluacionCuantitativaJuradoNoPerteneceJuradoException(EVALUACION_CUANTITATIVA_JURADO))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionJuradoEstaFinalizada() throws Exception {
        // Arrange
        doThrow(new EvaluacionJuradoFinalizadaException(EVALUACION_CUANTITATIVA_JURADO))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeRetornar422_cuandoLaDescripcionYaExiste() throws Exception {
        // Arrange
        doThrow(new DescripcionObservacionItemJuradoDuplicadaException(EVALUACION_CUANTITATIVA_JURADO))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.OBSERVACION_ITEM_JURADO_CREATE));
    }
}
