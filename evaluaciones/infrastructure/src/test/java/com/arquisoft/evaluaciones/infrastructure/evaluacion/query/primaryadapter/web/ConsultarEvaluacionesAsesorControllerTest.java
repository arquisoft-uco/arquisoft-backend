package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.ConsultarEvaluacionesAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.model.ConsultarEvaluacionesAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
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

@WebMvcTest(ConsultarEvaluacionesAsesorController.class)
@Import({
        IdentidadActualTestConfig.class,
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesAsesorControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/asesor";

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
    private ConsultarEvaluacionesAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(token -> token.subject(sub));
    }

    @Test
    void debeRetornar200ConElContratoExacto_yPasarElSubDelJwtYLosDefaultsAlInteractor() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var idEvaluacion = UtilUUID.generarNuevoUUID();
        var idEntregable = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(
                List.of(new EvaluacionReadModel(idEvaluacion,
                        new EvaluacionReadModel.Entregable(idEntregable, "Robot seguidor", 2),
                        new EvaluacionReadModel.Estado("PENDIENTE", "Pendiente"))),
                0, 10, 1));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelAsesor(asesor.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(idEvaluacion.toString()))
                .andExpect(jsonPath("$.content[0].entregable.id").value(idEntregable.toString()))
                .andExpect(jsonPath("$.content[0].entregable.proyecto").value("Robot seguidor"))
                .andExpect(jsonPath("$.content[0].entregable.version").value(2))
                .andExpect(jsonPath("$.content[0].estado.id").value("PENDIENTE"))
                .andExpect(jsonPath("$.content[0].estado.nombre").value("Pendiente"))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesAsesorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        var query = captor.getValue();
        assertThat(query.asesor()).isEqualTo(asesor);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
    }

    @Test
    void debePasarPaginacionOrdenYFiltroDelBodyAlInteractor() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 1, 5, 0));
        var body = """
                {
                  "pagina": 1,
                  "tamanio": 5,
                  "ordenamiento": ["estado:desc"],
                  "filtros": { "tipo": "PREDICADO", "campo": "proyecto", "operador": "CONTIENE", "valor": "robot" }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesAsesorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        var criterio = captor.getValue().criterio();
        assertThat(criterio.pagina()).isEqualTo(1);
        assertThat(criterio.tamanio()).isEqualTo(5);
        assertThat(criterio.ordenamiento()).hasSize(1);
        assertThat(criterio.raiz()).isNotNull();
    }

    @Test
    void debeRetornar400_cuandoElInteractorRechazaElFiltro() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new FiltroInvalidoException("Campo no permitido"));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401SinInvocarAlInteractor_cuandoElSubNoEsUnUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtDelAsesor("no-es-un-uuid")))
                .andExpect(status().isUnauthorized());

        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoNoHayJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized());
    }

}
