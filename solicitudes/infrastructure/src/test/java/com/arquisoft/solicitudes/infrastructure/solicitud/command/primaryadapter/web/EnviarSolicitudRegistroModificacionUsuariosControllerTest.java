package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web;

import com.arquisoft.shared.message.constant.SolicitudesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.destinatario.exception.DestinatarioNoEncontradoException;
import com.arquisoft.solicitudes.domain.remitente.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
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

import static org.hamcrest.Matchers.hasItems;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnviarSolicitudRegistroModificacionUsuariosController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        EnviarSolicitudRegistroModificacionUsuariosControllerTest.TestSecurityConfig.class})
class EnviarSolicitudRegistroModificacionUsuariosControllerTest {

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
    private EnviarSolicitudRegistroModificacionUsuariosInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()));
    }

    private static String body(String destinatario, String mensaje) {
        return String.format("""
                { "destinatario": "%s", "mensajeSolicitud": "%s" }
                """, destinatario, mensaje);
    }

    @Test
    void debe201YTomarElRemitenteDelJwt_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var remitente = UtilUUID.generarNuevoUUID();
        var destinatario = UtilUUID.generarNuevoUUID();
        var solicitudId = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(solicitudId);

        // Act & Assert
        mockMvc.perform(post("/solicitudes/registro-modificacion-usuarios")
                        .with(jwtDe(remitente))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(destinatario.toString(), "Solicito una registro y modificacion de usuarios por fuerza mayor")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(solicitudId.toString()));

        var captor =
                ArgumentCaptor.forClass(EnviarSolicitudRegistroModificacionUsuariosCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().remitenteUsuario()).isEqualTo(remitente);
        assertThat(captor.getValue().destinatarioUsuario()).isEqualTo(destinatario);
        assertThat(captor.getValue().mensajeSolicitud())
                .isEqualTo("Solicito una registro y modificacion de usuarios por fuerza mayor");
    }

    @Test
    void debe400ConFieldErrors_cuandoElMensajeYElDestinatarioSonInvalidos() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/solicitudes/registro-modificacion-usuarios")
                        .with(jwtDe(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("no-es-uuid", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].field").value(
                        hasItems(SolicitudesFields.Solicitud.MENSAJE, SolicitudesFields.Solicitud.DESTINATARIO)));
    }

    @Test
    void debe422_cuandoElRemitenteNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new RemitenteNoEncontradoException());

        // Act & Assert
        mockMvc.perform(post("/solicitudes/registro-modificacion-usuarios")
                        .with(jwtDe(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UtilUUID.generarNuevoUUID().toString(), "mensaje valido")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoElDestinatarioNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new DestinatarioNoEncontradoException());

        // Act & Assert
        mockMvc.perform(post("/solicitudes/registro-modificacion-usuarios")
                        .with(jwtDe(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UtilUUID.generarNuevoUUID().toString(), "mensaje valido")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoLaSolicitudEstaDuplicada() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new SolicitudDuplicadaException());

        // Act & Assert
        mockMvc.perform(post("/solicitudes/registro-modificacion-usuarios")
                        .with(jwtDe(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UtilUUID.generarNuevoUUID().toString(), "mensaje valido")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/solicitudes/registro-modificacion-usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UtilUUID.generarNuevoUUID().toString(), "mensaje valido")))
                .andExpect(status().isUnauthorized());
    }
}
