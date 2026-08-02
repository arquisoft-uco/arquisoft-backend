package com.arquisoft.fichas.infrastructure.fichaperfil.command.adapter.in.web;

import com.arquisoft.shared.web.config.MessageCatalogConfig;
import com.arquisoft.shared.message.FichasCodes;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.Messages;
import com.arquisoft.shared.message.ValidationKeys;
import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.application.fichaperfil.command.interactor.CambiarAsesorFichaInteractor;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.exception.DomainValidationException;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CambiarAsesorFichaInputAdapter.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, MessageCatalogConfig.class,
        CambiarAsesorFichaInputAdapterTest.TestSecurityConfig.class
})
class CambiarAsesorFichaInputAdapterTest {

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
    private CambiarAsesorFichaInteractor cambiarAsesorFichaInteractor;

    @Test
    void debeRetornar204_cuandoCambioExitoso() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        String requestBody = String.format("""
                {
                    "asesorFicha": "%s"
                }
                """, nuevoAsesorId);

        doNothing().when(cambiarAsesorFichaInteractor).ejecutar(any(CambiarAsesorFichaCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}/asesor-ficha", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE_ASESOR)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());
    }

    @Test
    void debeRetornar400_cuandoAsesorFichaIdNuloEnBody() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();

        String requestBody = """
                {
                    "asesorFicha": null
                }
                """;

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}/asesor-ficha", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE_ASESOR)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de validación en los datos enviados"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(FichasFields.FichaPerfil.ASESOR_FICHA))
                .andExpect(jsonPath("$.fieldErrors[0].message")
                        .value(Messages.obtener(
                                ValidationKeys.sinLlaves(ValidationKeys.FichaPerfil.ASESOR_OBLIGATORIO))));
    }

    @Test
    void debeRetornar400_cuandoFichaNoEncontrada() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        String requestBody = String.format("""
                {
                    "asesorFicha": "%s"
                }
                """, nuevoAsesorId);

        doThrow(new FichaPerfilNoEncontradaException(fichaId))
                .when(cambiarAsesorFichaInteractor).ejecutar(any(CambiarAsesorFichaCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}/asesor-ficha", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE_ASESOR)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.FichaPerfil.FICHA_NO_ENCONTRADA));
    }

    @Test
    void debeRetornar400_cuandoAsesorNoEncontrado() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        String requestBody = String.format("""
                {
                    "asesorFicha": "%s"
                }
                """, nuevoAsesorId);

        doThrow(new AsesorFichaNoEncontradoException(nuevoAsesorId))
                .when(cambiarAsesorFichaInteractor).ejecutar(any(CambiarAsesorFichaCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}/asesor-ficha", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE_ASESOR)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorCode").value(FichasCodes.FichaPerfil.ASESOR_NO_ENCONTRADO));
    }

    @Test
    void debeRetornar422_cuandoMismoAsesor() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorId = UUID.randomUUID();

        String requestBody = String.format("""
                {
                    "asesorFicha": "%s"
                }
                """, asesorId);

        ValidationResult result = new ValidationResult();
        result.agregarError(
                FichasFields.FichaPerfil.ASESOR_FICHA,
                FichasCodes.FichaPerfil.MISMO_ASESOR,
                Messages.formatear(FichasKeys.FichaPerfil.ERROR_MISMO_ASESOR, asesorId)
        );

        doThrow(new DomainValidationException(result))
                .when(cambiarAsesorFichaInteractor).ejecutar(any(CambiarAsesorFichaCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}/asesor-ficha", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE_ASESOR)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorCode").value("DOMAIN_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(FichasFields.FichaPerfil.ASESOR_FICHA))
                .andExpect(jsonPath("$.fieldErrors[0].message").value(Messages.formatear(FichasKeys.FichaPerfil.ERROR_MISMO_ASESOR, asesorId)));
    }

    @Test
    void debeRetornar422_cuandoEstadoTerminal() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        String requestBody = String.format("""
                {
                    "asesorFicha": "%s"
                }
                """, nuevoAsesorId);

        ValidationResult result = new ValidationResult();
        result.agregarError(
                FichasFields.FichaPerfil.ESTADO_FICHA,
                FichasCodes.FichaPerfil.ESTADO_TERMINAL,
                Messages.formatear(FichasKeys.FichaPerfil.ERROR_ESTADO_TERMINAL, "APROBADA")
        );

        doThrow(new DomainValidationException(result))
                .when(cambiarAsesorFichaInteractor).ejecutar(any(CambiarAsesorFichaCommand.class));

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}/asesor-ficha", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_UPDATE_ASESOR)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.errorCode").value("DOMAIN_VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value(FichasFields.FichaPerfil.ESTADO_FICHA))
                .andExpect(jsonPath("$.fieldErrors[0].message").value(Messages.formatear(FichasKeys.FichaPerfil.ERROR_ESTADO_TERMINAL, "APROBADA")));
    }

    @Test
    void debeRetornar401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        String requestBody = String.format("""
                {
                    "asesorFicha": "%s"
                }
                """, nuevoAsesorId);

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}/asesor-ficha", fichaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSinPermisos() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        String requestBody = String.format("""
                {
                    "asesorFicha": "%s"
                }
                """, nuevoAsesorId);

        // Act & Assert
        mockMvc.perform(patch("/fichas-perfil/{id}/asesor-ficha", fichaId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:otro-permiso")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}
