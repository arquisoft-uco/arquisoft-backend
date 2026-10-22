package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.interactor.AsignarTareaInteractor;
import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.AsignarTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.exception.TareaDuplicadaException;
import com.arquisoft.mapas_ruta.infrastructure.security.MapasRutaAuthorities;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsignarTareaController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AsignarTareaControllerTest.TestSecurityConfig.class})
class AsignarTareaControllerTest {

    private static final String RUTA = "/mapas-ruta/{mapaRutaId}/tareas";

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
    private AsignarTareaInteractor interactor;

    private static String body(String descripcion, String asesor) {
        return """
                {
                  "descripcion": "%s",
                  "asesor": "%s"
                }
                """.formatted(descripcion, asesor);
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtEstudiante() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(MapasRutaAuthorities.TAREA_CREATE));
    }

    @Test
    void debeRetornar201ConId_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(AsignarTareaCommand.class))).thenReturn(id);

        // Act
        mockMvc.perform(post(RUTA, mapaRuta)
                        .with(jwtEstudiante())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Redactar capitulo", asesor.toString())))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
        var captor = ArgumentCaptor.forClass(AsignarTareaCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().mapaRuta()).isEqualTo(mapaRuta);
        assertThat(captor.getValue().descripcion()).isEqualTo("Redactar capitulo");
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeRetornar400ConFieldErrors_cuandoDescripcionFaltaYAsesorNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(jwtEstudiante())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(" ", "no-uuid")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(2))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='" + MapasRutaFields.Tarea.DESCRIPCION + "')]")
                        .exists())
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='" + MapasRutaFields.Tarea.ASESOR + "')]")
                        .exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoElInteractorLanzaUnaReglaDeNegocio() throws Exception {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        doThrow(new TareaDuplicadaException("Redactar capitulo", mapaRuta)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA, mapaRuta)
                        .with(jwtEstudiante())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Redactar capitulo", UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.Tarea.TAREA_DUPLICADA));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Tarea", UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(MapasRutaAuthorities.MAPA_RUTA_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("Tarea", UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isForbidden());
    }
}
