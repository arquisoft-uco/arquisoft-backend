package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.interactor.ConsultarEstudiantesFichaPerfilInteractor;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarEstudiantesFichaPerfilQuery;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstudiantesFichaPerfilController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstudiantesFichaPerfilControllerTest.TestSecurityConfig.class})
class ConsultarEstudiantesFichaPerfilControllerTest {

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
    private ConsultarEstudiantesFichaPerfilInteractor consultarEstudiantesFichaPerfilInteractor;

    private static final UUID FICHA_ID = UUID.randomUUID();

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeRetornar200ConLista_cuandoElCoordinadorConsulta() throws Exception {
        // Arrange
        var vinculoId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        when(consultarEstudiantesFichaPerfilInteractor.ejecutar(any(ConsultarEstudiantesFichaPerfilQuery.class)))
                .thenReturn(List.of(new EstudianteFichaPerfilReadModel(
                        vinculoId, FICHA_ID, estudianteId, "Ana Ruiz", "ana.ruiz@uco.edu.co")));

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiantes", FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(vinculoId.toString()))
                .andExpect(jsonPath("$[0].fichaPerfilId").value(FICHA_ID.toString()))
                .andExpect(jsonPath("$[0].estudianteId").value(estudianteId.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Ana Ruiz"))
                .andExpect(jsonPath("$[0].email").value("ana.ruiz@uco.edu.co"));

        var captor = ArgumentCaptor.forClass(ConsultarEstudiantesFichaPerfilQuery.class);
        verify(consultarEstudiantesFichaPerfilInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(FICHA_ID);
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoNoHayEstudiantes() throws Exception {
        // Arrange
        when(consultarEstudiantesFichaPerfilInteractor.ejecutar(any(ConsultarEstudiantesFichaPerfilQuery.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiantes", FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeRetornar400_cuandoFichaPerfilIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiantes", "no-es-uuid")
                        .with(jwtConRol(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiantes", FICHA_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoFaltaElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/{fichaPerfilId}/estudiantes", FICHA_ID)
                        .with(jwtConRol("otro:permiso")))
                .andExpect(status().isForbidden());
    }
}
