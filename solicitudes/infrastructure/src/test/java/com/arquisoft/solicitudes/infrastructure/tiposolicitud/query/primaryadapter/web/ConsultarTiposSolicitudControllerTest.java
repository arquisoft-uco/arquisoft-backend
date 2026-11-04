package com.arquisoft.solicitudes.infrastructure.tiposolicitud.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.interactor.ConsultarTiposSolicitudInteractor;
import com.arquisoft.solicitudes.application.tiposolicitud.query.primaryport.model.ConsultarTiposSolicitudQuery;
import com.arquisoft.solicitudes.application.tiposolicitud.query.readmodel.TipoSolicitudReadModel;
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

import java.util.List;
import java.util.UUID;

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
@Import({AppLoggerConfig.class, IdentidadActualTestConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarTiposSolicitudControllerTest.TestSecurityConfig.class})
class ConsultarTiposSolicitudControllerTest {

    private static final String RUTA = "/solicitudes/tipos-solicitud";
    private static final UUID ACTOR = UUID.randomUUID();

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
    private ConsultarTiposSolicitudInteractor consultarTiposSolicitudInteractor;

    private ConsultarTiposSolicitudQuery queryInvocada() {
        var captor = ArgumentCaptor.forClass(ConsultarTiposSolicitudQuery.class);
        verify(consultarTiposSolicitudInteractor, times(1)).ejecutar(captor.capture());
        return captor.getValue();
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor conRoles(String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(ACTOR.toString()).claim("roles", List.of(roles)));
    }

    @Test
    void debe200ConLosTiposDeEstudiante_cuandoElLlamanteEsEstudiante() throws Exception {
        // Arrange
        var tipos = List.of(
                new TipoSolicitudReadModel("NOVEDAD_PARA_EL_COORDINADOR", "Novedad para el Coordinador",
                        "Solicitud para temas que surgen de improvisto y que no están tipados"),
                new TipoSolicitudReadModel("CAMBIO_DE_ASESOR", "Cambio de Asesor",
                        "Solicitud para modificar el asesor"));
        when(consultarTiposSolicitudInteractor.ejecutar(any(ConsultarTiposSolicitudQuery.class))).thenReturn(tipos);

        // Act
        mockMvc.perform(get(RUTA).with(conRoles(UsuariosRoles.ESTUDIANTE)))
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
        mockMvc.perform(get(RUTA).with(conRoles(UsuariosRoles.COORDINADOR)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("REGISTRO_Y_MODIFICACION_DE_USUARIOS"));
        assertThat(queryInvocada().tipos()).containsExactly("REGISTRO_Y_MODIFICACION_DE_USUARIOS");
    }

    @Test
    void debe200ConListaVacia_cuandoElRolNoEnviaSolicitudes() throws Exception {
        // Arrange
        when(consultarTiposSolicitudInteractor.ejecutar(any(ConsultarTiposSolicitudQuery.class)))
                .thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA).with(conRoles(UsuariosRoles.JURADO)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        assertThat(queryInvocada().tipos()).isEmpty();
    }

    @Test
    void debePasarLosCincoTipos_cuandoCombinaRolesDeEstudianteYCoordinador() throws Exception {
        // Arrange
        when(consultarTiposSolicitudInteractor.ejecutar(any(ConsultarTiposSolicitudQuery.class)))
                .thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA).with(conRoles(UsuariosRoles.ESTUDIANTE, UsuariosRoles.COORDINADOR)))
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
}
