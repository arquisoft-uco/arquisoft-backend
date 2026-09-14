package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
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

@WebMvcTest(ConsultarEvaluacionesCuantitativasJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesCuantitativasJuradoControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesCuantitativasJuradoControllerTest {

    private static final UUID EVALUACION_JURADO_ID = UUID.randomUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-jurado/" + EVALUACION_JURADO_ID + "/cuantitativas";

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
    private ConsultarEvaluacionesCuantitativasJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermisoView(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(subject))
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.EVALUACION_CUANTITATIVA_JURADO_ESTUDIANTE_VIEW));
    }

    @Test
    void debeRetornar200ConElContratoExacto_yPasarElSubjectDelJwtAlInteractor() throws Exception {
        // Arrange
        String subject = UUID.randomUUID().toString();
        UUID idEvaluacionCuantitativa = UUID.randomUUID();
        UUID idItem = UUID.randomUUID();
        UUID idCategoria = UUID.randomUUID();
        List<EvaluacionCuantitativaJuradoReadModel> resultado = List.of(new EvaluacionCuantitativaJuradoReadModel(
                idEvaluacionCuantitativa,
                350,
                new ItemCuantitativoJuradoReadModel(idItem, "Rigor", "Evalua el rigor", idCategoria, 500)));
        when(interactor.ejecutar(any())).thenReturn(resultado);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView(subject)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(idEvaluacionCuantitativa.toString()))
                .andExpect(jsonPath("$[0].puntaje").value(350))
                .andExpect(jsonPath("$[0].item.id").value(idItem.toString()))
                .andExpect(jsonPath("$[0].item.nombre").value("Rigor"))
                .andExpect(jsonPath("$[0].item.categoriaId").value(idCategoria.toString()))
                .andExpect(jsonPath("$[0].item.valor").value(500));

        ArgumentCaptor<ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery> captor =
                ArgumentCaptor.forClass(ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        Assertions.assertThat(captor.getValue().evaluacionJurado()).isEqualTo(EVALUACION_JURADO_ID);
        Assertions.assertThat(captor.getValue().estudiante()).isEqualTo(UUID.fromString(subject));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoLaEvaluacionNoTieneEvaluacionesCuantitativas() throws Exception {
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
