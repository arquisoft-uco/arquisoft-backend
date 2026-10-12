package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichasPerfilEstudianteInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilEstudianteQuery;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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

@WebMvcTest(ConsultarFichasPerfilEstudianteController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarFichasPerfilEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarFichasPerfilEstudianteControllerTest {

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
    private ConsultarFichasPerfilEstudianteInteractor consultarFichasPerfilEstudianteInteractor;

    private static final UUID ESTUDIANTE_ID = UUID.randomUUID();
    private static final UUID FICHA_ID = UUID.randomUUID();
    private static final UUID OTRA_FICHA_ID = UUID.randomUUID();

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    private static FichaPerfilEstudianteReadModel readModel(UUID fichaId) {
        return new FichaPerfilEstudianteReadModel(
                fichaId, "Sistema de gestion",
                new AsesorFichaReadModel(UUID.randomUUID(), "A100", "Asesor Uno", "asesor@uco.edu.co"),
                new EstadoFichaPerfilReadModel("FORMULACION", "Formulacion", Instant.now()),
                List.of());
    }

    @Test
    void debeRetornar200ConArray_cuandoEstudiantePerteneceAVariasFichas() throws Exception {
        // Arrange
        when(consultarFichasPerfilEstudianteInteractor.ejecutar(any(ConsultarFichasPerfilEstudianteQuery.class)))
                .thenReturn(List.of(readModel(FICHA_ID), readModel(OTRA_FICHA_ID)));

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estudiante")
                        .with(jwtConRol(FichasAuthorities.FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].idFichaPerfil").value(FICHA_ID.toString()))
                .andExpect(jsonPath("$[0].titulo").value("Sistema de gestion"))
                .andExpect(jsonPath("$[0].asesor.nombre").value("Asesor Uno"))
                .andExpect(jsonPath("$[0].estado.id").value("FORMULACION"))
                .andExpect(jsonPath("$[1].idFichaPerfil").value(OTRA_FICHA_ID.toString()));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoEstudianteNoPerteneceAFichas() throws Exception {
        // Arrange
        when(consultarFichasPerfilEstudianteInteractor.ejecutar(any(ConsultarFichasPerfilEstudianteQuery.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estudiante")
                        .with(jwtConRol(FichasAuthorities.FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeRetornar401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estudiante"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_sinClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estudiante")
                        .with(jwtConRol("otro:permiso")))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeExtraerEstudianteDelSubjectDelJwt() throws Exception {
        // Arrange
        when(consultarFichasPerfilEstudianteInteractor.ejecutar(any(ConsultarFichasPerfilEstudianteQuery.class)))
                .thenReturn(List.of(readModel(FICHA_ID)));

        // Act
        mockMvc.perform(get("/fichas-perfil/estudiante")
                        .with(jwtConRol(FichasAuthorities.FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarFichasPerfilEstudianteQuery.class);
        verify(consultarFichasPerfilEstudianteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().estudiante()).isEqualTo(ESTUDIANTE_ID);
    }

    @Test
    void debeRetornar400SinConsultar_cuandoSubjectDelJwtNoEsUUID() throws Exception {
        // Arrange
        var jwtSubjectInvalido = SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject("no-es-uuid"))
                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_ESTUDIANTE_VIEW));

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/estudiante").with(jwtSubjectInvalido))
                .andExpect(status().isBadRequest());
        verify(consultarFichasPerfilEstudianteInteractor, never())
                .ejecutar(any(ConsultarFichasPerfilEstudianteQuery.class));
    }
}
