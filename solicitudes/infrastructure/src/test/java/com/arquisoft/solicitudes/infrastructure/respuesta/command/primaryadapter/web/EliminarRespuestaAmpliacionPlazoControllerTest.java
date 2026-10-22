package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.EliminarRespuestaAmpliacionPlazoInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideAmpliacionPlazoException;
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

@WebMvcTest(EliminarRespuestaAmpliacionPlazoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        EliminarRespuestaAmpliacionPlazoControllerTest.TestSecurityConfig.class})
class EliminarRespuestaAmpliacionPlazoControllerTest {

    private static final String RUTA = "/solicitudes/ampliacion-plazo/%s/respuesta";

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
    private EliminarRespuestaAmpliacionPlazoInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject, String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debe204YTomarElCoordinadorDelJwt_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(solicitud))
                        .with(jwtDe(coordinador, SolicitudesAuthorities.RESPUESTA_AMPLIACION_PLAZO_DELETE)))
                .andExpect(status().isNoContent());

        var captor = ArgumentCaptor.forClass(EliminarRespuestaAmpliacionPlazoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().solicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().coordinadorUsuario()).isEqualTo(coordinador);
    }

    @Test
    void debe400_cuandoElIdDeLaSolicitudNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted("no-es-uuid"))
                        .with(jwtDe(UtilUUID.generarNuevoUUID(),
                                SolicitudesAuthorities.RESPUESTA_AMPLIACION_PLAZO_DELETE)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoElTokenNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .with(jwtDe(UtilUUID.generarNuevoUUID(),
                                SolicitudesAuthorities.RESPUESTA_AMPLIACION_PLAZO_CREATE)))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoElInteractorRechazaPorReglaDeNegocio() throws Exception {
        // Arrange
        doThrow(new SolicitudTipoNoCoincideAmpliacionPlazoException()).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .with(jwtDe(UtilUUID.generarNuevoUUID(),
                                SolicitudesAuthorities.RESPUESTA_AMPLIACION_PLAZO_DELETE)))
                .andExpect(status().isUnprocessableEntity());
    }
}
