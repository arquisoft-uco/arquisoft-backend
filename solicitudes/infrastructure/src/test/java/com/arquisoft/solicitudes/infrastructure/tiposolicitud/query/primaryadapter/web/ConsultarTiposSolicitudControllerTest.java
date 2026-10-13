package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.ConsultarTiposSolicitudInteractor;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
import org.junit.jupiter.api.Test;
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

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarTiposSolicitudController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarTiposSolicitudControllerTest.TestSecurityConfig.class})
class ConsultarTiposSolicitudControllerTest {

    private static final String RUTA = "/solicitudes/tipos-solicitud";

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
    private ConsultarTiposSolicitudInteractor consultarTiposSolicitudInteractor;

    @Test
    void debe200_cuandoConsultaExitosa() throws Exception {
        // Arrange
        var tipos = List.of(
                new TipoSolicitudReadModel("NOVEDAD_PARA_EL_COORDINADOR", "Novedad para el Coordinador",
                        "Solicitud para temas que surgen de improvisto y que no están tipados"),
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor")
        );
        when(consultarTiposSolicitudInteractor.ejecutar()).thenReturn(tipos);

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("NOVEDAD_PARA_EL_COORDINADOR"))
                .andExpect(jsonPath("$[0].nombre").value("Novedad para el Coordinador"))
                .andExpect(jsonPath("$[1].id").value("CAMBIO_DE_ASESOR"))
                .andExpect(jsonPath("$[1].nombre").value("Cambio de Asesor"));
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayTipos() throws Exception {
        // Arrange
        when(consultarTiposSolicitudInteractor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornarJsonCorrecto_cuandoListaTieneVariosElementos() throws Exception {
        // Arrange
        var tipos = List.of(
                new TipoSolicitudReadModel("NOVEDAD_PARA_EL_ASESOR", "Novedad para el Asesor",
                        "Solicitud para temas que surgen de improvisto y que no están tipados"),
                new TipoSolicitudReadModel("AMPLIACION_DE_PLAZO", "Ampliación de Plazo",
                        "Solicitud para extender la fecha de entrega del proyecto"),
                new TipoSolicitudReadModel("REGISTRO_Y_MODIFICACION_DE_USUARIOS",
                        "Registro y modificación de Usuarios",
                        "Solicitud para el registro o modificación de usuarios")
        );
        when(consultarTiposSolicitudInteractor.ejecutar()).thenReturn(tipos);

        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("NOVEDAD_PARA_EL_ASESOR"))
                .andExpect(jsonPath("$[1].id").value("AMPLIACION_DE_PLAZO"))
                .andExpect(jsonPath("$[2].id").value("REGISTRO_Y_MODIFICACION_DE_USUARIOS"));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SolicitudesAuthorities.SOLICITUD_CREATE))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeInvocarInteractor_cuandoEndpointEsLlamado() throws Exception {
        // Arrange
        when(consultarTiposSolicitudInteractor.ejecutar()).thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW))))
                .andExpect(status().isOk());

        // Assert
        verify(consultarTiposSolicitudInteractor, times(1)).ejecutar();
    }
}
