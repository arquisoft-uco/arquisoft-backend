package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.interactor.AgregarMapaRutaInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.AgregarMapaRutaCommand;
import com.arquisoft.mapas_ruta.domain.maparuta.exception.MapaRutaDuplicadoException;
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

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgregarMapaRutaController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AgregarMapaRutaControllerTest.TestSecurityConfig.class})
class AgregarMapaRutaControllerTest {

    private static final String RUTA = "/mapas-ruta";

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
    private AgregarMapaRutaInteractor interactor;

    private static String bodyValido(UUID proyectoGrado) {
        return """
                {
                  "proyectoGrado": "%s",
                  "fechaInicio": "2026-10-01",
                  "fechaFin": "2026-12-01"
                }
                """.formatted(proyectoGrado);
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtCoordinador(UUID coordinador) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(coordinador.toString()))
                .authorities(new SimpleGrantedAuthority(MapasRutaAuthorities.MAPA_RUTA_CREATE));
    }

    @Test
    void debeRetornar201ConId_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act
        mockMvc.perform(post(RUTA)
                        .with(jwtCoordinador(coordinador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(proyectoGrado)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
        var captor = ArgumentCaptor.forClass(AgregarMapaRutaCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().coordinador()).isEqualTo(coordinador);
        assertThat(captor.getValue().fechaInicio()).isEqualTo(LocalDate.of(2026, 10, 1));
        assertThat(captor.getValue().fechaFin()).isEqualTo(LocalDate.of(2026, 12, 1));
    }

    @Test
    void debeRetornar400ConFieldErrors_cuandoLaPeticionEsInvalida() throws Exception {
        // Arrange
        var body = """
                {
                  "proyectoGrado": "no-uuid",
                  "fechaInicio": "2026-02-30",
                  "fechaFin": " "
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtCoordinador(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(3))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='" + MapasRutaFields.MapaRuta.FECHA_INICIO + "')]")
                        .exists());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoElInteractorLanzaUnaReglaDeNegocio() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenThrow(new MapaRutaDuplicadoException(proyectoGrado));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtCoordinador(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(proyectoGrado)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(MapasRutaCodes.MapaRuta.MAPA_RUTA_DUPLICADO));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()))
                                .authorities(new SimpleGrantedAuthority("mapas-ruta:mapa-ruta:view")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyValido(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isForbidden());
    }
}
