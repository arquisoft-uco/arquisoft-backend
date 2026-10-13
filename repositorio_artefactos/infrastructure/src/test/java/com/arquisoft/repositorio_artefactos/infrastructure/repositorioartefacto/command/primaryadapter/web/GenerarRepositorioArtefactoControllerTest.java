package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.interactor.GenerarRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.GenerarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.domain.administrador.exception.AdministradorNoVigenteException;
import com.arquisoft.repositorio_artefactos.infrastructure.security.RepositorioArtefactosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenerarRepositorioArtefactoController.class)
@Import({
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        GenerarRepositorioArtefactoControllerTest.TestSecurityConfig.class
})
class GenerarRepositorioArtefactoControllerTest {

    private static final String RUTA = "/repositorio-artefactos/repositorios-artefactos";
    private static final String BODY_VALIDO = """
            { "nombreRepositorio": "Repo", "descripcion": "Descripcion" }
            """;

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint((request, response, error) ->
                                    response.sendError(401, "Unauthorized"))
                            .accessDeniedHandler((request, response, error) ->
                                    response.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenerarRepositorioArtefactoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(subject))
                .authorities(new SimpleGrantedAuthority(RepositorioArtefactosAuthorities.REPOSITORIO_ARTEFACTO_CREATE));
    }

    @Test
    void debeResponder201ConElId_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso(administrador.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
        var captor = ArgumentCaptor.forClass(GenerarRepositorioArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().administrador()).isEqualTo(administrador);
        assertThat(captor.getValue().nombreRepositorio()).isEqualTo("Repo");
    }

    @Test
    void debeResponder400_cuandoElSubjectNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso("no-es-uuid"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder400ConFieldErrors_cuandoNombreVacioYDescripcionLarga() throws Exception {
        // Arrange
        var body = """
                { "nombreRepositorio": " ", "descripcion": "%s" }
                """.formatted("d".repeat(301));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(2));
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()))
                                .authorities(new SimpleGrantedAuthority("otro:recurso:view")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isForbidden());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder422_cuandoElAdministradorNoEstaVigente() throws Exception {
        // Arrange
        var administrador = UtilUUID.generarNuevoUUID();
        doThrow(new AdministradorNoVigenteException(administrador)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtConPermiso(administrador.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(RepositorioArtefactosCodes.RepositorioArtefacto.ADMINISTRADOR_NO_VIGENTE));
    }
}
