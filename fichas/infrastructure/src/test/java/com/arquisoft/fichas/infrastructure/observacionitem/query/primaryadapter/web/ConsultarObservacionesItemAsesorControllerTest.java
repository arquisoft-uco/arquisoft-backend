package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.query.primaryport.interactor.ConsultarObservacionesItemAsesorInteractor;
import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarObservacionesItemAsesorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarObservacionesItemAsesorControllerTest.TestSecurityConfig.class})
class ConsultarObservacionesItemAsesorControllerTest {

    private static final String RUTA = "/fichas-perfil/observaciones-item/asesor";
    private static final UUID ASESOR_ID = UUID.randomUUID();

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
    private ConsultarObservacionesItemAsesorInteractor consultarObservacionesItemAsesorInteractor;

    @Test
    void debeRetornar200ConResponseDTO_cuandoConsultaValida() throws Exception {
        // Arrange
        var id = UUID.randomUUID();
        var revisionItem = UUID.randomUUID();
        var readModel = new ObservacionItemReadModel(
                id, revisionItem, "Falta precisar el alcance", "EN_PROGRESO", "En Progreso");
        when(consultarObservacionesItemAsesorInteractor.ejecutar(any(ConsultarObservacionesItemAsesorQuery.class)))
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
                        .with(conAutoridadYSub(ASESOR_ID)))
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
    void debeEntregarAlInteractorUnQueryConElSubDelJwtYElCriterioDelBody_cuandoConsultaValida() throws Exception {
        // Arrange
        when(consultarObservacionesItemAsesorInteractor.ejecutar(any(ConsultarObservacionesItemAsesorQuery.class)))
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
                        .with(conAutoridadYSub(ASESOR_ID)))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarObservacionesItemAsesorQuery.class);
        verify(consultarObservacionesItemAsesorInteractor).ejecutar(captor.capture());
        var query = captor.getValue();
        assertThat(query.asesorFicha()).isEqualTo(ASESOR_ID);
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
        when(consultarObservacionesItemAsesorInteractor.ejecutar(any(ConsultarObservacionesItemAsesorQuery.class)))
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
                        .with(conAutoridadYSub(ASESOR_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSinClientRoleObservacionItemAsesorView() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE))))
                .andExpect(status().isForbidden());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor conAutoridadYSub(UUID sub) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(sub.toString()))
                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_ASESOR_VIEW));
    }
}
