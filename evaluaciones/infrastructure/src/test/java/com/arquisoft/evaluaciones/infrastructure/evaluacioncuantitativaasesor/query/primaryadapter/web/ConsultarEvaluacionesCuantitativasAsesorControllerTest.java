package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.interactor.ConsultarEvaluacionesCuantitativasAsesorInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.primaryport.model.ConsultarEvaluacionesCuantitativasAsesorQuery;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoPerteneceAsesorException;
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

@WebMvcTest(ConsultarEvaluacionesCuantitativasAsesorController.class)
@Import({
        IdentidadActualTestConfig.class,
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesCuantitativasAsesorControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesCuantitativasAsesorControllerTest {

    private static final UUID EVALUACION_ASESOR_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA = "/evaluaciones/evaluaciones-asesor/" + EVALUACION_ASESOR_ID + "/cuantitativas";

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
    private ConsultarEvaluacionesCuantitativasAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDelAsesor(String sub) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(sub));
    }

    @Test
    void debeRetornar200ConElContratoExacto_yPasarPathYSubDelJwtAlInteractor() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var idCuantitativa = UtilUUID.generarNuevoUUID();
        var idItem = UtilUUID.generarNuevoUUID();
        var idCategoria = UtilUUID.generarNuevoUUID();
        var resultado = List.of(new EvaluacionCuantitativaAsesorReadModel(
                idCuantitativa,
                4,
                new ItemCuantitativoAsesorReadModel(idItem, "Claridad", "Evalua la claridad", idCategoria, 10)));
        when(interactor.ejecutar(any())).thenReturn(resultado);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelAsesor(asesor.toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(idCuantitativa.toString()))
                .andExpect(jsonPath("$[0].puntaje").value(4))
                .andExpect(jsonPath("$[0].item.id").value(idItem.toString()))
                .andExpect(jsonPath("$[0].item.nombre").value("Claridad"))
                .andExpect(jsonPath("$[0].item.descripcion").value("Evalua la claridad"))
                .andExpect(jsonPath("$[0].item.categoriaId").value(idCategoria.toString()))
                .andExpect(jsonPath("$[0].item.valor").value(10));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesCuantitativasAsesorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionAsesor()).isEqualTo(EVALUACION_ASESOR_ID);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoLaEvaluacionNoTieneCuantitativas() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornar401_cuandoElSubDelJwtNoEsUnUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelAsesor("no-es-uuid")))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoNoHayJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoLaEvaluacionNoPerteneceAlAsesor() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new EvaluacionAsesorNoPerteneceAsesorException(EVALUACION_ASESOR_ID));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtDelAsesor(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.EvaluacionCualitativaAsesor.NO_PERTENECE_ASESOR));
    }
}
