package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.SubirVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.SubirVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.VersionRepositorioArtefactoSubidaResult;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaPendienteNoEncontradaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.DocumentoNoExisteException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.DocumentoYaExisteException;
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

@WebMvcTest(SubirVersionRepositorioArtefactoController.class)
@Import({
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        SubirVersionRepositorioArtefactoControllerTest.TestSecurityConfig.class
})
class SubirVersionRepositorioArtefactoControllerTest {

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
    private SubirVersionRepositorioArtefactoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    private static String ruta(Object repositorio) {
        return "/repositorio-artefactos/" + repositorio + "/versiones";
    }

    private static String body(Object carga, String nombreDocumento, String nuevoDocumento) {
        return """
                { "carga": "%s", "nombreDocumento": "%s", "nuevoDocumento": %s }
                """.formatted(carga, nombreDocumento, nuevoDocumento);
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso(String subject) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(subject))
                .authorities(new SimpleGrantedAuthority(
                        RepositorioArtefactosAuthorities.VERSION_REPOSITORIO_ARTEFACTO_CREATE));
    }

    @Test
    void debeResponder201ConIdYNumeroVersion_cuandoLaPeticionEsValida() throws Exception {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var carga = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(new VersionRepositorioArtefactoSubidaResult(id, 3));

        // Act & Assert
        mockMvc.perform(post(ruta(repositorio))
                        .with(jwtConPermiso(solicitante.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(carga, "Formato A", "false")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.numeroVersion").value(3));
        var captor = ArgumentCaptor.forClass(SubirVersionRepositorioArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().carga()).isEqualTo(carga);
        assertThat(captor.getValue().repositorio()).isEqualTo(repositorio);
        assertThat(captor.getValue().solicitante()).isEqualTo(solicitante);
        assertThat(captor.getValue().nuevoDocumento()).isFalse();
    }

    @Test
    void debeResponder400_cuandoNuevoDocumentoNulo() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "carga": "%s", "nombreDocumento": "Formato A" }
                                """.formatted(UtilUUID.generarNuevoUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(1));
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder400_cuandoCargaNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("no-es-uuid", "Formato A", "true")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UtilUUID.generarNuevoUUID(), "Formato A", "true")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoSinRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString()))
                                .authorities(new SimpleGrantedAuthority(
                                        RepositorioArtefactosAuthorities.CARGA_VERSION_REPOSITORIO_ARTEFACTO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UtilUUID.generarNuevoUUID(), "Formato A", "true")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder422_cuandoLaCargaNoExiste() throws Exception {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        doThrow(new CargaPendienteNoEncontradaException(carga)).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(carga, "Formato A", "true")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(RepositorioArtefactosCodes.VersionRepositorioArtefacto.CARGA_VERSION_NO_ENCONTRADA));
    }

    @Test
    void debeResponder422_cuandoDocumentoYaExiste() throws Exception {
        // Arrange
        doThrow(new DocumentoYaExisteException("Formato A")).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UtilUUID.generarNuevoUUID(), "Formato A", "true")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(RepositorioArtefactosCodes.VersionRepositorioArtefacto.DOCUMENTO_YA_EXISTE));
    }

    @Test
    void debeResponder422_cuandoDocumentoNoExiste() throws Exception {
        // Arrange
        doThrow(new DocumentoNoExisteException("Formato A")).when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post(ruta(UtilUUID.generarNuevoUUID()))
                        .with(jwtConPermiso(UtilUUID.generarNuevoUUID().toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(UtilUUID.generarNuevoUUID(), "Formato A", "false")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(RepositorioArtefactosCodes.VersionRepositorioArtefacto.DOCUMENTO_NO_EXISTE));
    }
}
