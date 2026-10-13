package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.interactor.AsignarJuradosEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.primaryport.model.AsignarJuradosEvaluacionCommand;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.exception.JuradosYaAsignadosException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
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

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsignarJuradosEvaluacionController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        AsignarJuradosEvaluacionControllerTest.TestSecurityConfig.class
})
class AsignarJuradosEvaluacionControllerTest {

    private static final UUID EVALUACION = UtilUUID.generarNuevoUUID();
    private static final UUID JURADO = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/" + EVALUACION + "/jurados";
    private static final String BODY_VALIDO = """
            { "jurados": ["%s"] }
            """.formatted(JURADO);

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
    private AsignarJuradosEvaluacionInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar201ConLosIdsGenerados_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var idGenerado = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(AsignarJuradosEvaluacionCommand.class))).thenReturn(List.of(idGenerado));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.evaluacionesJurado.length()").value(1))
                .andExpect(jsonPath("$.evaluacionesJurado[0]").value(idGenerado.toString()));
    }

    @Test
    void debeRetornar400LoteVacio_cuandoLosJuradosNoLlegan() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("jurados"));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar400ConCampoIndexado_cuandoUnJuradoNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "jurados": ["%s", "no-es-un-uuid"] }
                                """.formatted(JURADO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value("jurados[1]"));
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
    void debeRetornar403_cuandoNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        EvaluacionesAuthorities.EVALUACION_CUALITATIVA_JURADO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar422_cuandoElInteractorLanzaUnaReglaDeDominio() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(AsignarJuradosEvaluacionCommand.class)))
                .thenThrow(new JuradosYaAsignadosException(Set.of(JURADO)));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.EvaluacionJurado.JURADOS_YA_ASIGNADOS));
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(EvaluacionesAuthorities.EVALUACION_JURADO_CREATE));
    }
}
