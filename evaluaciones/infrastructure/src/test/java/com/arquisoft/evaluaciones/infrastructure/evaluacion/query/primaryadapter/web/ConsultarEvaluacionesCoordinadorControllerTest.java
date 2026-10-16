package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacion.query.primaryport.interactor.ConsultarEvaluacionesCoordinadorInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionesCoordinadorController.class)
@Import({
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesCoordinadorControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesCoordinadorControllerTest {

    private static final String RUTA = "/evaluaciones/coordinador";

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
    private ConsultarEvaluacionesCoordinadorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermisoView() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(EvaluacionesAuthorities.EVALUACION_COORDINADOR_VIEW));
    }

    @Test
    void debeRetornar200ConElContratoExacto_yPasarLosDefaultsAlInteractor_cuandoNoHayBody() throws Exception {
        // Arrange
        var idEvaluacion = UUID.randomUUID();
        var idEntregable = UUID.randomUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(
                List.of(new EvaluacionReadModel(idEvaluacion,
                        new EvaluacionReadModel.Entregable(idEntregable, "Robot seguidor", 2),
                        new EvaluacionReadModel.Estado("PENDIENTE", "Pendiente"))),
                0, 10, 1));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(idEvaluacion.toString()))
                .andExpect(jsonPath("$.content[0].entregable.id").value(idEntregable.toString()))
                .andExpect(jsonPath("$.content[0].entregable.proyecto").value("Robot seguidor"))
                .andExpect(jsonPath("$.content[0].entregable.version").value(2))
                .andExpect(jsonPath("$.content[0].estado.id").value("PENDIENTE"))
                .andExpect(jsonPath("$.content[0].estado.nombre").value("Pendiente"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().pagina()).isZero();
        assertThat(captor.getValue().tamanio()).isEqualTo(10);
    }

    @Test
    void debeRetornar200YPasarPaginacionOrdenYFiltroAlInteractor_cuandoLlegaBody() throws Exception {
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
        mockMvc.perform(post(RUTA).with(jwtConPermisoView())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(interactor).ejecutar(captor.capture());
        var query = captor.getValue();
        assertThat(query.pagina()).isEqualTo(1);
        assertThat(query.tamanio()).isEqualTo(5);
        assertThat(query.ordenamiento()).hasSize(1);
        assertThat(query.raiz()).isNotNull();
    }

    @Test
    void debeRetornar400_cuandoElInteractorRechazaElFiltro() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(
                new FiltroInvalidoException("Campo no permitido"));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoHayJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSoloTieneElClientRoleDeOtroEndpointDeEvaluaciones() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        EvaluacionesAuthorities.EVALUACION_JURADO_ESTUDIANTE_VIEW))))
                .andExpect(status().isForbidden());
    }
}
