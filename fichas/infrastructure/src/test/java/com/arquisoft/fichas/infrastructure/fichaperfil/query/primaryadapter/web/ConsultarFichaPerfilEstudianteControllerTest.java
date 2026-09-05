package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.interactor.ConsultarFichaPerfilEstudianteInteractor;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichaPerfilEstudianteQuery;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarFichaPerfilEstudianteController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarFichaPerfilEstudianteControllerTest.TestSecurityConfig.class})
class ConsultarFichaPerfilEstudianteControllerTest {

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
    private ConsultarFichaPerfilEstudianteInteractor consultarFichaPerfilEstudianteInteractor;

    private static final UUID ESTUDIANTE_ID = UUID.randomUUID();
    private static final UUID FICHA_ID = UUID.randomUUID();

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                .authorities(new SimpleGrantedAuthority(authority));
    }

    private static FichaPerfilEstudianteReadModel readModel() {
        return new FichaPerfilEstudianteReadModel(
                FICHA_ID, "Sistema de gestion",
                new AsesorFichaReadModel(UUID.randomUUID(), "A100", "Asesor Uno", "asesor@uco.edu.co"),
                new EstadoFichaPerfilReadModel("FORMULACION", "Formulacion", Instant.now()),
                List.of());
    }

    @Test
    void debeRetornar200_cuandoFichaEncontrada() throws Exception {
        // Arrange
        when(consultarFichaPerfilEstudianteInteractor.ejecutar(any(ConsultarFichaPerfilEstudianteQuery.class)))
                .thenReturn(Optional.of(readModel()));

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiante", FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idFichaPerfil").value(FICHA_ID.toString()))
                .andExpect(jsonPath("$.titulo").value("Sistema de gestion"))
                .andExpect(jsonPath("$.asesor.nombre").value("Asesor Uno"))
                .andExpect(jsonPath("$.estado.id").value("FORMULACION"));
    }

    @Test
    void debeRetornar404_cuandoUseCaseRetornaVacio() throws Exception {
        // Arrange
        when(consultarFichaPerfilEstudianteInteractor.ejecutar(any(ConsultarFichaPerfilEstudianteQuery.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiante", FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isNotFound());
    }

    @Test
    void debeRetornar401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiante", FICHA_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_sinClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiante", FICHA_ID)
                        .with(jwtConRol("otro:permiso")))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeRetornar400_cuandoFichaPerfilIdNoEsUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiante", "no-es-uuid")
                        .with(jwtConRol(FichasAuthorities.FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeExtraerEstudianteDelSubjectDelJwt() throws Exception {
        // Arrange
        when(consultarFichaPerfilEstudianteInteractor.ejecutar(any(ConsultarFichaPerfilEstudianteQuery.class)))
                .thenReturn(Optional.of(readModel()));

        // Act
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiante", FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.FICHA_PERFIL_ESTUDIANTE_VIEW)))
                .andExpect(status().isOk());

        // Assert
        var captor = ArgumentCaptor.forClass(ConsultarFichaPerfilEstudianteQuery.class);
        verify(consultarFichaPerfilEstudianteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(FICHA_ID);
        assertThat(captor.getValue().estudiante()).isEqualTo(ESTUDIANTE_ID);
    }
}
