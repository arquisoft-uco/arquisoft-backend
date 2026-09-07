package com.arquisoft.solicitudes.infrastructure.solicitud.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.solicitudes.application.destinatario.query.readmodel.DestinatarioReadModel;
import com.arquisoft.solicitudes.application.remitente.query.readmodel.RemitenteReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesNovedadCoordinadorEnviadasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.infrastructure.security.SolicitudesAuthorities;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarSolicitudesNovedadCoordinadorEnviadasController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarSolicitudesNovedadCoordinadorEnviadasControllerTest.TestSecurityConfig.class})
class ConsultarSolicitudesNovedadCoordinadorEnviadasControllerTest {

    private static final String RUTA = "/solicitudes/novedad-coordinador/enviadas";

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
    private ConsultarSolicitudesNovedadCoordinadorEnviadasInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtDe(
            UUID subject, String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(subject.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeRetornar200ConPaginaVacia_cuandoConsultaSinBody() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultarSolicitudesNovedadCoordinadorEnviadasQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtDe(UUID.randomUUID(),
                                SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_ENVIADA_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeSerializarSolicitudResponseDTOConDestinatarioEmbebido_yTomarElEstudianteDelJwt() throws Exception {
        // Arrange
        UUID estudiante = UUID.randomUUID();
        var remitente = new RemitenteReadModel(UUID.randomUUID(), "EST-1", "Ana Estudiante",
                "ana@uco.edu.co");
        var destinatario = new DestinatarioReadModel(UUID.randomUUID(), "COORD-1", "Coordinadora Uno",
                "coord1@uco.edu.co");
        var solicitud = new SolicitudReadModel(UUID.randomUUID(), "una novedad",
                LocalDateTime.of(2026, 3, 1, 10, 0), "NOVEDAD_PARA_EL_COORDINADOR",
                "Novedad para el Coordinador", remitente, destinatario);
        when(interactor.ejecutar(any(ConsultarSolicitudesNovedadCoordinadorEnviadasQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(solicitud), 0, 10, 1L));

        // Act
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtDe(estudiante,
                                SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_ENVIADA_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(solicitud.id().toString()))
                .andExpect(jsonPath("$.content[0].mensajeSolicitud").value("una novedad"))
                .andExpect(jsonPath("$.content[0].tipoSolicitudNombre").value("Novedad para el Coordinador"))
                .andExpect(jsonPath("$.content[0].remitente.nombre").value("Ana Estudiante"))
                .andExpect(jsonPath("$.content[0].destinatario.identificador").value("COORD-1"))
                .andExpect(jsonPath("$.content[0].destinatario.nombre").value("Coordinadora Uno"))
                .andExpect(jsonPath("$.content[0].destinatario.email").value("coord1@uco.edu.co"));

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarSolicitudesNovedadCoordinadorEnviadasQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().estudianteUsuario()).isEqualTo(estudiante);
    }

    @Test
    void debeRetornar400_cuandoElInteractorLanzaFiltroException() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultarSolicitudesNovedadCoordinadorEnviadasQuery.class)))
                .thenThrow(new FiltroException("campo de filtro no permitido: mensajeSolicitud",
                        "app.consulta.campo-filtro-no-permitido"));

        String body = """
                {
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "mensajeSolicitud",
                    "operador": "CONTIENE",
                    "valor": "x"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwtDe(UUID.randomUUID(),
                                SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_ENVIADA_VIEW)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoElTokenNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwtDe(UUID.randomUUID(),
                                SolicitudesAuthorities.SOLICITUD_NOVEDAD_COORDINADOR_RECIBIDA_VIEW)))
                .andExpect(status().isForbidden());
    }
}
