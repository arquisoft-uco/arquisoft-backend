package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.EliminarRespuestaNovedadAsesorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideException;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EliminarRespuestaNovedadAsesorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        EliminarRespuestaNovedadAsesorControllerTest.TestSecurityConfig.class})
class EliminarRespuestaNovedadAsesorControllerTest {

    private static final String RUTA = "/solicitudes/novedad-asesor/%s/respuesta";

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
    private EliminarRespuestaNovedadAsesorInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject, String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debe204YTomarElAsesorDelJwt_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var solicitud = UUID.randomUUID();
        var asesor = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(solicitud))
                        .with(jwtDe(asesor, SolicitudesAuthorities.RESPUESTA_NOVEDAD_ASESOR_DELETE)))
                .andExpect(status().isNoContent());

        var captor = ArgumentCaptor.forClass(EliminarRespuestaNovedadAsesorCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().solicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().asesorUsuario()).isEqualTo(asesor);
    }

    @Test
    void debe400_cuandoElIdDeLaSolicitudNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted("no-es-uuid"))
                        .with(jwtDe(UUID.randomUUID(),
                                SolicitudesAuthorities.RESPUESTA_NOVEDAD_ASESOR_DELETE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(UUID.randomUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoElTokenSoloTieneElClientRoleDeOtroEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(UUID.randomUUID()))
                        .with(jwtDe(UUID.randomUUID(),
                                SolicitudesAuthorities.RESPUESTA_NOVEDAD_ASESOR_CREATE)))
                .andExpect(status().isForbidden());
    }

    static Stream<DomainException> excepcionesDeDominio() {
        return Stream.of(
                new SolicitudNoEncontradaException(UUID.randomUUID()),
                new SolicitudTipoNoCoincideException(UUID.randomUUID()),
                new SolicitudNoEsDestinatarioException(UUID.randomUUID()),
                new RespuestaNoEncontradaException(UUID.randomUUID()),
                new RespuestaNoEnRevisionException(UUID.randomUUID()));
    }

    @ParameterizedTest
    @MethodSource("excepcionesDeDominio")
    void debe422_cuandoElInteractorRechazaPorReglaDeNegocio(DomainException excepcion) throws Exception {
        // Arrange
        doThrow(excepcion).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(UUID.randomUUID()))
                        .with(jwtDe(UUID.randomUUID(),
                                SolicitudesAuthorities.RESPUESTA_NOVEDAD_ASESOR_DELETE)))
                .andExpect(status().isUnprocessableEntity());
    }
}
