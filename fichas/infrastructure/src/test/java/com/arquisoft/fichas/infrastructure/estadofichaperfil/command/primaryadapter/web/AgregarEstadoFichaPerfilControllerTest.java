package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.primaryadapter.web;

import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.interactor.AgregarEstadoFichaPerfilInteractor;
import com.arquisoft.fichas.application.estadofichaperfil.command.primaryport.model.AgregarEstadoFichaPerfilCommand;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.TransicionEstadoFichaNoPermitidaException;
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

@WebMvcTest(AgregarEstadoFichaPerfilController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AgregarEstadoFichaPerfilControllerTest.TestSecurityConfig.class})
class AgregarEstadoFichaPerfilControllerTest {

    private static final String RUTA = "/fichas-perfil/{fichaPerfilId}/estados-ficha";

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
    private AgregarEstadoFichaPerfilInteractor interactor;

    private final UUID asesorFicha = UUID.randomUUID();

    private RequestPostProcessor conRol(String rol) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(jwt -> jwt.subject(asesorFicha.toString()))
                .authorities(new SimpleGrantedAuthority(rol));
    }

    private RequestPostProcessor asesorAutorizado() {
        return conRol(FichasAuthorities.ESTADO_FICHA_PERFIL_ASESOR_CREATE);
    }

    private MockHttpServletRequestBuilder peticion(Object fichaPerfil, String cuerpo) {
        return post(RUTA, fichaPerfil).contentType(MediaType.APPLICATION_JSON).content(cuerpo);
    }

    @Test
    void debe201ConElIdYElAsesorDelToken_cuandoPeticionValida() throws Exception {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var idEstado = UUID.randomUUID();
        when(interactor.ejecutar(any())).thenReturn(idEstado);

        // Act
        mockMvc.perform(peticion(fichaPerfil, "{\"estadoFicha\":\"DESCARTADA\"}").with(asesorAutorizado()))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(idEstado.toString()));

        var captor = ArgumentCaptor.forClass(AgregarEstadoFichaPerfilCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(captor.getValue().estadoFicha()).isEqualTo("DESCARTADA");
        assertThat(captor.getValue().asesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debe400_cuandoElBodyNoTraeEstadoFicha() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{}").with(asesorAutorizado()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe400_cuandoLaFichaDelPathNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion("no-es-uuid", "{\"estadoFicha\":\"DESCARTADA\"}").with(asesorAutorizado()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{\"estadoFicha\":\"DESCARTADA\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoElTokenTraeElRolDeConsultaDelAsesor() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{\"estadoFicha\":\"DESCARTADA\"}")
                        .with(conRol(FichasAuthorities.ESTADO_FICHA_PERFIL_ASESOR_VIEW)))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoElTokenTraeElRolDeAprobacionDelCoordinador() throws Exception {
        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{\"estadoFicha\":\"DESCARTADA\"}")
                        .with(conRol(FichasAuthorities.ESTADO_FICHA_PERFIL_APROBACION_CREATE)))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debe422ConElCodigoDeLaRegla_cuandoLaTransicionNoEstaPermitida() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenThrow(new TransicionEstadoFichaNoPermitidaException(
                EstadoFicha.DESCARTADA, EstadoFicha.DISPONIBLE_PARA_EVALUACION));

        // Act & Assert
        mockMvc.perform(peticion(UUID.randomUUID(), "{\"estadoFicha\":\"DISPONIBLE_PARA_EVALUACION\"}")
                        .with(asesorAutorizado()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.EstadoFichaPerfil.TRANSICION_NO_PERMITIDA));
    }
}
