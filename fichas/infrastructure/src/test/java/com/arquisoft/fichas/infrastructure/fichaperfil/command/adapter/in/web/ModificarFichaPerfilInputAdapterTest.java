package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web;

import com.arquisoft.shared.web.config.MessageCatalogConfig;
import com.arquisoft.fichas.application.fichaperfil.command.interactor.ModificarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaNoEncontradaException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarFichaPerfilInputAdapter.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, MessageCatalogConfig.class,
        ModificarFichaPerfilInputAdapterTest.TestSecurityConfig.class})
class ModificarFichaPerfilInputAdapterTest {

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
    private ModificarFichaPerfilInteractor modificarFichaPerfilInteractor;

    private static final UUID FICHA_ID = UUID.randomUUID();
    private static final UUID ESTUDIANTE_ID = UUID.randomUUID();

    private static final String BODY_VALIDO = """
            {
              "tituloProyecto": "Nuevo título"
            }
            """;

    // ── 1. Éxito ──

    @Test
    void debeRetornar204_cuandoFichaModificada() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}", FICHA_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isNoContent());
    }

    // ── 2. Validación Jakarta ──

    @Test
    void debeRetornar400_cuandoTituloVacio() throws Exception {
        // Arrange
        String bodyInvalido = """
                {
                  "tituloProyecto": ""
                }
                """;

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}", FICHA_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest());
    }

    // ── 3. Excepción de autorización: no propietario ──

    @Test
    void debeRetornar403_cuandoNoEsPropietario() throws Exception {
        // Arrange
        doThrow(new FichaNoPropietarioException(FICHA_ID, ESTUDIANTE_ID))
                .when(modificarFichaPerfilInteractor)
                .ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}", FICHA_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }

    // ── 4. Excepción de dominio: ficha no existe ──

    @Test
    void debeRetornar400_cuandoFichaNoExiste() throws Exception {
        // Arrange
        doThrow(new FichaNoEncontradaException(FICHA_ID))
                .when(modificarFichaPerfilInteractor)
                .ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}", FICHA_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isBadRequest());
    }

    // ── 5. Excepción de dominio: título duplicado ──

    @Test
    void debeRetornar400_cuandoTituloDuplicado() throws Exception {
        // Arrange
        doThrow(new FichaTituloDuplicadoException("Título duplicado"))
                .when(modificarFichaPerfilInteractor)
                .ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}", FICHA_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isBadRequest());
    }

    // ── 6. No autenticado ──

    @Test
    void debeRetornar401_cuandoNoAutenticado() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}", FICHA_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    // ── 7. Sin permisos ──

    @Test
    void debeRetornar403_cuandoSinPermisos() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}", FICHA_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ESTUDIANTE_ID.toString()))
                                .authorities(new SimpleGrantedAuthority("otro:permiso")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
    }
}
