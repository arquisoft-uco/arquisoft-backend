package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.interactor.ConsultarEstadosFichaPerfilCoordinadorInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosFichaPerfilCoordinadorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosFichaPerfilCoordinadorControllerTest.TestSecurityConfig.class})
class ConsultarEstadosFichaPerfilCoordinadorControllerTest {

    private static final String RUTA = "/fichas-perfil/{fichaPerfilId}/estados-ficha/coordinador";
    private static final UUID FICHA_ID = UtilUUID.generarNuevoUUID();

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
    private ConsultarEstadosFichaPerfilCoordinadorInteractor consultarEstadosFichaPerfilCoordinadorInteractor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeResponder200ConLista_cuandoTieneElRol() throws Exception {
        // Arrange
        when(consultarEstadosFichaPerfilCoordinadorInteractor.ejecutar(any(ConsultarEstadosFichaPerfilCoordinadorQuery.class)))
                .thenReturn(List.of(
                        new EstadoFichaPerfilReadModel("EN_CONSTRUCCION", "En Construccion",
                                Instant.parse("2026-09-07T14:03:00Z")),
                        new EstadoFichaPerfilReadModel("DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion",
                                Instant.parse("2026-09-10T09:30:00Z"))));

        // Act
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("EN_CONSTRUCCION"))
                .andExpect(jsonPath("$[0].nombre").value("En Construccion"))
                .andExpect(jsonPath("$[0].fechaActualizacion").value("2026-09-07T14:03:00Z"))
                .andExpect(jsonPath("$[1].id").value("DISPONIBLE_PARA_EVALUACION"))
                .andExpect(jsonPath("$[1].fechaActualizacion").value("2026-09-10T09:30:00Z"));

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarEstadosFichaPerfilCoordinadorQuery.class);
        verify(consultarEstadosFichaPerfilCoordinadorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(FICHA_ID);
    }

    @Test
    void debeResponder200Vacia_cuandoNoHayEstados() throws Exception {
        // Arrange
        when(consultarEstadosFichaPerfilCoordinadorInteractor.ejecutar(any(ConsultarEstadosFichaPerfilCoordinadorQuery.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeResponder400_cuandoFichaNoEsUuid() throws Exception {
        // Act
        mockMvc.perform(get(RUTA, "no-es-uuid")
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isBadRequest());

        // Assert
        verify(consultarEstadosFichaPerfilCoordinadorInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoNoTieneElRol() throws Exception {
        // Act
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isForbidden());

        // Assert
        verify(consultarEstadosFichaPerfilCoordinadorInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder403_cuandoTieneSoloElRolDelRepresentante() throws Exception {
        // Act
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ESTADO_FICHA_PERFIL_REPRESENTANTE_VIEW)))
                .andExpect(status().isForbidden());

        // Assert
        verify(consultarEstadosFichaPerfilCoordinadorInteractor, never()).ejecutar(any());
    }
}
