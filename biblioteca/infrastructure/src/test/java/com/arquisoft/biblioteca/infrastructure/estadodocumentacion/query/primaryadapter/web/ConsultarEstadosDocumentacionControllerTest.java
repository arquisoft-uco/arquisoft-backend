package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.primaryport.interactor.ConsultarEstadosDocumentacionInteractor;
import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
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

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosDocumentacionController.class)
@Import({AppLoggerConfig.class, IdentidadActualTestConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosDocumentacionControllerTest.TestSecurityConfig.class})
class ConsultarEstadosDocumentacionControllerTest {

    private static final String RUTA = "/biblioteca/estados-documentacion";

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
    private ConsultarEstadosDocumentacionInteractor consultarEstadosDocumentacionInteractor;

    @Test
    void debeRetornar200ConLosEstados_cuandoEstaAutenticado() throws Exception {
        // Arrange
        var estados = List.of(
                new EstadoDocumentacionReadModel("PENDIENTE", "Pendiente", "Por publicar"),
                new EstadoDocumentacionReadModel("PUBLICADO", "Publicado", "Publicada"));
        when(consultarEstadosDocumentacionInteractor.ejecutar()).thenReturn(estados);
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].nombre").value("Pendiente"))
                .andExpect(jsonPath("$[0].descripcion").value("Por publicar"))
                .andExpect(jsonPath("$[1].id").value("PUBLICADO"));
        verify(consultarEstadosDocumentacionInteractor).ejecutar();
    }

    @Test
    void debeRetornar401SinInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(consultarEstadosDocumentacionInteractor);
    }
}
