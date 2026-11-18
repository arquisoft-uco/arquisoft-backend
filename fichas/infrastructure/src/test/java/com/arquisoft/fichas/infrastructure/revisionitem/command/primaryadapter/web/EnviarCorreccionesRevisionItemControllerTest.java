package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.EnviarCorreccionesRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.EnviarCorreccionesRevisionItemCommand;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemConObservacionesPendientesException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnviarCorreccionesRevisionItemController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        EnviarCorreccionesRevisionItemControllerTest.TestSecurityConfig.class})
class EnviarCorreccionesRevisionItemControllerTest {

    private static final String RUTA = "/fichas-perfil/revisiones/{revisionItemId}/correcciones";

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
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnviarCorreccionesRevisionItemInteractor enviarCorreccionesRevisionItemInteractor;

    private final UUID revisionItemId = UtilUUID.generarNuevoUUID();
    private final UUID estudianteId = UtilUUID.generarNuevoUUID();

    private RequestPostProcessor estudianteAutorizado() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(estudianteId.toString()));
    }

    @Test
    void debe204YConstruirElCommandConElSubjectDelJwt_cuandoPeticionValida() throws Exception {
        // Act
        mockMvc.perform(patch(RUTA, revisionItemId).with(estudianteAutorizado()))
                .andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(EnviarCorreccionesRevisionItemCommand.class);
        verify(enviarCorreccionesRevisionItemInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().revisionItem()).isEqualTo(revisionItemId);
        assertThat(captor.getValue().estudiante()).isEqualTo(estudianteId);
    }

    @Test
    void debe400_cuandoElPathNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, "no-es-un-uuid").with(estudianteAutorizado()))
                .andExpect(status().isBadRequest());
        verify(enviarCorreccionesRevisionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, revisionItemId))
                .andExpect(status().isUnauthorized());
        verify(enviarCorreccionesRevisionItemInteractor, never()).ejecutar(any());
    }


    @Test
    void debe422ConElCodigoDeLaRegla_cuandoHayObservacionesPendientes() throws Exception {
        // Arrange
        doThrow(new RevisionItemConObservacionesPendientesException(revisionItemId, 2))
                .when(enviarCorreccionesRevisionItemInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, revisionItemId).with(estudianteAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.CON_OBSERVACIONES_PENDIENTES));
    }
}
