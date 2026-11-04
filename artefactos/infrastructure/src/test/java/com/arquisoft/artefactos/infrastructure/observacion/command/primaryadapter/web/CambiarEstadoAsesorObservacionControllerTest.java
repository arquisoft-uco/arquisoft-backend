package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web;

import com.arquisoft.artefactos.application.observacion.command.primaryport.interactor.CambiarEstadoAsesorObservacionInteractor;
import com.arquisoft.artefactos.application.observacion.command.primaryport.model.CambiarEstadoAsesorObservacionCommand;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoAsesorObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
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
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarEstadoAsesorObservacionController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, CambiarEstadoAsesorObservacionControllerTest.TestSecurityConfig.class})
class CambiarEstadoAsesorObservacionControllerTest {

    private static final String RUTA = "/artefactos/observaciones/{observacionId}/estado-asesor";

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
    private CambiarEstadoAsesorObservacionInteractor interactor;

    @Test
    void debeResponder204_cuandoValido() throws Exception {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"REABIERTA\"}"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        var captor = ArgumentCaptor.forClass(CambiarEstadoAsesorObservacionCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().observacion()).isEqualTo(observacion);
        assertThat(captor.getValue().estado()).isEqualTo("REABIERTA");
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeResponder400_cuandoEstadoAusente() throws Exception {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CERRADA\"}"))
                .andExpect(status().isUnauthorized());

        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoElInteractorLanzaDomainException() throws Exception {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        doThrow(new ObservacionNoEncontradaException(observacion)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"CERRADA\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.Observacion.NO_ENCONTRADA));
    }

    @Test
    void debeResponder422ConFieldErrors_cuandoElEstadoEstaFueraDelCatalogo() throws Exception {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        doAnswer(invocacion -> {
            var comando = invocacion.<CambiarEstadoAsesorObservacionCommand>getArgument(0);
            CambioEstadoAsesorObservacionDomain.crear(comando.observacion(), comando.estado(), comando.asesor());
            return null;
        }).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"VACIO\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]",
                        ArtefactosFields.Observacion.ESTADO).exists());
    }
}
