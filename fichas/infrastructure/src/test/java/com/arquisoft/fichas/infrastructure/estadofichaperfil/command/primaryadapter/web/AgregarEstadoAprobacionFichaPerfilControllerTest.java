package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.AgregarEstadoAprobacionFichaPerfilInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoAprobacionFichaPerfilCommand;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgregarEstadoAprobacionFichaPerfilController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AgregarEstadoAprobacionFichaPerfilControllerTest.TestSecurityConfig.class})
class AgregarEstadoAprobacionFichaPerfilControllerTest {

    private static final String RUTA = "/fichas-perfil/{fichaPerfilId}/estados-ficha/aprobacion";

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
    private AgregarEstadoAprobacionFichaPerfilInteractor interactor;

    private final UUID coordinador = UUID.randomUUID();

    private RequestPostProcessor coordinadorAutorizado() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(coordinador.toString()))
                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_FICHA_PERFIL_APROBACION_CREATE));
    }

    private MockHttpServletRequestBuilder peticion(Object fichaPerfil, String cuerpo) {
        return post(RUTA, fichaPerfil).contentType(MediaType.APPLICATION_JSON).content(cuerpo);
    }

    @Test
    void debe201ConElIdYElCoordinadorDelToken_cuandoPeticionValida() throws Exception {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var idEstado = UUID.randomUUID();
        when(interactor.ejecutar(any())).thenReturn(idEstado);

        // Act
        mockMvc.perform(peticion(fichaPerfil, "{\"acepta\":true}").with(coordinadorAutorizado()))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idEstado.toString()));

        var captor = ArgumentCaptor.forClass(AgregarEstadoAprobacionFichaPerfilCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(captor.getValue().acepta()).isTrue();
        assertThat(captor.getValue().coordinador()).isEqualTo(coordinador);
    }

    @Test
    void debe400_cuandoElBodyNoTraeAcepta() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{}").with(coordinadorAutorizado()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe400_cuandoLaFichaDelPathNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion("no-es-uuid", "{\"acepta\":false}").with(coordinadorAutorizado()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe400_cuandoLaPeticionNoTraeBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON)
                        .with(coordinadorAutorizado()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{\"acepta\":true}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoElTokenTraeOtroClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{\"acepta\":true}")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(coordinador.toString()))
                                .authorities(new SimpleGrantedAuthority(
                                        FichasAuthorities.ESTADO_FICHA_PERFIL_ASESOR_VIEW))))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe422ConElCodigoDeLaRegla_cuandoLaFichaNoEstaDisponibleParaEvaluacion() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new FichaPerfilNoDisponibleParaEvaluacionException(EstadoFicha.APROBADA));

        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{\"acepta\":true}").with(coordinadorAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(FichasCodes.EstadoFichaPerfil.NO_DISPONIBLE_PARA_EVALUACION));
    }
}
