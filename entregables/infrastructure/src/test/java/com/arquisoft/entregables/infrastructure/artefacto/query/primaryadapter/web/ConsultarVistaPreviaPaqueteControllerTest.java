package com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web;

import com.arquisoft.entregables.application.artefacto.query.primaryport.interactor.ConsultarVistaPreviaPaqueteInteractor;
import com.arquisoft.entregables.application.artefacto.query.primaryport.model.ConsultarVistaPreviaPaqueteQuery;
import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarVistaPreviaPaqueteController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarVistaPreviaPaqueteControllerTest.TestSecurityConfig.class})
class ConsultarVistaPreviaPaqueteControllerTest {

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
    private ConsultarVistaPreviaPaqueteInteractor interactor;

    private static RequestPostProcessor token() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(UtilUUID.generarNuevoUUID().toString()));
    }

    private static String ruta(Object proyecto) {
        return "/entregables/proyectos-grado/" + proyecto + "/vista-previa-paquete";
    }

    @Test
    void debeResponder200ConCantidadYArtefactos_cuandoElProyectoTieneVigentes() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of(
                new ArtefactoVistaPreviaReadModel(artefacto, "Anteproyecto", 2),
                new ArtefactoVistaPreviaReadModel(UtilUUID.generarNuevoUUID(), "Informe", 1)));

        // Act & Assert
        mockMvc.perform(get(ruta(proyecto)).with(token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(2))
                .andExpect(jsonPath("$.artefactos[0].artefacto").value(artefacto.toString()))
                .andExpect(jsonPath("$.artefactos[0].documento").value("Anteproyecto"))
                .andExpect(jsonPath("$.artefactos[0].version").value(2));
        var captor = ArgumentCaptor.forClass(ConsultarVistaPreviaPaqueteQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
    }

    @Test
    void debeResponder200ConCantidadCero_cuandoNoHayArtefactosVigentes() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID())).with(token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(0))
                .andExpect(jsonPath("$.artefactos").isEmpty());
    }

    @Test
    void debeResponder400YNoInvocarElInteractor_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta("no-es-uuid")).with(token()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401YNoInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
