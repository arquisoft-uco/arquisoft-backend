package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.AgregarObservacionItemInteractor;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemDuplicadaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgregarObservacionItemController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AgregarObservacionItemControllerTest.TestSecurityConfig.class})
class AgregarObservacionItemControllerTest {

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
    private AgregarObservacionItemInteractor agregarObservacionItemInteractor;

    private static final UUID REVISION_ITEM_ID = UUID.randomUUID();
    private static final UUID ASESOR_ID = UUID.randomUUID();

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID observacionId = UUID.randomUUID();
        when(agregarObservacionItemInteractor.ejecutar(any())).thenReturn(observacionId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones/{revisionItemId}/observaciones", REVISION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "El objetivo general no cumple el formato"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(observacionId.toString()));
    }

    @Test
    void debe400_cuandoObservacionEnBlanco() throws Exception {
        // Act & Assert — Command.crear() rechaza antes de llegar al dominio: 400, no 422
        mockMvc.perform(post("/fichas-perfil/revisiones/{revisionItemId}/observaciones", REVISION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "   "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones/{revisionItemId}/observaciones", REVISION_ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "Observación válida"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones/{revisionItemId}/observaciones", REVISION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("estudiante:perfil:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "Observación válida"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoRevisionNoEncontrada() throws Exception {
        // Arrange
        when(agregarObservacionItemInteractor.ejecutar(any()))
                .thenThrow(new RevisionItemNoEncontradoException(REVISION_ITEM_ID));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones/{revisionItemId}/observaciones", REVISION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "Observación válida"}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.NO_ENCONTRADA));
    }

    @Test
    void debe422_cuandoRevisionCerrada() throws Exception {
        // Arrange
        when(agregarObservacionItemInteractor.ejecutar(any()))
                .thenThrow(new RevisionItemCerradaException(REVISION_ITEM_ID));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones/{revisionItemId}/observaciones", REVISION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "Observación válida"}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.CERRADA));
    }

    @Test
    void debe422_cuandoFichaNoPerteneceAsesor() throws Exception {
        // Arrange
        when(agregarObservacionItemInteractor.ejecutar(any()))
                .thenThrow(new FichaNoPerteneceAsesorException(UUID.randomUUID(), ASESOR_ID));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones/{revisionItemId}/observaciones", REVISION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "Observación válida"}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.FichaPerfil.FICHA_NO_PERTENECE_ASESOR));
    }

    @Test
    void debe422_cuandoObservacionDuplicada() throws Exception {
        // Arrange
        when(agregarObservacionItemInteractor.ejecutar(any()))
                .thenThrow(new ObservacionItemDuplicadaException(REVISION_ITEM_ID, "Observación válida"));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/revisiones/{revisionItemId}/observaciones", REVISION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "Observación válida"}
                                """))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.ObservacionItem.OBSERVACION_ITEM_DUPLICADA));
    }
}
