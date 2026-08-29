package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.ModificarRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.ModificarRevisionItemCommand;
import com.arquisoft.fichas.domain.estadorevision.exception.EstadoRevisionNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradaException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.constant.FichasCodes;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarRevisionItemController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ModificarRevisionItemControllerTest.TestSecurityConfig.class})
class ModificarRevisionItemControllerTest {

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
    private ModificarRevisionItemInteractor modificarRevisionItemInteractor;

    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final UUID ASESOR_ID = UUID.randomUUID();

    private static final String BODY_VALIDO = """
            {
              "estadoRevision": "VISUALIZADA"
            }
            """;

    @Test
    void debe204_cuandoPeticionValida() throws Exception {
        // Arrange
        doNothing().when(modificarRevisionItemInteractor).ejecutar(any(ModificarRevisionItemCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent());
    }

    @Test
    void debe400_cuandoEstadoRevisionEnBlanco() throws Exception {
        // Arrange
        String bodyInvalido = """
                {
                  "estadoRevision": ""
                }
                """;

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("estudiante:perfil:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoRevisionNoEncontrada() throws Exception {
        // Arrange
        doThrow(new RevisionItemNoEncontradaException(ITEM_ID))
                .when(modificarRevisionItemInteractor).ejecutar(any(ModificarRevisionItemCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.NO_ENCONTRADA));
    }

    @Test
    void debe422_cuandoFichaNoPerteneceAsesor() throws Exception {
        // Arrange
        doThrow(new FichaNoPerteneceAsesorException(UUID.randomUUID(), ASESOR_ID))
                .when(modificarRevisionItemInteractor).ejecutar(any(ModificarRevisionItemCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.FichaPerfil.FICHA_NO_PERTENECE_ASESOR));
    }

    @Test
    void debe422_cuandoEstadoRevisionNoEncontrado() throws Exception {
        // Arrange
        doThrow(new EstadoRevisionNoEncontradoException("ESTADO_INVALIDO"))
                .when(modificarRevisionItemInteractor).ejecutar(any(ModificarRevisionItemCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.ESTADO_REVISION_NO_ENCONTRADO));
    }
}
