package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor.AsignarAsesorProyectoGradoInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.AsignarAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.CupoAsesoresExcedidoException;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.constant.ProyectosLimits;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsignarAsesorProyectoGradoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AsignarAsesorProyectoGradoControllerTest.TestSecurityConfig.class})
class AsignarAsesorProyectoGradoControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/asesores";
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
    private AsignarAsesorProyectoGradoInteractor asignarAsesorProyectoGradoInteractor;

    private static String cuerpo(String asesor) {
        return "{\"asesor\": " + (asesor == null ? "null" : "\"" + asesor + "\"") + "}";
    }

    private ResultActions enviar(UUID proyectoGradoId, String body) throws Exception {
        return mockMvc.perform(post(RUTA, proyectoGradoId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(COORDINADOR.toString()))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.ASESOR_PROYECTO_GRADO_CREATE)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @Test
    void debe201ConElIdYPasarProyectoYCoordinadorDelJwt_cuandoPeticionValida() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var idVinculo = UtilUUID.generarNuevoUUID();
        when(asignarAsesorProyectoGradoInteractor.ejecutar(any())).thenReturn(idVinculo);

        // Act
        enviar(proyectoGradoId, cuerpo(asesor.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idVinculo.toString()));

        // Assert
        var captor = ArgumentCaptor.forClass(AsignarAsesorProyectoGradoCommand.class);
        verify(asignarAsesorProyectoGradoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGradoId);
        assertThat(captor.getValue().coordinador()).isEqualTo(COORDINADOR);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debe400_cuandoElAsesorEsNuloONoEsUuid() throws Exception {
        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), cuerpo("no-es-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(ProyectosFields.AsesorProyectoGrado.ASESOR));
        enviar(UtilUUID.generarNuevoUUID(), cuerpo(null)).andExpect(status().isBadRequest());
        verify(asignarAsesorProyectoGradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_sinClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("proyectos:otra-autoridad")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoUnaReglaDeDominioRechaza() throws Exception {
        // Arrange
        doThrow(new CupoAsesoresExcedidoException(ProyectosLimits.AsesorProyectoGrado.MAX_ASESORES_ASIGNADOS))
                .when(asignarAsesorProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), cuerpo(UtilUUID.generarNuevoUUID().toString()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.AsesorProyectoGrado.CUPO_ASESORES_EXCEDIDO));
    }
}
