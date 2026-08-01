package com.arquisoft.fichas.infrastructure.itemfichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.itemfichaperfil.command.port.in.AgregarItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemTipoDuplicadoException;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgregarItemFichaPerfilInputAdapter.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        AgregarItemFichaPerfilInputAdapterTest.TestSecurityConfig.class})
class AgregarItemFichaPerfilInputAdapterTest {

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
    private AgregarItemFichaPerfilInteractor agregarItemFichaPerfilInteractor;

    private static final UUID ESTUDIANTE_ID = UUID.randomUUID();
    private static final UUID FICHA_PERFIL_ID = UUID.randomUUID();

    private static final String BODY_VALIDO = """
            {
              "tipoItem": "OBJETIVO_GENERAL",
              "contenido": "Este es un objetivo general válido para el proyecto"
            }
            """;

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        UUID itemId = UUID.randomUUID();
        when(agregarItemFichaPerfilInteractor.ejecutar(any())).thenReturn(itemId);

        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/items", FICHA_PERFIL_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemId.toString()));
    }

    @Test
    void debe422_cuandoRequestInvalido() throws Exception {
        String bodyInvalido = """
                {
                  "tipoItem": null,
                  "contenido": ""
                }
                """;

        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/items", FICHA_PERFIL_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/items", FICHA_PERFIL_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/items", FICHA_PERFIL_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("otro:permiso")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe400_cuandoTipoDuplicado() throws Exception {
        when(agregarItemFichaPerfilInteractor.ejecutar(any()))
                .thenThrow(new ItemTipoDuplicadoException("OBJETIVO_GENERAL"));

        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/items", FICHA_PERFIL_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe403_cuandoFichaNoPropia() throws Exception {
        when(agregarItemFichaPerfilInteractor.ejecutar(any()))
                .thenThrow(new ItemFichaNoPropiaException(FICHA_PERFIL_ID));

        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/items", FICHA_PERFIL_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoTipoItemInvalido() throws Exception {
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError(
                FichasMessages.ItemFichaPerfil.CAMPO_TIPO_ITEM,
                FichasMessages.ItemFichaPerfil.TIPO_ITEM_INVALIDO,
                FichasMessages.ItemFichaPerfil.TIPO_ITEM_INVALIDO_MSG.formatted("TIPO_INEXISTENTE")
        );

        when(agregarItemFichaPerfilInteractor.ejecutar(any()))
                .thenThrow(new DomainValidationException(validationResult));

        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/items", FICHA_PERFIL_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ITEM_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(FichasMessages.ItemFichaPerfil.CAMPO_TIPO_ITEM));
    }
}
