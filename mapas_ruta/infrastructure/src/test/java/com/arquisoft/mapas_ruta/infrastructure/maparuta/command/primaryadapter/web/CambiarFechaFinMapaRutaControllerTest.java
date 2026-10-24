package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.CambiarFechaFinMapaRutaInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.CambiarFechaFinMapaRutaCommand;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaNoEncontradoException;
import com.arquisoft.shared.message.constant.MapasRutaCodes;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarFechaFinMapaRutaController.class)
@Import({IdentidadActualTestConfig.class, com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        CambiarFechaFinMapaRutaControllerTest.TestSecurityConfig.class})
class CambiarFechaFinMapaRutaControllerTest {

    private static final String RUTA = "/mapas-ruta/{id}/fecha-fin";

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
                            .accessDeniedHandler((req, res, e) -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CambiarFechaFinMapaRutaInteractor interactor;

    private static String body(String fechaFin) {
        return """
                {
                  "fechaFin": "%s"
                }
                """.formatted(fechaFin);
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtCoordinador(UUID coordinador) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(coordinador.toString()));
    }

    @Test
    void debeRetornar204_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();

        // Act
        mockMvc.perform(patch(RUTA, mapaRuta)
                        .with(jwtCoordinador(coordinador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("2026-12-15")))
                // Assert
                .andExpect(status().isNoContent());
        var captor = ArgumentCaptor.forClass(CambiarFechaFinMapaRutaCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().mapaRuta()).isEqualTo(mapaRuta);
        assertThat(captor.getValue().coordinador()).isEqualTo(coordinador);
        assertThat(captor.getValue().fechaFin()).isEqualTo(LocalDate.of(2026, 12, 15));
    }

    @Test
    void debeRetornar400ConFieldErrors_cuandoLaFechaEsInvalida() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .with(jwtCoordinador(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("2026-02-30")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='" + MapasRutaFields.MapaRuta.FECHA_FIN + "')]")
                        .exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoElInteractorLanzaUnaReglaDeNegocio() throws Exception {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        doThrow(new MapaRutaNoEncontradoException(mapaRuta)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, mapaRuta)
                        .with(jwtCoordinador(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("2026-12-15")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.MapaRuta.MAPA_RUTA_NO_ENCONTRADO));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("2026-12-15")))
                .andExpect(status().isUnauthorized());
    }
}
