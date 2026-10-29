package com.arquisoft.solicitudes.infrastructure.respuesta.query.primaryadapter.web;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.destinatario.query.readmodel.DestinatarioReadModel;
import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasRegistroModificacionUsuariosEnviadasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarRespuestasRegistroModificacionUsuariosEnviadasController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarRespuestasRegistroModificacionUsuariosEnviadasControllerTest.TestSecurityConfig.class})
class ConsultarRespuestasRegistroModificacionUsuariosEnviadasControllerTest {

    private static final String RUTA = "/solicitudes/registro-modificacion-usuarios/respuestas/enviadas";

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
    private ConsultarRespuestasRegistroModificacionUsuariosEnviadasInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()));
    }

    @Test
    void debeRetornar200ConPaginaVacia_cuandoConsultaSinBody() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtDe(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeSerializarRespuestaResponseDTOConSolicitudEmbebida_yTomarElAdministradorDelJwt() throws Exception {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        var remitente = new RemitenteReadModel(UtilUUID.generarNuevoUUID(), "EST-1", "Ana Estudiante",
                "ana@uco.edu.co");
        var destinatario = new DestinatarioReadModel(UtilUUID.generarNuevoUUID(), "ADMIN-1", "Administrador Uno",
                "admin1@uco.edu.co");
        var solicitud = new SolicitudReadModel(UtilUUID.generarNuevoUUID(), "un registro de usuario",
                Instant.parse("2026-03-01T10:00:00Z"), "REGISTRO_Y_MODIFICACION_DE_USUARIOS",
                "Registro y modificación de usuarios", remitente, destinatario);
        var respuesta = new RespuestaReadModel(UtilUUID.generarNuevoUUID(), "contenido de la respuesta",
                Instant.parse("2026-03-05T09:00:00Z"), "EN_REVISION", "En revisión", solicitud);
        when(interactor.ejecutar(any(ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(respuesta), 0, 10, 1L));

        // Act
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtDe(administrador)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(respuesta.id().toString()))
                .andExpect(jsonPath("$.content[0].contenido").value("contenido de la respuesta"))
                .andExpect(jsonPath("$.content[0].estadoRespuestaId").value("EN_REVISION"))
                .andExpect(jsonPath("$.content[0].estadoRespuestaNombre").value("En revisión"))
                .andExpect(jsonPath("$.content[0].solicitud.mensajeSolicitud").value("un registro de usuario"))
                .andExpect(jsonPath("$.content[0].solicitud.tipoSolicitudNombre")
                        .value("Registro y modificación de usuarios"))
                .andExpect(jsonPath("$.content[0].solicitud.remitente.nombre").value("Ana Estudiante"))
                .andExpect(jsonPath("$.content[0].solicitud.destinatario.identificador").value("ADMIN-1"))
                .andExpect(jsonPath("$.content[0].solicitud.destinatario.nombre").value("Administrador Uno"))
                .andExpect(jsonPath("$.content[0].solicitud.destinatario.email").value("admin1@uco.edu.co"));

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().administradorUsuario()).isEqualTo(administrador);
    }

    @Test
    void debeRetornar400_cuandoElInteractorLanzaFiltroException() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery.class)))
                .thenThrow(new FiltroException("campo de filtro no permitido: solicitudId",
                        "app.consulta.campo-filtro-no-permitido"));

        var body = """
                {
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "solicitudId",
                    "operador": "ES",
                    "valor": "x"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwtDe(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar401_cuandoElSubjectDelJwtNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(builder -> builder.subject("no-es-uuid"))))
                .andExpect(status().isUnauthorized());
    }
}
