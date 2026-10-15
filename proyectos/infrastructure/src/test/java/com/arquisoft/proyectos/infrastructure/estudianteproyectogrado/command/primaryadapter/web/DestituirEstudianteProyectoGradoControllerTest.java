package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.DestituirEstudianteProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.DestituirEstudianteProyectoGradoCommand;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DestituirEstudianteProyectoGradoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        DestituirEstudianteProyectoGradoControllerTest.TestSecurityConfig.class})
class DestituirEstudianteProyectoGradoControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/estudiantes/{estudianteId}";
    private static final UUID COORDINADOR = UtilUUID.generarNuevoUUID();

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
    private DestituirEstudianteProyectoGradoInteractor destituirEstudianteProyectoGradoInteractor;

    private ResultActions enviar(UUID proyectoGradoId, UUID estudianteId, String subject) throws Exception {
        return mockMvc.perform(delete(RUTA, proyectoGradoId, estudianteId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(subject))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.ESTUDIANTE_PROYECTO_GRADO_DELETE))));
    }

    @Test
    void debeResponder204YPasarElSubjectComoCoordinador_cuandoDestituyeConExito() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();
        var estudianteId = UtilUUID.generarNuevoUUID();

        // Act
        enviar(proyectoGradoId, estudianteId, COORDINADOR.toString()).andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(DestituirEstudianteProyectoGradoCommand.class);
        verify(destituirEstudianteProyectoGradoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGradoId);
        assertThat(captor.getValue().estudiante()).isEqualTo(estudianteId);
        assertThat(captor.getValue().coordinador()).isEqualTo(COORDINADOR);
    }

    @Test
    void debeResponder400_cuandoElSubjectDelJwtNoEsUuid() throws Exception {
        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "no-es-uuid")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(ProyectosFields.EstudianteProyectoGrado.COORDINADOR));
        verify(destituirEstudianteProyectoGradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoSinAuthority() throws Exception {
        // Act & Assert
        mockMvc.perform(delete(RUTA, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("proyectos:otra-autoridad"))))
                .andExpect(status().isForbidden());
        verify(destituirEstudianteProyectoGradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoReglaDeDominioFalla() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();
        var estudianteId = UtilUUID.generarNuevoUUID();
        doThrow(new EstudianteNoVinculadoException(estudianteId, proyectoGradoId))
                .when(destituirEstudianteProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(proyectoGradoId, estudianteId, COORDINADOR.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_NO_VINCULADO));
    }
}
