package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.ConsultarEvaluacionesAsignadasJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesAsignadasJuradoQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionAsignadaJuradoReadModel;
import com.arquisoft.evaluaciones.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.shared.message.constant.AppCodes;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionesAsignadasJuradoController.class)
@Import({IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesAsignadasJuradoControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesAsignadasJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/evaluaciones-jurado/jurado";

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
    private ConsultarEvaluacionesAsignadasJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelJurado(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(token -> token.subject(subject));
    }

    @Test
    void debeResponder200ConPagina_yPasarAlInteractorElJuradoDelJwtYLosDefaults_cuandoNoHayBody()
            throws Exception {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var evaluacion = UtilUUID.generarNuevoUUID();
        var entregable = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(new EvaluacionAsignadaJuradoReadModel(
                evaluacionJurado,
                new EvaluacionAsignadaJuradoReadModel.Estado("PENDIENTE_POR_EVALUAR", "Pendiente por evaluar"),
                new EvaluacionAsignadaJuradoReadModel.Evaluacion(evaluacion,
                        new EvaluacionAsignadaJuradoReadModel.Estado("PENDIENTE", "Pendiente")),
                new EvaluacionAsignadaJuradoReadModel.Entregable(entregable, "Robot seguidor", 2),
                true, false)),
                0, 10, 1));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelJurado(jurado.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(evaluacionJurado.toString()))
                .andExpect(jsonPath("$.content[0].estado.id").value("PENDIENTE_POR_EVALUAR"))
                .andExpect(jsonPath("$.content[0].estado.nombre").value("Pendiente por evaluar"))
                .andExpect(jsonPath("$.content[0].evaluacion.id").value(evaluacion.toString()))
                .andExpect(jsonPath("$.content[0].evaluacion.estado.id").value("PENDIENTE"))
                .andExpect(jsonPath("$.content[0].evaluacion.estado.nombre").value("Pendiente"))
                .andExpect(jsonPath("$.content[0].entregable.id").value(entregable.toString()))
                .andExpect(jsonPath("$.content[0].entregable.proyecto").value("Robot seguidor"))
                .andExpect(jsonPath("$.content[0].entregable.version").value(2))
                .andExpect(jsonPath("$.content[0].tieneEvaluacionesCualitativas").value(true))
                .andExpect(jsonPath("$.content[0].tieneEvaluacionesCuantitativas").value(false))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesAsignadasJuradoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().jurado()).isEqualTo(jurado);
        assertThat(captor.getValue().criterio().pagina()).isZero();
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(10);
    }

    @Test
    void debeResponder200YPasarPaginacionOrdenYFiltroAlInteractor_cuandoLlegaBody() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 1, 5, 0));
        var body = """
                {
                  "pagina": 1,
                  "tamanio": 5,
                  "ordenamiento": ["proyecto:desc"],
                  "filtros": { "tipo": "PREDICADO", "campo": "estadoJurado", "operador": "ES",
                               "valor": "PENDIENTE_POR_EVALUAR" }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelJurado(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesAsignadasJuradoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        var criterio = captor.getValue().criterio();
        assertThat(criterio.pagina()).isEqualTo(1);
        assertThat(criterio.tamanio()).isEqualTo(5);
        assertThat(criterio.ordenamiento()).hasSize(1);
        assertThat(criterio.raiz()).isNotNull();
    }

    @Test
    void debeResponder400_cuandoElInteractorRechazaElFiltroOElOrden() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(
                new FiltroException("Campo de orden no permitido", AppCodes.Consulta.CAMPO_ORDEN_NO_PERMITIDO));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelJurado(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoElJuradoNoExiste() throws Exception {
        // Arrange
        var jurado = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenThrow(new JuradoNoEncontradoException(jurado));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelJurado(jurado.toString())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeResponder401_cuandoElSubjectNoEsUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelJurado("no-es-uuid")))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
