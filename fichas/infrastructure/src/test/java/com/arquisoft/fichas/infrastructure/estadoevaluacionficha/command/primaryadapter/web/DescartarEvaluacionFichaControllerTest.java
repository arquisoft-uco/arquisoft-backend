package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.primaryadapter.web;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.interactor.DescartarEvaluacionFichaInteractor;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model.DescartarEvaluacionFichaCommand;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionNoDescartableException;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import org.junit.jupiter.api.Test;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DescartarEvaluacionFichaController.class)
@Import({IdentidadActualTestConfig.class, com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        DescartarEvaluacionFichaControllerTest.TestSecurityConfig.class})
class DescartarEvaluacionFichaControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(ex -> ex
                            .authenticationEntryPoint((req, res, e) -> res.sendError(401, "Unauthorized"))
                            .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DescartarEvaluacionFichaInteractor descartarEvaluacionFichaInteractor;

    @Test
    void debeRetornar204SinCuerpoConRepresentanteDelJwt() throws Exception {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        var representante = UtilUUID.generarNuevoUUID();
        var captor = forClass(DescartarEvaluacionFichaCommand.class);

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/evaluaciones/{id}/descarte", evaluacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(representante.toString()))))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(descartarEvaluacionFichaInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().evaluacionFichaPerfil()).isEqualTo(evaluacion);
        assertThat(captor.getValue().representanteComite()).isEqualTo(representante);
    }

    @Test
    void debeRetornar400_cuandoPathNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/evaluaciones/{id}/descarte", "no-es-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()))))
                .andExpect(status().isBadRequest());

        verify(descartarEvaluacionFichaInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/evaluaciones/{id}/descarte", UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoInteractorLanzaDomainException() throws Exception {
        // Arrange
        var evaluacion = UtilUUID.generarNuevoUUID();
        doThrow(new EvaluacionNoDescartableException(evaluacion, EstadoEvaluacion.APROBADA))
                .when(descartarEvaluacionFichaInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/evaluaciones/{id}/descarte", evaluacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()))))
                .andExpect(status().isUnprocessableEntity());
    }
}
