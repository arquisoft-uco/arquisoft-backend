package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.itemfichaperfil.command.model.ModificarItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.ModificarItemFichaPerfilInputPort;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemNoEncontradoException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.message.FichasMessages;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarItemFichaPerfilInputAdapter.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ModificarItemFichaPerfilInputAdapterTest.TestSecurityConfig.class})
class ModificarItemFichaPerfilInputAdapterTest {

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
    private ModificarItemFichaPerfilInputPort modificarItemFichaPerfilInputPort;

    @Test
    void debe204_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String body = "{\"contenido\": \"Contenido modificado válido\"}";

        doNothing().when(modificarItemFichaPerfilInputPort).ejecutar(any(ModificarItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(estudianteId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void debe400_cuandoItemNoExiste() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String body = "{\"contenido\": \"Contenido modificado\"}";

        doThrow(new ItemNoEncontradoException(itemId))
                .when(modificarItemFichaPerfilInputPort).ejecutar(any(ModificarItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(estudianteId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe403_cuandoEstudianteNoEsPropietario() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String body = "{\"contenido\": \"Contenido modificado\"}";

        doThrow(new ItemFichaNoPropiaException(fichaPerfilId))
                .when(modificarItemFichaPerfilInputPort).ejecutar(any(ModificarItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(estudianteId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        String body = "{\"contenido\": \"Contenido modificado\"}";

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String body = "{\"contenido\": \"Contenido modificado\"}";

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(estudianteId.toString()))
                                .authorities(new SimpleGrantedAuthority("fichas:item-ficha-perfil:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoContenidoInvalido() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String body = "{\"contenido\": \"Contenido modificado\"}";

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(
                FichasMessages.ItemFichaPerfil.CAMPO_CONTENIDO,
                FichasMessages.ItemFichaPerfil.CONTENIDO_REQUERIDO,
                "El contenido es obligatorio"
        );

        doThrow(new DomainValidationException(validationResult))
                .when(modificarItemFichaPerfilInputPort).ejecutar(any(ModificarItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(estudianteId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoFichaEnEstadoTerminal() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String body = "{\"contenido\": \"Contenido modificado\"}";

        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(
                FichasMessages.ItemFichaPerfil.CAMPO_ESTADO_FICHA,
                FichasMessages.ItemFichaPerfil.ESTADO_FICHA_NO_MODIFICABLE,
                FichasMessages.ItemFichaPerfil.ESTADO_FICHA_NO_MODIFICABLE_MSG.formatted("Aprobada")
        );

        doThrow(new DomainValidationException(validationResult))
                .when(modificarItemFichaPerfilInputPort).ejecutar(any(ModificarItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/items/{itemId}", itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(estudianteId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }
}
