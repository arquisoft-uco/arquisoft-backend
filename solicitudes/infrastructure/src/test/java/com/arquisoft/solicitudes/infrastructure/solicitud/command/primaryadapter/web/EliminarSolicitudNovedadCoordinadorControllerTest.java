package com.arquisoft.solicitudes.infrastructure.solicitud.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EliminarSolicitudNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudConRespuestasException;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EliminarSolicitudNovedadCoordinadorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        EliminarSolicitudNovedadCoordinadorControllerTest.TestSecurityConfig.class})
class EliminarSolicitudNovedadCoordinadorControllerTest {

    private static final String RUTA = "/solicitudes/novedad-coordinador/";

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
    private EliminarSolicitudNovedadCoordinadorInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject, String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debe204YTomarElRemitenteDelJwt_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA + solicitud)
                        .with(jwtDe(remitente, SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_DELETE)))
                .andExpect(status().isNoContent());

        ArgumentCaptor<EliminarSolicitudNovedadCoordinadorCommand> captor =
                ArgumentCaptor.forClass(EliminarSolicitudNovedadCoordinadorCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().solicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().remitenteUsuario()).isEqualTo(remitente);
    }

    @Test
    void debe400_cuandoElIdDeLaSolicitudNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA + "no-es-uuid")
                        .with(jwtDe(UUID.randomUUID(),
                                SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_DELETE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA + UUID.randomUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoElTokenNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA + UUID.randomUUID())
                        .with(jwtDe(UUID.randomUUID(), "solicitudes:solicitud:read")))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoElInteractorRechazaPorReglaDeNegocio() throws Exception {
        // Arrange
        doThrow(new SolicitudConRespuestasException(UUID.randomUUID()))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA + UUID.randomUUID())
                        .with(jwtDe(UUID.randomUUID(),
                                SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_DELETE)))
                .andExpect(status().isUnprocessableEntity());
    }
}
