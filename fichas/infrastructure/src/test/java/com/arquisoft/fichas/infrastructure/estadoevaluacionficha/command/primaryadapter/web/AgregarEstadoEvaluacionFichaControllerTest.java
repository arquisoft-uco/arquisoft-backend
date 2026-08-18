package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.config.CatalogoMensajesConfig;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.interactor.AgregarEstadoEvaluacionFichaInteractor;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaNoPropiaException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgregarEstadoEvaluacionFichaController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class, CatalogoMensajesConfig.class,
        AgregarEstadoEvaluacionFichaControllerTest.TestSecurityConfig.class})
class AgregarEstadoEvaluacionFichaControllerTest {

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
    private AgregarEstadoEvaluacionFichaInteractor agregarEstadoEvaluacionFichaInteractor;

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        UUID resultadoId = UUID.randomUUID();
        String requestBody = String.format(
                "{\"evaluacionFichaPerfil\":\"%s\",\"estadoEvaluacion\":\"APROBADA\"}",
                evaluacionId);

        when(agregarEstadoEvaluacionFichaInteractor.ejecutar(any())).thenReturn(resultadoId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estado-evaluacion-ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_FICHA_CREATE))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(resultadoId.toString()));
    }

    @Test
    void debe400_cuandoRequestInvalido() throws Exception {
        // Arrange
        String requestInvalido = "{\"evaluacionFichaPerfil\":null,\"estadoEvaluacion\":\"\"}";

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estado-evaluacion-ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestInvalido)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_FICHA_CREATE))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe422_cuandoEvaluacionNoExiste() throws Exception {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        String requestBody = String.format(
                "{\"evaluacionFichaPerfil\":\"%s\",\"estadoEvaluacion\":\"APROBADA\"}",
                evaluacionId);

        when(agregarEstadoEvaluacionFichaInteractor.ejecutar(any()))
                .thenThrow(new EvaluacionFichaPerfilNoEncontradaException(evaluacionId));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estado-evaluacion-ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_FICHA_CREATE))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoEstadoDuplicado() throws Exception {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        String requestBody = String.format(
                "{\"evaluacionFichaPerfil\":\"%s\",\"estadoEvaluacion\":\"APROBADA\"}",
                evaluacionId);

        when(agregarEstadoEvaluacionFichaInteractor.ejecutar(any()))
                .thenThrow(new EstadoEvaluacionDuplicadoException(evaluacionId, "APROBADA"));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estado-evaluacion-ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_FICHA_CREATE))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoValidacionDominio() throws Exception {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        String requestBody = String.format(
                "{\"evaluacionFichaPerfil\":\"%s\",\"estadoEvaluacion\":\"NO_APROBADA\"}",
                evaluacionId);

        var validationResult = new ValidationResult();
        validationResult.agregarError("estadoEvaluacion", "TRANSICION_INVALIDA",
                "No se puede agregar un nuevo estado cuando la evaluación ya alcanzó un estado terminal");

        when(agregarEstadoEvaluacionFichaInteractor.ejecutar(any()))
                .thenThrow(new DomainValidationException(validationResult));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estado-evaluacion-ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_FICHA_CREATE))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        String requestBody = String.format(
                "{\"evaluacionFichaPerfil\":\"%s\",\"estadoEvaluacion\":\"APROBADA\"}",
                evaluacionId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estado-evaluacion-ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe422_cuandoRepresentanteNoEsPropietario() throws Exception {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        String requestBody = String.format(
                "{\"evaluacionFichaPerfil\":\"%s\",\"estadoEvaluacion\":\"APROBADA\"}",
                evaluacionId);

        when(agregarEstadoEvaluacionFichaInteractor.ejecutar(any()))
                .thenThrow(new EvaluacionFichaNoPropiaException(evaluacionId));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estado-evaluacion-ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_EVALUACION_FICHA_CREATE))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        String requestBody = String.format(
                "{\"evaluacionFichaPerfil\":\"%s\",\"estadoEvaluacion\":\"APROBADA\"}",
                evaluacionId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/estado-evaluacion-ficha")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority("fichas:otro-permiso"))))
                .andExpect(status().isForbidden());
    }
}
