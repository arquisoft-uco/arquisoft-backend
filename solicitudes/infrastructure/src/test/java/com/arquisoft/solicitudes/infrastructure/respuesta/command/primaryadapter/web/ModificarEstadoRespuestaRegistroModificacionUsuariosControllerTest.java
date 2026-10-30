package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ModificarEstadoRespuestaRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarEstadoRespuestaRegistroModificacionUsuariosController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ModificarEstadoRespuestaRegistroModificacionUsuariosControllerTest.TestSecurityConfig.class})
class ModificarEstadoRespuestaRegistroModificacionUsuariosControllerTest {

    private static final String RUTA = "/solicitudes/registro-modificacion-usuarios/%s/respuesta/estado";

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
    private ModificarEstadoRespuestaRegistroModificacionUsuariosInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()));
    }

    @Test
    void debeResponder204_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA.formatted(solicitud))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"APROBADA\"}")
                        .with(jwtDe(administrador)))
                .andExpect(status().isNoContent());

        var captor = ArgumentCaptor.forClass(ModificarEstadoRespuestaRegistroModificacionUsuariosCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().solicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().nuevoEstado()).isEqualTo("APROBADA");
        assertThat(captor.getValue().administradorUsuario()).isEqualTo(administrador);
    }

    @Test
    void debeResponder400_cuandoNuevoEstadoEstaEnBlanco() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"  \"}")
                        .with(jwtDe(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder400_cuandoElSolicitudIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/solicitudes/registro-modificacion-usuarios/no-es-uuid/respuesta/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"APROBADA\"}")
                        .with(jwtDe(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"APROBADA\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoElInteractorLanzaDomainException() throws Exception {
        // Arrange
        doThrow(new RespuestaNoEnRevisionException()).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"APROBADA\"}")
                        .with(jwtDe(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnprocessableEntity());
    }
}
