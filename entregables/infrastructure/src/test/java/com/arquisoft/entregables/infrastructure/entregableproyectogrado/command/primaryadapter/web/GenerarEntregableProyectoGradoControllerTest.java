package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.web;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.GenerarEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.GenerarEntregableProyectoGradoCommand;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenerarEntregableProyectoGradoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, GenerarEntregableProyectoGradoControllerTest.TestSecurityConfig.class})
class GenerarEntregableProyectoGradoControllerTest {

    private static final UUID ASESOR_ID = UtilUUID.generarNuevoUUID();

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
    private GenerarEntregableProyectoGradoInteractor interactor;

    private static RequestPostProcessor tokenAsesor() {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(ASESOR_ID.toString()));
    }

    private static String ruta(Object proyecto) {
        return "/proyectos-grado/" + proyecto + "/entregables";
    }

    @Test
    void debeResponder201ConIdYUsarElAsesorDelToken_cuandoEsValido() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post(ruta(proyecto)).with(tokenAsesor()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
        var captor = ArgumentCaptor.forClass(GenerarEntregableProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyecto);
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR_ID);
    }

    @Test
    void debeResponder400YNoInvocarElInteractor_cuandoProyectoGradoIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta("no-es-uuid")).with(tokenAsesor()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422ConElCodigoDeError_cuandoElProyectoNoExiste() throws Exception {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenThrow(new ProyectoGradoNoEncontradoException(proyecto));

        // Act & Assert
        mockMvc.perform(post(ruta(proyecto)).with(tokenAsesor()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EntregablesCodes.ProyectoGrado.NO_ENCONTRADO));
    }

    @Test
    void debeResponder401YNoInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
