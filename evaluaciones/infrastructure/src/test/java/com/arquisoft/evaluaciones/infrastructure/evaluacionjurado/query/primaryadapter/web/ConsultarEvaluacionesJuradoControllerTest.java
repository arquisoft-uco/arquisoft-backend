package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.interactor.ConsultarEvaluacionesJuradoInteractor;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model.ConsultarEvaluacionesJuradoEstudianteQuery;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.readmodel.EvaluacionJuradoReadModel;
import com.arquisoft.evaluaciones.domain.evaluacion.exception.EvaluacionNoEncontradaException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.query.exception.FiltroException;
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

@WebMvcTest(ConsultarEvaluacionesJuradoController.class)
@Import({
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesJuradoControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesJuradoControllerTest {

    private static final UUID EVALUACION_ID = UUID.randomUUID();
    private static final String RUTA = "/evaluaciones/" + EVALUACION_ID + "/evaluaciones-jurado";

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
    private ConsultarEvaluacionesJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermisoView() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(EvaluacionesAuthorities.EVALUACION_JURADO_ESTUDIANTE_VIEW));
    }

    @Test
    void debeRetornar200ConElContratoExacto_yPasarLosDefaultsAlInteractor_cuandoNoHayBody() throws Exception {
        // Arrange
        var idFila = UUID.randomUUID();
        var idJurado = UUID.randomUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(
                List.of(new EvaluacionJuradoReadModel(idFila,
                        new EvaluacionJuradoReadModel.Jurado(idJurado, "Ana Ruiz", "ana@uco.edu.co"))),
                0, 10, 1));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(idFila.toString()))
                .andExpect(jsonPath("$.content[0].jurado.id").value(idJurado.toString()))
                .andExpect(jsonPath("$.content[0].jurado.nombre").value("Ana Ruiz"))
                .andExpect(jsonPath("$.content[0].jurado.email").value("ana@uco.edu.co"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesJuradoEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacion()).isEqualTo(EVALUACION_ID);
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
                  "ordenamiento": ["jurado:desc"],
                  "filtros": { "tipo": "PREDICADO", "campo": "juradoId", "operador": "ES", "valor": "%s" }
                }
                """.formatted(UUID.randomUUID());

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtConPermisoView())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesJuradoEstudianteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        var criterio = captor.getValue().criterio();
        assertThat(criterio.pagina()).isEqualTo(1);
        assertThat(criterio.tamanio()).isEqualTo(5);
        assertThat(criterio.ordenamiento()).hasSize(1);
    }

    @Test
    void debeRetornar400_cuandoElIdDeLaRutaNoEsUnUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/evaluaciones/no-es-un-uuid/evaluaciones-jurado")
                        .with(jwtConPermisoView()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoElInteractorRechazaElFiltroOElOrden() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(
                new FiltroException("Campo de orden no permitido", AppCodes.Consulta.CAMPO_ORDEN_NO_PERMITIDO));

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
    void debeRetornar403_cuandoNoTieneElClientRoleExclusivo() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        EvaluacionesAuthorities.ESTADO_EVALUACION_VIEW))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new EvaluacionNoEncontradaException(EVALUACION_ID));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isUnprocessableEntity());
    }
}
