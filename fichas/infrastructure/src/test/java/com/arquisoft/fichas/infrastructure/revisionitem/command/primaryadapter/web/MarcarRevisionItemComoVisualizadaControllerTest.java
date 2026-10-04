package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.MarcarRevisionItemComoVisualizadaInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.MarcarRevisionItemComoVisualizadaCommand;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarcarRevisionItemComoVisualizadaController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        MarcarRevisionItemComoVisualizadaControllerTest.TestSecurityConfig.class})
class MarcarRevisionItemComoVisualizadaControllerTest {

    private static final String RUTA = "/fichas-perfil/revisiones/{revisionItemId}/visualizada";

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
    private MarcarRevisionItemComoVisualizadaInteractor marcarRevisionItemComoVisualizadaInteractor;

    private final UUID revisionItemId = UtilUUID.generarNuevoUUID();
    private final UUID estudianteId = UtilUUID.generarNuevoUUID();

    private RequestPostProcessor estudianteAutorizado() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(estudianteId.toString()))
                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_VISUALIZADA_UPDATE));
    }

    @Test
    void debe204YConstruirElCommandConElSubjectDelJwt_cuandoPeticionValida() throws Exception {
        // Act
        mockMvc.perform(patch(RUTA, revisionItemId).with(estudianteAutorizado()))
                .andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(MarcarRevisionItemComoVisualizadaCommand.class);
        verify(marcarRevisionItemComoVisualizadaInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().revisionItem()).isEqualTo(revisionItemId);
        assertThat(captor.getValue().estudiante()).isEqualTo(estudianteId);
    }

    @Test
    void debe400_cuandoElPathNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, "no-es-un-uuid").with(estudianteAutorizado()))
                .andExpect(status().isBadRequest());
        verify(marcarRevisionItemComoVisualizadaInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, revisionItemId))
                .andExpect(status().isUnauthorized());
        verify(marcarRevisionItemComoVisualizadaInteractor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoFaltaElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, revisionItemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(estudianteId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_CREATE))))
                .andExpect(status().isForbidden());
        verify(marcarRevisionItemComoVisualizadaInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422_cuandoLaRevisionNoExiste() throws Exception {
        // Arrange
        doThrow(new RevisionItemNoEncontradoException(revisionItemId))
                .when(marcarRevisionItemComoVisualizadaInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, revisionItemId).with(estudianteAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.NO_ENCONTRADA));
    }

    @Test
    void debe422_cuandoElEstudianteNoEsPropietarioDeLaFicha() throws Exception {
        // Arrange
        doThrow(new FichaNoPropietarioException(UUID.randomUUID(), estudianteId))
                .when(marcarRevisionItemComoVisualizadaInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, revisionItemId).with(estudianteAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.FichaPerfil.FICHA_NO_PROPIETARIO));
    }

    @Test
    void debe422_cuandoLaRevisionEstaCerrada() throws Exception {
        // Arrange
        doThrow(new RevisionItemCerradaException(revisionItemId))
                .when(marcarRevisionItemComoVisualizadaInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, revisionItemId).with(estudianteAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.CERRADA));
    }
}
