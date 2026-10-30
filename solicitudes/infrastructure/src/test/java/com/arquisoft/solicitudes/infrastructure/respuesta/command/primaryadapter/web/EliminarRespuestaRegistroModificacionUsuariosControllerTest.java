package com.arquisoft.solicitudes.infrastructure.respuesta.command.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.EliminarRespuestaRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudTipoNoCoincideRegistroModificacionUsuariosException;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EliminarRespuestaRegistroModificacionUsuariosController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        EliminarRespuestaRegistroModificacionUsuariosControllerTest.TestSecurityConfig.class})
class EliminarRespuestaRegistroModificacionUsuariosControllerTest {

    private static final String RUTA = "/solicitudes/registro-modificacion-usuarios/%s/respuesta";

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
    private EliminarRespuestaRegistroModificacionUsuariosInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()));
    }

    @Test
    void debe204YTomarElAdministradorDelJwt_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var solicitud = UtilUUID.generarNuevoUUID();
        var administrador = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(solicitud))
                        .with(jwtDe(administrador)))
                .andExpect(status().isNoContent());

        var captor = ArgumentCaptor.forClass(EliminarRespuestaRegistroModificacionUsuariosCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().solicitud()).isEqualTo(solicitud);
        assertThat(captor.getValue().administradorUsuario()).isEqualTo(administrador);
    }

    @Test
    void debe400_cuandoElIdDeLaSolicitudNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted("no-es-uuid"))
                        .with(jwtDe(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe422_cuandoElInteractorRechazaPorReglaDeNegocio() throws Exception {
        // Arrange
        doThrow(new SolicitudTipoNoCoincideRegistroModificacionUsuariosException()).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA.formatted(UtilUUID.generarNuevoUUID()))
                        .with(jwtDe(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnprocessableEntity());
    }
}
