package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.ConsultarEvaluacionesAsignadasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionesAsignadasJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesAsignadasJuradoControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesAsignadasJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/evaluaciones-jurado/jurado";

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
    private ConsultarEvaluacionesAsignadasJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelJurado(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(token -> token.subject(subject))
                .authorities(new SimpleGrantedAuthority(EvaluacionesAuthorities.EVALUACION_JURADO_JURADO_VIEW));
    }

    @Test
    void debeResponder200ConLista_yPasarAlInteractorElJuradoDelJwt() throws Exception {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of(new EvaluacionAsignadaJuradoReadModel(
                evaluacionJurado,
                new EvaluacionAsignadaJuradoReadModel.Evaluacion(evaluacion,
                        new EvaluacionAsignadaJuradoReadModel.Estado("PENDIENTE", "Pendiente")),
                new EvaluacionAsignadaJuradoReadModel.Entregable(entregable, "Robot seguidor", 2))));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelJurado(jurado.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(evaluacionJurado.toString()))
                .andExpect(jsonPath("$[0].evaluacion.id").value(evaluacion.toString()))
                .andExpect(jsonPath("$[0].evaluacion.estado.id").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].evaluacion.estado.nombre").value("Pendiente"))
                .andExpect(jsonPath("$[0].entregable.id").value(entregable.toString()))
                .andExpect(jsonPath("$[0].entregable.proyecto").value("Robot seguidor"))
                .andExpect(jsonPath("$[0].entregable.version").value(2));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesAsignadasJuradoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().jurado()).isEqualTo(jurado);
    }

    @Test
    void debeResponder200Vacia_cuandoNoHayAsignaciones() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelJurado(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_sinClientRole() throws Exception {
        // Arrange
        var soloEstudiante = SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(token -> token.subject(UtilUUID.generarNuevoUUID().toString()))
                .authorities(new SimpleGrantedAuthority(EvaluacionesAuthorities.EVALUACION_JURADO_ESTUDIANTE_VIEW));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(RUTA).with(soloEstudiante))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoElJuradoNoExiste() throws Exception {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenThrow(new JuradoNoEncontradoException(jurado));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelJurado(jurado.toString())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeResponder400_cuandoElSubjectNoEsUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelJurado("no-es-uuid")))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }
}
