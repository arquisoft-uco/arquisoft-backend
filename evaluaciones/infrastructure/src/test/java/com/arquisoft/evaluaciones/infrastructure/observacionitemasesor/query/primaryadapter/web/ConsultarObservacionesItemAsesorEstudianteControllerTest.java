package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.interactor.ConsultarObservacionesItemAsesorEstudianteInteractor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.primaryport.model.ConsultarObservacionesItemAsesorEstudianteQuery;
import com.arquisoft.evaluaciones.application.observacionitemasesor.query.readmodel.ObservacionItemAsesorReadModel;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorNoPerteneceEstudianteException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.EvaluacionCuantitativaAsesorNoEncontradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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
import org.springframework.http.MediaType;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarObservacionesItemAsesorEstudianteController.class)
@Import({
        IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarObservacionesItemAsesorEstudianteControllerTest.TestSecurityConfig.class
})
class ConsultarObservacionesItemAsesorEstudianteControllerTest {

    private static final UUID EVALUACION_CUANTITATIVA_ASESOR = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-cuantitativas-asesor/"
            + EVALUACION_CUANTITATIVA_ASESOR + "/observaciones/estudiante";

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
    private ConsultarObservacionesItemAsesorEstudianteInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelEstudiante(UUID estudiante) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(token -> token.subject(estudiante.toString()));
    }

    @Test
    void debeRetornar200ConElContratoExacto_yPasarEvaluacionEstudianteYDefaultsAlInteractor_cuandoNoHayBody()
            throws Exception {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var idObservacion = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(
                List.of(new ObservacionItemAsesorReadModel(idObservacion, "Sustenta el puntaje")), 0, 10, 1));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelEstudiante(estudiante)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(idObservacion.toString()))
                .andExpect(jsonPath("$.content[0].descripcion").value("Sustenta el puntaje"))
                .andExpect(jsonPath("$.content[0].evaluacionCuantitativaAsesor").doesNotExist())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultarObservacionesItemAsesorEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionCuantitativaAsesor()).isEqualTo(EVALUACION_CUANTITATIVA_ASESOR);
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
        assertThat(captor.getValue().criterio().pagina()).isZero();
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(10);
    }

    @Test
    void debeRetornar200YPasarPaginacionOrdenYFiltroAlInteractor_cuandoLlegaBody() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 1, 5, 0));
        var body = """
                {
                  "pagina": 1,
                  "tamanio": 5,
                  "ordenamiento": ["descripcion:desc"],
                  "filtros": { "tipo": "PREDICADO", "campo": "descripcion", "operador": "CONTIENE", "valor": "rigor" }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelEstudiante(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        var captor = ArgumentCaptor.forClass(ConsultarObservacionesItemAsesorEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        var criterio = captor.getValue().criterio();
        assertThat(criterio.pagina()).isEqualTo(1);
        assertThat(criterio.tamanio()).isEqualTo(5);
        assertThat(criterio.ordenamiento()).hasSize(1);
        assertThat(criterio.raiz())
                .isEqualTo(NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "rigor"));
    }

    @Test
    void debeRetornar400_cuandoElIdDeLaRutaNoEsUnUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/evaluaciones/evaluaciones-cuantitativas-asesor/no-es-un-uuid/observaciones/estudiante")
                        .with(jwtDelEstudiante(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoElInteractorRechazaElFiltroOElOrden() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(
                new FiltroException("Campo de orden no permitido", AppCodes.Consulta.CAMPO_ORDEN_NO_PERMITIDO));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelEstudiante(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoHayJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new EvaluacionCuantitativaAsesorNoEncontradaException(EVALUACION_CUANTITATIVA_ASESOR));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelEstudiante(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ObservacionItemAsesor.EVALUACION_NO_ENCONTRADA));
    }

    @Test
    void debeRetornar422_cuandoElEstudianteNoEstaVinculado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new EvaluacionAsesorNoPerteneceEstudianteException(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelEstudiante(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EvaluacionesCodes.EvaluacionAsesor.NO_PERTENECE_ESTUDIANTE));
    }
}
