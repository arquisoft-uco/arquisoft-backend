package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web;

import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.interactor.ConsultarVersionArtefactoInteractor;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.ConsultarVersionArtefactoQuery;
import com.arquisoft.artefactos.application.versionartefacto.query.primaryport.model.RolConsultaVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.VersionArtefactoReadModel;
import com.arquisoft.artefactos.domain.versionartefacto.exception.ActorSinAccesoVersionArtefactoException;
import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

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

@WebMvcTest(ConsultarVersionArtefactoController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarVersionArtefactoControllerTest.TestSecurityConfig.class})
class ConsultarVersionArtefactoControllerTest {

    private static final String RUTA = "/artefactos/versiones/{versionArtefactoId}";

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
    private ConsultarVersionArtefactoInteractor consultarVersionArtefactoInteractor;

    private static final UUID ACTOR_ID = UtilUUID.generarNuevoUUID();

    private static RequestPostProcessor token(String... roles) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(ACTOR_ID.toString()).claim("roles", List.of(roles)));
    }

    @Test
    void debeResponder200_yConsultarConActorYRolesDelToken() throws Exception {
        // Arrange
        var versionId = UtilUUID.generarNuevoUUID();
        var version = new VersionArtefactoReadModel(
                versionId, UtilUUID.generarNuevoUUID(), 2, Instant.parse("2026-10-20T10:00:00Z"),
                "artefactos/v2.pdf", "Proyecto Uno", "Documento Uno", 1, "repo-uno", "Asesor Uno",
                UtilUUID.generarNuevoUUID(), "PENDIENTE", "Pendiente");
        when(consultarVersionArtefactoInteractor.ejecutar(any(ConsultarVersionArtefactoQuery.class)))
                .thenReturn(version);

        // Act & Assert
        mockMvc.perform(get(RUTA, versionId).with(token(UsuariosRoles.ESTUDIANTE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(versionId.toString()))
                .andExpect(jsonPath("$.version").value(2))
                .andExpect(jsonPath("$.uriArchivo").value("artefactos/v2.pdf"))
                .andExpect(jsonPath("$.nombreProyecto").value("Proyecto Uno"))
                .andExpect(jsonPath("$.estadoRevision").value("PENDIENTE"));

        var captor = ArgumentCaptor.forClass(ConsultarVersionArtefactoQuery.class);
        verify(consultarVersionArtefactoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().versionArtefacto()).isEqualTo(versionId);
        assertThat(captor.getValue().actor()).isEqualTo(ACTOR_ID);
        assertThat(captor.getValue().roles()).containsExactly(RolConsultaVersionArtefacto.ESTUDIANTE);
    }

    @Test
    void debeResponder400_cuandoIdInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid").with(token(UsuariosRoles.ASESOR)))
                .andExpect(status().isBadRequest());
        verify(consultarVersionArtefactoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder400_cuandoSinRolesDeNegocio() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()).with(token("coordinador")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[?(@.field == '%s')]", ArtefactosFields.VersionArtefacto.ROLES)
                        .exists());
        verify(consultarVersionArtefactoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder422_cuandoNoExiste() throws Exception {
        // Arrange
        var versionId = UtilUUID.generarNuevoUUID();
        when(consultarVersionArtefactoInteractor.ejecutar(any(ConsultarVersionArtefactoQuery.class)))
                .thenThrow(new VersionArtefactoNoEncontradaException(versionId));

        // Act & Assert
        mockMvc.perform(get(RUTA, versionId).with(token(UsuariosRoles.ASESOR)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.VersionArtefacto.NO_ENCONTRADA));
    }

    @Test
    void debeResponder422_cuandoAccesoDenegado() throws Exception {
        // Arrange
        var versionId = UtilUUID.generarNuevoUUID();
        when(consultarVersionArtefactoInteractor.ejecutar(any(ConsultarVersionArtefactoQuery.class)))
                .thenThrow(new ActorSinAccesoVersionArtefactoException(ACTOR_ID, versionId));

        // Act & Assert
        mockMvc.perform(get(RUTA, versionId).with(token(UsuariosRoles.ESTUDIANTE)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ArtefactosCodes.VersionArtefacto.ACCESO_DENEGADO));
    }
}
