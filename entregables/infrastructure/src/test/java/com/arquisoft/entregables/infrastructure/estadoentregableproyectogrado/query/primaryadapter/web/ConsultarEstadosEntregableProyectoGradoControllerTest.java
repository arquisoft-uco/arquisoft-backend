package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.interactor.ConsultarEstadosEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.model.ConsultarEstadosEntregableProyectoGradoQuery;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel.EstadoEntregableProyectoGradoReadModel;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EntregableProyectoGradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.EntregablesCodes;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosEntregableProyectoGradoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosEntregableProyectoGradoControllerTest.TestSecurityConfig.class})
class ConsultarEstadosEntregableProyectoGradoControllerTest {

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
    private ConsultarEstadosEntregableProyectoGradoInteractor interactor;

    private static String ruta(String entregable) {
        return "/entregables/" + entregable + "/estados";
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt(UUID asesor) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString()));
    }

    @Test
    void debeResponder200ConElHistorial() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-11-10T10:00:00Z");
        var readModel = new EstadoEntregableProyectoGradoReadModel(id, "DISPONIBLE_EVALUACION",
                "Disponible Evaluacion", fecha);
        when(interactor.ejecutar(any(ConsultarEstadosEntregableProyectoGradoQuery.class)))
                .thenReturn(List.of(readModel));

        // Act & Assert
        mockMvc.perform(get(ruta(entregable.toString())).with(jwt(asesor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].estadoEntregable").value("DISPONIBLE_EVALUACION"))
                .andExpect(jsonPath("$[0].estadoEntregableNombre").value("Disponible Evaluacion"))
                .andExpect(jsonPath("$[0].fechaActualizacion").exists());
        var captor = ArgumentCaptor.forClass(ConsultarEstadosEntregableProyectoGradoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeResponder400YNoInvocarElInteractor_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta("no-es-uuid")).with(jwt(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any(ConsultarEstadosEntregableProyectoGradoQuery.class));
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any(ConsultarEstadosEntregableProyectoGradoQuery.class));
    }

    @Test
    void debeResponder422_cuandoElEntregableNoExiste() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarEstadosEntregableProyectoGradoQuery.class)))
                .thenThrow(new EntregableProyectoGradoNoEncontradoException(entregable));

        // Act & Assert
        mockMvc.perform(get(ruta(entregable.toString())).with(jwt(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EntregablesCodes.EntregableProyectoGrado.NO_ENCONTRADO));
    }
}
