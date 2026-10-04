package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor.ConsultarObservacionesItemEstudianteInteractor;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemEstudianteQuery;
import com.arquisoft.fichas.application.observacionitem.query.readmodel.ObservacionItemReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarObservacionesItemEstudianteController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarObservacionesItemEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarObservacionesItemEstudianteControllerTest {

    private static final String RUTA = "/fichas-perfil/observaciones-item/estudiante";
    private static final UUID ESTUDIANTE_ID = UUID.randomUUID();

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
    private ConsultarObservacionesItemEstudianteInteractor consultarObservacionesItemEstudianteInteractor;

    @Test
    void debeRetornar200ConResponseDTO_cuandoConsultaValida() throws Exception {
        // Arrange
        var id = UUID.randomUUID();
        var revisionItem = UUID.randomUUID();
        var readModel = new ObservacionItemReadModel(
                id, revisionItem, "Falta precisar el alcance", "EN_PROGRESO", "En Progreso");
        when(consultarObservacionesItemEstudianteInteractor.ejecutar(
                any(ConsultarObservacionesItemEstudianteQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel), 0, 10, 1L));

        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(conAutoridadYSub(ESTUDIANTE_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].revisionItem").value(revisionItem.toString()))
                .andExpect(jsonPath("$.content[0].observacion").value("Falta precisar el alcance"))
                .andExpect(jsonPath("$.content[0].estadoObservacionRevision").value("EN_PROGRESO"))
                .andExpect(jsonPath("$.content[0].estadoObservacionRevisionNombre").value("En Progreso"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void debeRetornar200ConPaginaVacia_cuandoEstudianteSinObservaciones() throws Exception {
        // Arrange
        when(consultarObservacionesItemEstudianteInteractor.ejecutar(
                any(ConsultarObservacionesItemEstudianteQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(conAutoridadYSub(ESTUDIANTE_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void debeEntregarAlInteractorUnQueryConElSubDelJwtYElCriterioDelBody_cuandoConsultaValida() throws Exception {
        // Arrange
        when(consultarObservacionesItemEstudianteInteractor.ejecutar(
                any(ConsultarObservacionesItemEstudianteQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 1, 5, 0L));

        var body = """
                {
                  "pagina": 1,
                  "tamanio": 5,
                  "ordenamiento": ["estadoObservacionRevision:DESC"]
                }
                """;

        // Act
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(conAutoridadYSub(ESTUDIANTE_ID)))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarObservacionesItemEstudianteQuery.class);
        verify(consultarObservacionesItemEstudianteInteractor).ejecutar(captor.capture());
        var query = captor.getValue();
        assertThat(query.estudiante()).isEqualTo(ESTUDIANTE_ID);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(5);
        assertThat(query.criterio().ordenamiento()).singleElement().satisfies(orden -> {
            assertThat(orden.getCampo()).isEqualTo("estadoObservacionRevision");
            assertThat(orden.getDireccion()).isEqualTo(SortDirection.DESC);
        });
    }

    @Test
    void debeRetornar400_cuandoElArbolDeFiltrosReferenciaUnCampoNoDeclarado() throws Exception {
        // Arrange
        when(consultarObservacionesItemEstudianteInteractor.ejecutar(
                any(ConsultarObservacionesItemEstudianteQuery.class)))
                .thenThrow(new FiltroException(
                        "campo no permitido: tituloProyecto", "fichas.consulta.campo-no-permitido"));

        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "tituloProyecto",
                    "operador": "ES",
                    "valor": "cualquiera"
                  }
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(conAutoridadYSub(ESTUDIANTE_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400_cuandoElSubDelJwtNoEsUuidValido() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject("no-es-un-uuid"))
                                .authorities(new SimpleGrantedAuthority(
                                        FichasAuthorities.OBSERVACION_ITEM_ESTUDIANTE_VIEW))))
                .andExpect(status().isBadRequest());

        verify(consultarObservacionesItemEstudianteInteractor, never())
                .ejecutar(any(ConsultarObservacionesItemEstudianteQuery.class));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSoloTieneElClientRoleDelAsesor() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(
                                        FichasAuthorities.OBSERVACION_ITEM_ASESOR_VIEW))))
                .andExpect(status().isForbidden());

        verify(consultarObservacionesItemEstudianteInteractor, never())
                .ejecutar(any(ConsultarObservacionesItemEstudianteQuery.class));
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor conAutoridadYSub(UUID sub) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(sub.toString()))
                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_ESTUDIANTE_VIEW));
    }
}
