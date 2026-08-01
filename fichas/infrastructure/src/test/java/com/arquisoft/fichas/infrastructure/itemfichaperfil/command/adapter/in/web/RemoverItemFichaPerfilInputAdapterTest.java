package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.itemfichaperfil.command.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.RemoverItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.infrastructure.FichasInfrastructureTestApplication;
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
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RemoverItemFichaPerfilInputAdapter.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RemoverItemFichaPerfilInputAdapterTest.TestSecurityConfig.class,
        FichasInfrastructureTestApplication.class
})
class RemoverItemFichaPerfilInputAdapterTest {

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf -> csrf.disable())
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
    private RemoverItemFichaPerfilInteractor removerItemFichaPerfilInteractor;

    @Test
    void debe204_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        doNothing().when(removerItemFichaPerfilInteractor).ejecutar(any(RemoverItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/items/{itemId}", itemId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_DELETE))))
                .andExpect(status().isNoContent());
    }

    @Test
    void debe400_cuandoItemNoExiste() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        doThrow(new ItemFichaPerfilNoEncontradoException(itemId))
                .when(removerItemFichaPerfilInteractor).ejecutar(any(RemoverItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/items/{itemId}", itemId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_DELETE))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(FichasMessages.ItemFichaPerfil.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debe403_cuandoEstudianteNoEsPropietario() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        doThrow(new FichaNoPropietarioException(fichaId, estudianteId))
                .when(removerItemFichaPerfilInteractor).ejecutar(any(RemoverItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/items/{itemId}", itemId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(estudianteId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_DELETE))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value(FichasMessages.FichaPerfil.FICHA_NO_PROPIETARIO));
    }

    @Test
    void debe422_cuandoItemTieneRevisiones() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ValidationResult result = new ValidationResult();
        result.agregarError(
                FichasMessages.ItemFichaPerfil.CAMPO_REVISIONES,
                FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES,
                FichasMessages.ItemFichaPerfil.ITEM_CON_REVISIONES_MSG.formatted(itemId)
        );

        doThrow(new DomainValidationException(result))
                .when(removerItemFichaPerfilInteractor).ejecutar(any(RemoverItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/items/{itemId}", itemId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_DELETE))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(FichasMessages.ItemFichaPerfil.CAMPO_REVISIONES));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/items/{itemId}", itemId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/items/{itemId}", itemId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority("otro:rol:read"))))
                .andExpect(status().isForbidden());
    }
}
