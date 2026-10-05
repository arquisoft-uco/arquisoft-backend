package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.interactor.ConsultarMapasRutaCoordinadorInteractor;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapasRutaCoordinadorQuery;
import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.infrastructure.security.MapasRutaAuthorities;
import com.arquisoft.shared.message.constant.MapasRutaFields;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarMapasRutaCoordinadorController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarMapasRutaCoordinadorControllerTest.TestSecurityConfig.class})
class ConsultarMapasRutaCoordinadorControllerTest {

    private static final String RUTA = "/mapas-ruta/coordinador";

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
    private ConsultarMapasRutaCoordinadorInteractor interactor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtCoordinador(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(subject))
                .authorities(new SimpleGrantedAuthority(MapasRutaAuthorities.MAPA_RUTA_COORDINADOR_VIEW));
    }

    @Test
    void debeRetornar200ConLaPaginaYSinCoordinador_cuandoNoHayCuerpo() throws Exception {
        // Arrange
        var idMapa = UtilUUID.generarNuevoUUID();
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var readModel = new MapaRutaReadModel(idMapa, proyectoGrado, "Sistema de gestion",
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 12, 1));
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(readModel), 0, 10, 1L));

        // Act
        var respuesta = mockMvc.perform(post(RUTA).with(jwtCoordinador(UtilUUID.generarNuevoUUID().toString())))
                // Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(idMapa.toString()))
                .andExpect(jsonPath("$.content[0].proyectoGrado").value(proyectoGrado.toString()))
                .andExpect(jsonPath("$.content[0].tituloProyecto").value("Sistema de gestion"))
                .andExpect(jsonPath("$.content[0].fechaInicio").value("2026-10-01"))
                .andExpect(jsonPath("$.content[0].fechaFin").value("2026-12-01"))
                .andReturn();
        assertThat(respuesta.getResponse().getContentAsString()).doesNotContainIgnoringCase("coordinador");
    }

    @Test
    void debePasarElSubDelJwtComoCoordinador_cuandoSeConsulta() throws Exception {
        // Arrange
        var coordinador = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        mockMvc.perform(post(RUTA).with(jwtCoordinador(coordinador.toString())))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarMapasRutaCoordinadorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().coordinador()).isEqualTo(coordinador);
    }

    @Test
    void debeLlevarLosFiltrosAlInteractor_cuandoElCuerpoLosTrae() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 0, 5, 0L));
        var cuerpo = """
                {
                  "pagina": 0,
                  "tamanio": 5,
                  "ordenamiento": ["fechaFin:DESC"],
                  "filtros": {
                    "tipo": "PREDICADO",
                    "campo": "fechaInicio",
                    "operador": "MAYOR_IGUAL_QUE",
                    "valor": "2026-02-01"
                  }
                }
                """;

        // Act
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo)
                        .with(jwtCoordinador(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarMapasRutaCoordinadorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        var criterio = captor.getValue().criterio();
        assertThat(criterio.tamanio()).isEqualTo(5);
        assertThat(criterio.ordenamiento().get(0).getCampo()).isEqualTo("fechaFin");
        assertThat(criterio.raiz()).isEqualTo(
                NodoFiltro.predicado("fechaInicio", FiltroOperador.MAYOR_IGUAL_QUE, "2026-02-01"));
    }

    @Test
    void debeRetornar400_cuandoElInteractorLanzaFiltroInvalido() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new FiltroInvalidoException("fecha invalida"));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtCoordinador(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar400SinInvocarElInteractor_cuandoElSubNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).with(jwtCoordinador("no-es-un-uuid")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(MapasRutaFields.MapaRuta.COORDINADOR));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar403_cuandoSoloTieneElClientRoleDeEstudiante() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(
                                        MapasRutaAuthorities.MAPA_RUTA_ESTUDIANTE_VIEW))))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }
}
