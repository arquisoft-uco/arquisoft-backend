package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.solicitud.exception.RemitenteNoEncontradoException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnviarSolicitudNovedadCoordinadorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        EnviarSolicitudNovedadCoordinadorControllerTest.TestSecurityConfig.class})
class EnviarSolicitudNovedadCoordinadorControllerTest {

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
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
    private EnviarSolicitudNovedadCoordinadorInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject, String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    private static String body(String destinatario, String mensaje) {
        return String.format("""
                { "destinatario": "%s", "mensajeSolicitud": "%s" }
                """, destinatario, mensaje);
    }

    @Test
    void debe201YTomarElRemitenteDelJwt_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        UUID remitente = UUID.randomUUID();
        UUID destinatario = UUID.randomUUID();
        UUID solicitudId = UUID.randomUUID();
        when(interactor.ejecutar(any())).thenReturn(solicitudId);

        // Act & Assert
        mockMvc.perform(post("/solicitudes/novedad-coordinador")
                        .with(jwtDe(remitente, SolicitudesAuthorities.SOLICITUD_CREATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(destinatario.toString(), "Necesito reportar una novedad")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(solicitudId.toString()));

        ArgumentCaptor<EnviarSolicitudNovedadCoordinadorCommand> captor =
                ArgumentCaptor.forClass(EnviarSolicitudNovedadCoordinadorCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().remitenteUsuario()).isEqualTo(remitente);
        assertThat(captor.getValue().destinatarioUsuario()).isEqualTo(destinatario);
        assertThat(captor.getValue().mensajeSolicitud()).isEqualTo("Necesito reportar una novedad");
    }

    @Test
    void debe400_cuandoElMensajeEstaEnBlanco() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/solicitudes/novedad-coordinador")
                        .with(jwtDe(UUID.randomUUID(), SolicitudesAuthorities.SOLICITUD_CREATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID().toString(), "")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoElDestinatarioNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/solicitudes/novedad-coordinador")
                        .with(jwtDe(UUID.randomUUID(), SolicitudesAuthorities.SOLICITUD_CREATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("no-es-uuid", "mensaje valido")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe422_cuandoElRemitenteNoExiste() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new RemitenteNoEncontradoException(UUID.randomUUID()));

        // Act & Assert
        mockMvc.perform(post("/solicitudes/novedad-coordinador")
                        .with(jwtDe(UUID.randomUUID(), SolicitudesAuthorities.SOLICITUD_CREATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID().toString(), "mensaje valido")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoLaSolicitudEstaDuplicada() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new SolicitudDuplicadaException());

        // Act & Assert
        mockMvc.perform(post("/solicitudes/novedad-coordinador")
                        .with(jwtDe(UUID.randomUUID(), SolicitudesAuthorities.SOLICITUD_CREATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID().toString(), "mensaje valido")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/solicitudes/novedad-coordinador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID().toString(), "mensaje valido")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoElTokenNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/solicitudes/novedad-coordinador")
                        .with(jwtDe(UUID.randomUUID(), "solicitudes:solicitud:read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UUID.randomUUID().toString(), "mensaje valido")))
                .andExpect(status().isForbidden());
    }
}
