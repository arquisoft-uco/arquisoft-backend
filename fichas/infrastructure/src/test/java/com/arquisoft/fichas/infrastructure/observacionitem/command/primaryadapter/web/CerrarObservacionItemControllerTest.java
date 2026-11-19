package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.CerrarObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.CerrarObservacionItemCommand;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEnProgresoException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CerrarObservacionItemController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        CerrarObservacionItemControllerTest.TestSecurityConfig.class})
class CerrarObservacionItemControllerTest {

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

    private static final String RUTA = "/fichas-perfil/observaciones-item/{observacionItemId}/cierre";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CerrarObservacionItemInteractor cerrarObservacionItemInteractor;

    @Test
    void debeResponder204YEntregarElCommandConElAsesorDelJwt_cuandoPeticionValida() throws Exception {
        // Arrange
        var observacionItemId = UtilUUID.generarNuevoUUID();
        var asesorId = UtilUUID.generarNuevoUUID();

        // Act
        mockMvc.perform(patch(RUTA, observacionItemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(asesorId.toString()))))
                .andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(CerrarObservacionItemCommand.class);
        verify(cerrarObservacionItemInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().observacionItem()).isEqualTo(observacionItemId);
        assertThat(captor.getValue().asesorFicha()).isEqualTo(asesorId);
    }

    @Test
    void debeResponder400_cuandoIdNoEsUuid() throws Exception {
        // Arrange
        var asesorId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA, "no-es-un-uuid")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(asesorId.toString()))))
                .andExpect(status().isBadRequest());

        verify(cerrarObservacionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());

        verify(cerrarObservacionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoInteractorLanzaDomainException() throws Exception {
        // Arrange
        var observacionItemId = UtilUUID.generarNuevoUUID();
        var asesorId = UtilUUID.generarNuevoUUID();
        doThrow(new ObservacionItemNoEnProgresoException(observacionItemId, "PENDIENTE"))
                .when(cerrarObservacionItemInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, observacionItemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(asesorId.toString()))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.ObservacionItem.NO_EN_PROGRESO));
    }
}
