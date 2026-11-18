package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.interactor.ConsultarHistorialEstadosProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarHistorialEstadosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.domain.estadoproyectogrado.exception.UsuarioSinAccesoEstadosProyectoGradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ProyectosCodes;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarHistorialEstadosProyectoGradoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarHistorialEstadosProyectoGradoControllerTest.TestSecurityConfig.class})
class ConsultarHistorialEstadosProyectoGradoControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/historial-estados";
    private static final UUID USUARIO = UtilUUID.generarNuevoUUID();

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
    private ConsultarHistorialEstadosProyectoGradoInteractor interactor;

    private ResultActions consultar(String proyectoGradoId) throws Exception {
        return mockMvc.perform(get(RUTA, proyectoGradoId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(USUARIO.toString()))));
    }

    @Test
    void debeResponder200_conHistorialYMotivo() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var cancelado = UtilUUID.generarNuevoUUID();
        var enCurso = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of(
                new EstadoProyectoGradoReadModel(cancelado, "CANCELADO", "Cancelado",
                        Instant.parse("2026-10-05T10:00:00Z"), "Cancelado por inactividad"),
                new EstadoProyectoGradoReadModel(enCurso, "EN_CURSO", "En curso",
                        Instant.parse("2026-09-01T10:00:00Z"), null)));

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(cancelado.toString()))
                .andExpect(jsonPath("$[0].estadoProyecto").value("CANCELADO"))
                .andExpect(jsonPath("$[0].estadoProyectoNombre").value("Cancelado"))
                .andExpect(jsonPath("$[0].fechaActualizacion").exists())
                .andExpect(jsonPath("$[0].motivo").value("Cancelado por inactividad"))
                .andExpect(jsonPath("$[1].estadoProyecto").value("EN_CURSO"))
                .andExpect(jsonPath("$[1].motivo").doesNotExist());
    }

    @Test
    void debeTomarUsuarioDeIdentidad_noDelPath() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act
        consultar(proyectoGrado.toString()).andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarHistorialEstadosProyectoGradoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().usuario()).isEqualTo(USUARIO);
    }

    @Test
    void debeResponder400_cuandoProyectoGradoIdNoEsUUID() throws Exception {
        // Act & Assert
        consultar("no-es-uuid").andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())).andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoProyectoNoExiste() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        doThrow(new ProyectoGradoNoEncontradoException(proyectoGrado)).when(interactor).ejecutar(any());

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.ProyectoGrado.NO_ENCONTRADO));
    }

    @Test
    void debeResponder422_cuandoUsuarioSinAcceso() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        doThrow(new UsuarioSinAccesoEstadosProyectoGradoException(proyectoGrado, USUARIO))
                .when(interactor).ejecutar(any());

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.EstadoProyectoGrado.USUARIO_SIN_ACCESO));
    }
}
