package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCualitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.assertj.core.api.Assertions;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionesCualitativasJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesCualitativasJuradoControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesCualitativasJuradoControllerTest {

    private static final UUID EVALUACION_JURADO_ID = UUID.randomUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-jurado/" + EVALUACION_JURADO_ID + "/cualitativas";

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
    private ConsultarEvaluacionesCualitativasJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermisoView(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(subject))
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.EVALUACION_CUALITATIVA_JURADO_ESTUDIANTE_VIEW));
    }

    @Test
    void debeRetornar200ConElContratoExacto_yPasarElSubjectDelJwtAlInteractor() throws Exception {
        // Arrange
        String subject = UUID.randomUUID().toString();
        UUID idEvaluacionCualitativa = UUID.randomUUID();
        UUID idItem = UUID.randomUUID();
        UUID idCriterio = UUID.randomUUID();
        List<EvaluacionCualitativaJuradoReadModel> resultado = List.of(new EvaluacionCualitativaJuradoReadModel(
                idEvaluacionCualitativa,
                new ItemCualitativoJuradoReadModel(idItem, "Claridad", "Evalua la claridad"),
                new CriterioItemCualitativoJuradoReadModel(idCriterio, "Excelente", "Cumple todo")));
        when(interactor.ejecutar(any())).thenReturn(resultado);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView(subject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(idEvaluacionCualitativa.toString()))
                .andExpect(jsonPath("$[0].item.id").value(idItem.toString()))
                .andExpect(jsonPath("$[0].item.nombre").value("Claridad"))
                .andExpect(jsonPath("$[0].criterio.id").value(idCriterio.toString()))
                .andExpect(jsonPath("$[0].criterio.nombre").value("Excelente"));

        ArgumentCaptor<ConsultarEvaluacionesCualitativasJuradoEstudianteQuery> captor =
                ArgumentCaptor.forClass(ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        Assertions.assertThat(captor.getValue().evaluacionJurado()).isEqualTo(EVALUACION_JURADO_ID);
        Assertions.assertThat(captor.getValue().estudiante()).isEqualTo(UUID.fromString(subject));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoLaEvaluacionNoTieneEvaluacionesCualitativas() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView(UUID.randomUUID().toString())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornar400_cuandoElSubjectDelJwtNoEsUnUuidValido() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView("no-es-un-uuid")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoHayJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoNoTieneElClientRoleExclusivo() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionJuradoNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new EvaluacionJuradoNoEncontradaException(EVALUACION_JURADO_ID));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView(UUID.randomUUID().toString())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionJuradoNoPerteneceAlEstudiante() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new EvaluacionJuradoNoPerteneceEstudianteException(EVALUACION_JURADO_ID));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView(UUID.randomUUID().toString())))
                .andExpect(status().isUnprocessableEntity());
    }
}
