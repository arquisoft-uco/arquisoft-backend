package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasJuradoRealizadasInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionesCuantitativasJuradoRealizadasController.class)
@Import({IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesCuantitativasJuradoRealizadasControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesCuantitativasJuradoRealizadasControllerTest {

    private static final UUID EVALUACION_JURADO_ID = UtilUUID.generarNuevoUUID();
    private static final UUID JURADO_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA =
            "/evaluaciones/evaluaciones-jurado/" + EVALUACION_JURADO_ID + "/cuantitativas/jurado";

    @TestConfiguration
    @EnableWebSecurity
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
    private ConsultarEvaluacionesCuantitativasJuradoRealizadasInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtJurado() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(token -> token.subject(JURADO_ID.toString()));
    }

    @Test
    void debeRetornar200ConLista_cuandoJuradoPropietario() throws Exception {
        // Arrange
        var idEvaluacion = UtilUUID.generarNuevoUUID();
        var idItem = UtilUUID.generarNuevoUUID();
        var idCategoria = UtilUUID.generarNuevoUUID();
        var resultado = List.of(new EvaluacionCuantitativaJuradoReadModel(
                idEvaluacion, 350,
                new ItemCuantitativoJuradoReadModel(idItem, "Rigor", "Evalua el rigor", idCategoria, 500)));
        when(interactor.ejecutar(any())).thenReturn(resultado);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJurado()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(idEvaluacion.toString()))
                .andExpect(jsonPath("$[0].puntaje").value(350))
                .andExpect(jsonPath("$[0].item.id").value(idItem.toString()))
                .andExpect(jsonPath("$[0].item.nombre").value("Rigor"))
                .andExpect(jsonPath("$[0].item.categoriaId").value(idCategoria.toString()))
                .andExpect(jsonPath("$[0].item.valor").value(500));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesCuantitativasJuradoRealizadasQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionJurado()).isEqualTo(EVALUACION_JURADO_ID);
        assertThat(captor.getValue().jurado()).isEqualTo(JURADO_ID);
    }

    @Test
    void debeRetornar200Vacio_cuandoSinEvaluaciones() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJurado()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornar400_cuandoIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/evaluaciones/evaluaciones-jurado/no-es-uuid/cuantitativas/jurado")
                        .with(jwtJurado()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoEvaluacionNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new EvaluacionJuradoNoEncontradaException(EVALUACION_JURADO_ID));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJurado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(
                        EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_NO_ENCONTRADA));
    }

    @Test
    void debeRetornar422_cuandoPerteneceAOtroJurado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new EvaluacionJuradoNoPerteneceJuradoException(EVALUACION_JURADO_ID));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJurado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(
                        EvaluacionesCodes.EvaluacionCualitativaJurado.NO_PERTENECE_JURADO));
    }
}
