package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.interactor.CambiarPuntajeEvaluacionCuantitativaJuradoInteractor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionCuantitativaJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.config.JacksonConfig;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarPuntajeEvaluacionCuantitativaJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        JacksonConfig.class,
        CambiarPuntajeEvaluacionCuantitativaJuradoControllerTest.TestSecurityConfig.class
})
class CambiarPuntajeEvaluacionCuantitativaJuradoControllerTest {

    private static final UUID ID = UUID.randomUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-cuantitativas-jurado/" + ID + "/puntaje";
    private static final String BODY_VALIDO = """
            { "puntaje": 320 }
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
    private CambiarPuntajeEvaluacionCuantitativaJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204_cuandoPeticionEsValida() throws Exception {
        // Arrange
        doNothing().when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent());
    }

    @Test
    void debeRetornar400_cuandoPuntajeEsNulo() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"puntaje\": null }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoPuntajeNoEsNumerico() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"puntaje\": \"no-es-un-numero\" }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoPuntajeEsDecimal() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"puntaje\": 320.5 }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoNoTienePermiso() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        "evaluaciones:evaluacion-cuantitativa-jurado-estudiante:view")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionNoExiste() throws Exception {
        // Arrange
        doThrow(new EvaluacionCuantitativaJuradoNoEncontradaException(ID)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeRetornar422_cuandoNoPerteneceAlJuradoAutenticado() throws Exception {
        // Arrange
        doThrow(new EvaluacionCuantitativaJuradoNoPerteneceJuradoException(ID)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionJuradoEstaFinalizada() throws Exception {
        // Arrange
        doThrow(new EvaluacionJuradoFinalizadaException(ID)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeRetornar422_cuandoElPuntajeExcedeElValorDelItem() throws Exception {
        // Arrange
        doThrow(new PuntajeEvaluacionCuantitativaJuradoExcedeValorItemException(320, 200))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.EVALUACION_CUANTITATIVA_JURADO_UPDATE));
    }
}
