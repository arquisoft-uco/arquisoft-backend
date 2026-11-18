package com.arquisoft.proyectos.infrastructure.proyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.interactor.CambiarEstadoProyectoGradoInteractor;
import com.arquisoft.proyectos.application.proyectogrado.command.primaryport.model.CambiarEstadoProyectoGradoCommand;
import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.exception.TransicionEstadoProyectoNoPermitidaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarEstadoProyectoGradoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, CambiarEstadoProyectoGradoControllerTest.TestSecurityConfig.class})
class CambiarEstadoProyectoGradoControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/estado";
    private static final UUID ASESOR = UtilUUID.generarNuevoUUID();

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
    private CambiarEstadoProyectoGradoInteractor cambiarEstadoProyectoGradoInteractor;

    private static String cuerpo(String estado) {
        return "{\"estadoProyecto\": " + (estado == null ? "null" : "\"" + estado + "\"") + "}";
    }

    private ResultActions enviar(UUID proyectoGradoId, String body) throws Exception {
        return mockMvc.perform(patch(RUTA, proyectoGradoId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(ASESOR.toString())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @Test
    void debe204YPasarProyectoYAsesorDelJwt_cuandoPeticionValida() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();

        // Act
        enviar(proyectoGradoId, cuerpo("FINALIZADO"))
                .andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(CambiarEstadoProyectoGradoCommand.class);
        verify(cambiarEstadoProyectoGradoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGradoId);
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR);
        assertThat(captor.getValue().estadoProyecto()).isEqualTo("FINALIZADO");
    }

    @Test
    void debe400_cuandoElEstadoEstaAusenteOEnBlanco() throws Exception {
        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), cuerpo(" "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(ProyectosFields.ProyectoGrado.ESTADO_PROYECTO));
        enviar(UtilUUID.generarNuevoUUID(), cuerpo(null))
                .andExpect(status().isBadRequest());
        verify(cambiarEstadoProyectoGradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("FINALIZADO")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        doThrow(new TransicionEstadoProyectoNoPermitidaException(UtilUUID.generarNuevoUUID(), "Sistema",
                EstadoProyecto.FINALIZADO, EstadoProyecto.FINALIZADO))
                .when(cambiarEstadoProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), cuerpo("FINALIZADO"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(ProyectosCodes.ProyectoGrado.TRANSICION_ESTADO_NO_PERMITIDA));
    }
}
