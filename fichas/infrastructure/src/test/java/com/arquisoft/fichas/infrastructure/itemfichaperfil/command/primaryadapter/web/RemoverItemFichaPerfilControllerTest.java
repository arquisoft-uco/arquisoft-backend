package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.primaryadapter.web;

import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.web.config.CatalogoMensajesConfig;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.model.RemoverItemFichaPerfilCommand;
import com.arquisoft.fichas.application.itemfichaperfil.command.primaryport.interactor.RemoverItemFichaPerfilInteractor;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.infrastructure.FichasInfrastructureTestApplication;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.validation.DomainValidationException;
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

@WebMvcTest(controllers = RemoverItemFichaPerfilController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, CatalogoMensajesConfig.class,
        RemoverItemFichaPerfilControllerTest.TestSecurityConfig.class,
        FichasInfrastructureTestApplication.class
})
class RemoverItemFichaPerfilControllerTest {

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
    void debe422_cuandoItemNoExiste() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        doThrow(new ItemFichaPerfilNoEncontradoException(itemId))
                .when(removerItemFichaPerfilInteractor).ejecutar(any(RemoverItemFichaPerfilCommand.class));

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/items/{itemId}", itemId)
                        .with(jwt()
                                .jwt(jwt -> jwt.subject(UUID.randomUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_DELETE))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.ItemFichaPerfil.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debe422_cuandoEstudianteNoEsPropietario() throws Exception {
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
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.FichaPerfil.FICHA_NO_PROPIETARIO));
    }

    @Test
    void debe422_cuandoItemTieneRevisiones() throws Exception {
        // Arrange
        UUID itemId = UUID.randomUUID();
        ValidationResult result = new ValidationResult();
        result.agregarError(
                FichasFields.ItemFichaPerfil.REVISIONES,
                FichasCodes.ItemFichaPerfil.ITEM_CON_REVISIONES,
                Mensajes.formatear(ItemFichaPerfilKey.ERROR_CON_REVISIONES, itemId)
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
                        .value(FichasFields.ItemFichaPerfil.REVISIONES));
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
