package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.interactor.RemoverRevisionItemInteractor;
import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.RemoverRevisionItemCommand;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverRevisionItemController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        RemoverRevisionItemControllerTest.TestSecurityConfig.class})
class RemoverRevisionItemControllerTest {

    private static final String RUTA = "/fichas-perfil/revisiones/{revisionItemId}";

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
    private RemoverRevisionItemInteractor removerRevisionItemInteractor;

    private final UUID revisionItemId = UtilUUID.generarNuevoUUID();
    private final UUID asesorId = UtilUUID.generarNuevoUUID();

    private RequestPostProcessor asesorAutorizado() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(asesorId.toString()))
                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_DELETE));
    }

    @Test
    void debe204YConstruirElCommandConElSubjectDelJwt_cuandoPeticionValida() throws Exception {
        // Act
        mockMvc.perform(delete(RUTA, revisionItemId).with(asesorAutorizado()))
                .andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverRevisionItemCommand.class);
        verify(removerRevisionItemInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().revisionItem()).isEqualTo(revisionItemId);
        assertThat(captor.getValue().asesorFicha()).isEqualTo(asesorId);
    }

    @Test
    void debe400_cuandoElPathNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, "no-es-un-uuid").with(asesorAutorizado()))
                .andExpect(status().isBadRequest());
        verify(removerRevisionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debe400ConFieldErrors_cuandoElSubjectNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, revisionItemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject("no-es-un-uuid"))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_DELETE))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value(FichasFields.RevisionItem.ASESOR_FICHA));
        verify(removerRevisionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, revisionItemId))
                .andExpect(status().isUnauthorized());
        verify(removerRevisionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoFaltaElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, revisionItemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(asesorId.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.REVISION_ITEM_CREATE))))
                .andExpect(status().isForbidden());
        verify(removerRevisionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422_cuandoLaRevisionNoExiste() throws Exception {
        // Arrange
        doThrow(new RevisionItemNoEncontradoException(revisionItemId))
                .when(removerRevisionItemInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, revisionItemId).with(asesorAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.NO_ENCONTRADA));
    }

    @Test
    void debe422_cuandoElSolicitanteNoEsElAsesorDeLaFicha() throws Exception {
        // Arrange
        doThrow(new FichaNoPerteneceAsesorException(UtilUUID.generarNuevoUUID(), asesorId))
                .when(removerRevisionItemInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, revisionItemId).with(asesorAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.FichaPerfil.FICHA_NO_PERTENECE_ASESOR));
    }

    @Test
    void debe422_cuandoLaRevisionEstaCerrada() throws Exception {
        // Arrange
        doThrow(new RevisionItemCerradaException(revisionItemId))
                .when(removerRevisionItemInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, revisionItemId).with(asesorAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.RevisionItem.CERRADA));
    }
}
