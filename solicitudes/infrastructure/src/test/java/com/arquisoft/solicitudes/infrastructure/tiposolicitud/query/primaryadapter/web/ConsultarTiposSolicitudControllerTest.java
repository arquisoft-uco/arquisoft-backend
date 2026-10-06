package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.ConsultarTiposSolicitudInteractor;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
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

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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

    private static SimpleGrantedAuthority auth(String nombre) {
        return new SimpleGrantedAuthority(nombre);
    }

    private ConsultarTiposSolicitudQuery queryInvocada() {
        var captor = ArgumentCaptor.forClass(ConsultarTiposSolicitudQuery.class);
        verify(consultarTiposSolicitudInteractor, times(1)).ejecutar(captor.capture());
        return captor.getValue();
    }

    @Test
    void debe200ConLosTiposDeEstudiante_cuandoTieneLosRolesDeEnvio() throws Exception {
        // Arrange
        var tipos = List.of(
                new TipoSolicitudReadModel("NOVEDAD_PARA_EL_COORDINADOR", "Novedad para el Coordinador",
                        "Solicitud para temas que surgen de improvisto y que no están tipados"),
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor"));
        when(consultarTiposSolicitudInteractor.ejecutar(any(ConsultarTiposSolicitudQuery.class))).thenReturn(tipos);

        // Act
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(
                                auth(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW),
                                auth(SolicitudesAuthorities.SOLICITUD_CREATE),
                                auth(SolicitudesAuthorities.SOLICITUD_NOVEDAD_ASESOR_CREATE),
                                auth(SolicitudesAuthorities.SOLICITUD_CAMBIO_ASESOR_CREATE),
                                auth(SolicitudesAuthorities.SOLICITUD_AMPLIACION_PLAZO_CREATE))))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("NOVEDAD_PARA_EL_COORDINADOR"))
                .andExpect(jsonPath("$[0].nombre").value("Novedad para el Coordinador"))
                .andExpect(jsonPath("$[0].descripcion").exists())
                .andExpect(jsonPath("$[1].id").value("CAMBIO_DE_ASESOR"));
        assertThat(queryInvocada().tipos()).containsExactlyInAnyOrder(
                "NOVEDAD_PARA_EL_COORDINADOR", "NOVEDAD_PARA_EL_ASESOR",
                "CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO");
    }

    @Test
    void debe200ConSoloRegistroDeUsuarios_cuandoEsCoordinador() throws Exception {
        // Arrange
        when(consultarTiposSolicitudInteractor.ejecutar(any(ConsultarTiposSolicitudQuery.class))).thenReturn(List.of(
                new TipoSolicitudReadModel("REGISTRO_Y_MODIFICACION_DE_USUARIOS",
                        "Registro y modificación de Usuarios",
                        "Solicitud para el registro o modificación de usuarios")));

        // Act
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(
                                auth(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW),
                                auth(SolicitudesAuthorities.SOLICITUD_REGISTRO_MODIFICACION_USUARIOS_CREATE))))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("REGISTRO_Y_MODIFICACION_DE_USUARIOS"));
        assertThat(queryInvocada().tipos()).containsExactly("REGISTRO_Y_MODIFICACION_DE_USUARIOS");
    }

    @Test
    void debe200ConListaVacia_cuandoNoTieneRolesDeEnvio() throws Exception {
        // Arrange
        when(consultarTiposSolicitudInteractor.ejecutar(any(ConsultarTiposSolicitudQuery.class)))
                .thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(auth(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW))))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        assertThat(queryInvocada().tipos()).isEqualTo(Set.of());
    }

    @Test
    void debePasarLosCincoTipos_cuandoCombinaRolesDeEstudianteYCoordinador() throws Exception {
        // Arrange
        when(consultarTiposSolicitudInteractor.ejecutar(any(ConsultarTiposSolicitudQuery.class)))
                .thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(
                                auth(SolicitudesAuthorities.TIPO_SOLICITUD_VIEW),
                                auth(SolicitudesAuthorities.SOLICITUD_CREATE),
                                auth(SolicitudesAuthorities.SOLICITUD_NOVEDAD_ASESOR_CREATE),
                                auth(SolicitudesAuthorities.SOLICITUD_CAMBIO_ASESOR_CREATE),
                                auth(SolicitudesAuthorities.SOLICITUD_AMPLIACION_PLAZO_CREATE),
                                auth(SolicitudesAuthorities.SOLICITUD_REGISTRO_MODIFICACION_USUARIOS_CREATE))))
                // Assert
                .andExpect(status().isOk());
        assertThat(queryInvocada().tipos()).containsExactlyInAnyOrder(
                "NOVEDAD_PARA_EL_COORDINADOR", "NOVEDAD_PARA_EL_ASESOR",
                "CAMBIO_DE_ASESOR", "AMPLIACION_DE_PLAZO",
                "REGISTRO_Y_MODIFICACION_DE_USUARIOS");
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
        verify(consultarTiposSolicitudInteractor, never()).ejecutar(any(ConsultarTiposSolicitudQuery.class));
    }

    @Test
    void debe403_cuandoTieneRolDeEnvioPeroNoElDeConsulta() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(auth(SolicitudesAuthorities.SOLICITUD_CREATE))))
                .andExpect(status().isForbidden());
        verify(consultarTiposSolicitudInteractor, never()).ejecutar(any(ConsultarTiposSolicitudQuery.class));
    }
}
