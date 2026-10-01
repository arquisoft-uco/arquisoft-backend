package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.AgregarRevisionItemInteractor;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemYaExisteException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgregarRevisionItemController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AgregarRevisionItemControllerTest.TestSecurityConfig.class})
class AgregarRevisionItemControllerTest {

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
    private AgregarRevisionItemInteractor agregarRevisionItemInteractor;

    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final UUID ASESOR_ID = UUID.randomUUID();

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        // Arrange — sin body: el estado inicial ('NUEVA') se fija en el dominio, no lo envía el cliente
        UUID revisionId = UUID.randomUUID();
        when(agregarRevisionItemInteractor.ejecutar(any())).thenReturn(revisionId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_CREATE))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(revisionId.toString()));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("estudiante:perfil:read"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoItemNoEncontrado() throws Exception {
        // Arrange
        when(agregarRevisionItemInteractor.ejecutar(any()))
                .thenThrow(new ItemFichaPerfilNoEncontradoException(ITEM_ID));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_CREATE))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.ItemFichaPerfil.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debe422_cuandoFichaNoPerteneceAsesor() throws Exception {
        // Arrange
        when(agregarRevisionItemInteractor.ejecutar(any()))
                .thenThrow(new FichaNoPerteneceAsesorException(UUID.randomUUID(), ASESOR_ID));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_CREATE))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.FichaPerfil.FICHA_NO_PERTENECE_ASESOR));
    }

    @Test
    void debe422_cuandoRevisionYaExiste() throws Exception {
        // Arrange
        when(agregarRevisionItemInteractor.ejecutar(any()))
                .thenThrow(new RevisionItemYaExisteException(ITEM_ID));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/items/{itemId}/revisiones", ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_CREATE))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.YA_EXISTE));
    }
}
