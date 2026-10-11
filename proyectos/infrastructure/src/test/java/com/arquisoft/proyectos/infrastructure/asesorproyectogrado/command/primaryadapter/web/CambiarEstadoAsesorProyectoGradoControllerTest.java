package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.CambiarEstadoAsesorProyectoGradoInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.CambiarEstadoAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.TransicionEstadoAsesorInvalidaException;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

@WebMvcTest(CambiarEstadoAsesorProyectoGradoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        CambiarEstadoAsesorProyectoGradoControllerTest.TestSecurityConfig.class})
class CambiarEstadoAsesorProyectoGradoControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/asesores/{asesorId}/estado";
    private static final UUID COORDINADOR = UtilUUID.generarNuevoUUID();

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
                            .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CambiarEstadoAsesorProyectoGradoInteractor cambiarEstadoAsesorProyectoGradoInteractor;

    private static String cuerpo(String estado) {
        return "{\"estadoAsesor\": " + (estado == null ? "null" : "\"" + estado + "\"") + "}";
    }

    private ResultActions enviar(UUID proyectoGradoId, UUID asesorId, String body) throws Exception {
        return mockMvc.perform(patch(RUTA, proyectoGradoId, asesorId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(COORDINADOR.toString()))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.ASESOR_PROYECTO_GRADO_UPDATE_ESTADO)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @Test
    void debe204YPasarIdsYCoordinadorDelJwt_cuandoPeticionValida() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();
        var asesorId = UtilUUID.generarNuevoUUID();

        // Act
        enviar(proyectoGradoId, asesorId, cuerpo("FINALIZADO"))
                .andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(CambiarEstadoAsesorProyectoGradoCommand.class);
        verify(cambiarEstadoAsesorProyectoGradoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGradoId);
        assertThat(captor.getValue().asesor()).isEqualTo(asesorId);
        assertThat(captor.getValue().coordinador()).isEqualTo(COORDINADOR);
        assertThat(captor.getValue().estadoAsesor()).isEqualTo("FINALIZADO");
    }

    @Test
    void debe400_cuandoElEstadoEstaAusenteOEnBlanco() throws Exception {
        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), cuerpo(" "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(ProyectosFields.AsesorProyectoGrado.ESTADO_ASESOR));
        enviar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), cuerpo(null))
                .andExpect(status().isBadRequest());
        verify(cambiarEstadoAsesorProyectoGradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("FINALIZADO")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_sinClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("proyectos:otra-autoridad")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("FINALIZADO")))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        doThrow(new TransicionEstadoAsesorInvalidaException(EstadoAsesor.FINALIZADO, EstadoAsesor.FINALIZADO))
                .when(cambiarEstadoAsesorProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), cuerpo("FINALIZADO"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(ProyectosCodes.AsesorProyectoGrado.TRANSICION_ESTADO_ASESOR_INVALIDA));
    }
}
