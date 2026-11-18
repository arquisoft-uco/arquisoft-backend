package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
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

@WebMvcTest(ConsultarEvaluacionesCuantitativasAsesorEstudianteController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesCuantitativasAsesorEstudianteControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesCuantitativasAsesorEstudianteControllerTest {

    private static final UUID EVALUACION_ASESOR_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA_BASE = "/evaluaciones/evaluaciones-asesor/";
    private static final String RUTA = RUTA_BASE + EVALUACION_ASESOR_ID + "/cuantitativas/estudiante";

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
    private ConsultarEvaluacionesCuantitativasAsesorEstudianteInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelEstudiante(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(sub));
    }

    @Test
    void debeResponder200_cuandoConsultaExitosa() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var idCuantitativa = UtilUUID.generarNuevoUUID();
        var idItem = UtilUUID.generarNuevoUUID();
        var idCategoria = UtilUUID.generarNuevoUUID();
        var resultado = List.of(new EvaluacionCuantitativaAsesorReadModel(
                idCuantitativa,
                4,
                new ItemCuantitativoAsesorReadModel(idItem, "Claridad", "Evalua la claridad", idCategoria, 10)));
        when(interactor.ejecutar(any())).thenReturn(resultado);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelEstudiante(estudiante.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(idCuantitativa.toString()))
                .andExpect(jsonPath("$[0].puntaje").value(4))
                .andExpect(jsonPath("$[0].item.id").value(idItem.toString()))
                .andExpect(jsonPath("$[0].item.nombre").value("Claridad"))
                .andExpect(jsonPath("$[0].item.descripcion").value("Evalua la claridad"))
                .andExpect(jsonPath("$[0].item.categoriaId").value(idCategoria.toString()))
                .andExpect(jsonPath("$[0].item.valor").value(10));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesCuantitativasAsesorEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionAsesor()).isEqualTo(EVALUACION_ASESOR_ID);
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeResponder200ListaVacia_cuandoSinEvaluaciones() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelEstudiante(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeResponder400_cuandoIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA_BASE + "no-es-uuid/cuantitativas/estudiante")
                        .with(jwtDelEstudiante(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new EvaluacionAsesorNoEncontradaException(EVALUACION_ASESOR_ID));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelEstudiante(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.EvaluacionCualitativaAsesor.EVALUACION_ASESOR_NO_ENCONTRADA));
    }

    @Test
    void debeResponder422_cuandoNoPerteneceAlEstudiante() throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any()))
                .thenThrow(new EvaluacionAsesorNoPerteneceEstudianteException(EVALUACION_ASESOR_ID, estudiante));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelEstudiante(estudiante.toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.EvaluacionAsesor.NO_PERTENECE_ESTUDIANTE));
    }
}
