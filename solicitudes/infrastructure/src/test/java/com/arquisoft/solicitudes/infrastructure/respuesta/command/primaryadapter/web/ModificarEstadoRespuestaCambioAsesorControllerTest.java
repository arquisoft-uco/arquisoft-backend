package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ModificarEstadoRespuestaCambioAsesorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaCambioAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarEstadoRespuestaCambioAsesorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ModificarEstadoRespuestaCambioAsesorControllerTest.TestSecurityConfig.class})
class ModificarEstadoRespuestaCambioAsesorControllerTest {

    private static final String RUTA = "/solicitudes/cambio-asesor/%s/respuesta/estado";

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
    private ModificarEstadoRespuestaCambioAsesorInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject, String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeResponder204_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(patch(RUTA.formatted(solicitud))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"APROBADA\"}")
                        .with(jwtDe(coordinador, SolicitudesAuthorities.RESPUESTA_CAMBIO_ASESOR_UPDATE)))
                .andExpect(status().isNoContent());

        var captor = ArgumentCaptor.forClass(ModificarEstadoRespuestaCambioAsesorCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().solicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().nuevoEstado()).isEqualTo("APROBADA");
        assertThat(captor.getValue().coordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debeResponder400_cuandoNuevoEstadoEstaEnBlanco() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"  \"}")
                        .with(jwtDe(UtilUUID.generarNuevoUUID(),
                                SolicitudesAuthorities.RESPUESTA_CAMBIO_ASESOR_UPDATE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder400_cuandoElSolicitudIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/solicitudes/cambio-asesor/no-es-uuid/respuesta/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"APROBADA\"}")
                        .with(jwtDe(UtilUUID.generarNuevoUUID(),
                                SolicitudesAuthorities.RESPUESTA_CAMBIO_ASESOR_UPDATE)))
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
    void debeResponder403_cuandoFaltaElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"APROBADA\"}")
                        .with(jwtDe(UtilUUID.generarNuevoUUID(),
                                SolicitudesAuthorities.RESPUESTA_CAMBIO_ASESOR_CREATE)))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeResponder422_cuandoElInteractorLanzaDomainException() throws Exception {
        // Arrange
        doThrow(new RespuestaNoEnRevisionException()).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nuevoEstado\":\"APROBADA\"}")
                        .with(jwtDe(UtilUUID.generarNuevoUUID(),
                                SolicitudesAuthorities.RESPUESTA_CAMBIO_ASESOR_UPDATE)))
                .andExpect(status().isUnprocessableEntity());
    }
}
