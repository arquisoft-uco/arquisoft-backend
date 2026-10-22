package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.interactor.ConsultarEvaluacionesCualitativasJuradoRealizadasInteractor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoRealizadasQuery;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoNoPerteneceJuradoException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEvaluacionesCualitativasJuradoRealizadasController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarEvaluacionesCualitativasJuradoRealizadasControllerTest.TestSecurityConfig.class
})
class ConsultarEvaluacionesCualitativasJuradoRealizadasControllerTest {

    private static final UUID EVALUACION_JURADO_ID = UtilUUID.generarNuevoUUID();
    private static final UUID JURADO_ID = UtilUUID.generarNuevoUUID();
    private static final String RUTA =
            "/evaluaciones/evaluaciones-jurado/" + EVALUACION_JURADO_ID + "/cualitativas/jurado";

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
    private ConsultarEvaluacionesCualitativasJuradoRealizadasInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtJuradoConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(token -> token.subject(JURADO_ID.toString()))
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.EVALUACION_CUALITATIVA_JURADO_JURADO_VIEW));
    }

    @Test
    void debeResponder200_yPasarEvaluacionJuradoYSujetoDelJwtAlInteractor() throws Exception {
        // Arrange
        var idEvaluacionCualitativa = UtilUUID.generarNuevoUUID();
        var idItem = UtilUUID.generarNuevoUUID();
        var idCriterio = UtilUUID.generarNuevoUUID();
        var resultado = List.of(new EvaluacionCualitativaJuradoReadModel(
                idEvaluacionCualitativa,
                new ItemCualitativoJuradoReadModel(idItem, "Claridad", "Evalua la claridad"),
                new CriterioItemCualitativoJuradoReadModel(idCriterio, "Excelente", "Cumple todo")));
        when(interactor.ejecutar(any())).thenReturn(resultado);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJuradoConPermiso()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(idEvaluacionCualitativa.toString()))
                .andExpect(jsonPath("$[0].item.nombre").value("Claridad"))
                .andExpect(jsonPath("$[0].criterio.nombre").value("Excelente"));

        var captor = ArgumentCaptor.forClass(ConsultarEvaluacionesCualitativasJuradoRealizadasQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionJurado()).isEqualTo(EVALUACION_JURADO_ID);
        assertThat(captor.getValue().jurado()).isEqualTo(JURADO_ID);
    }

    @Test
    void debeResponder200ListaVacia_cuandoNoHayEvaluaciones() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJuradoConPermiso()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeResponder400_cuandoIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/evaluaciones/evaluaciones-jurado/no-es-uuid/cualitativas/jurado")
                        .with(jwtJuradoConPermiso()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_sinAuthority() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(new SimpleGrantedAuthority(
                                EvaluacionesAuthorities.EVALUACION_CUALITATIVA_JURADO_ESTUDIANTE_VIEW))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeResponder422_cuandoNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new EvaluacionJuradoNoEncontradaException(EVALUACION_JURADO_ID));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJuradoConPermiso()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(
                        EvaluacionesCodes.EvaluacionCualitativaJurado.EVALUACION_JURADO_NO_ENCONTRADA));
    }

    @Test
    void debeResponder422_cuandoNoEsPropietario() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new EvaluacionJuradoNoPerteneceJuradoException(EVALUACION_JURADO_ID));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJuradoConPermiso()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(
                        EvaluacionesCodes.EvaluacionCualitativaJurado.NO_PERTENECE_JURADO));
    }
}
