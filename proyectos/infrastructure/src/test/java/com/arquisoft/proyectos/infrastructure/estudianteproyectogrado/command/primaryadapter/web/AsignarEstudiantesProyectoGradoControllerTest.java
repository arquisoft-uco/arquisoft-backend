package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.primaryadapter.web;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.interactor.AsignarEstudiantesProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.primaryport.model.AsignarEstudiantesProyectoGradoCommand;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteYaVinculadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.constant.ProyectosLimits;
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
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.stream.Stream;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsignarEstudiantesProyectoGradoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AsignarEstudiantesProyectoGradoControllerTest.TestSecurityConfig.class})
class AsignarEstudiantesProyectoGradoControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/estudiantes";
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
    private AsignarEstudiantesProyectoGradoInteractor asignarEstudiantesProyectoGradoInteractor;

    private static String cuerpo(int cantidad) {
        var ids = Stream.generate(() -> "\"" + UtilUUID.generarNuevoUUID() + "\"")
                .limit(cantidad)
                .toList();
        return "{\"estudiantes\": [" + String.join(",", ids) + "]}";
    }

    private ResultActions enviar(UUID proyectoGradoId, String body) throws Exception {
        return mockMvc.perform(post(RUTA, proyectoGradoId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(COORDINADOR.toString()))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.ESTUDIANTE_PROYECTO_GRADO_CREATE)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    @Test
    void debe204_cuandoPeticionValida() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        enviar(proyectoGradoId, cuerpo(3)).andExpect(status().isNoContent());
        verify(asignarEstudiantesProyectoGradoInteractor).ejecutar(any());
    }

    @Test
    void debe400_cuandoListaVacia() throws Exception {
        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), cuerpo(0)).andExpect(status().isBadRequest());
        verify(asignarEstudiantesProyectoGradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debe400_cuandoMasDeTres() throws Exception {
        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), cuerpo(4)).andExpect(status().isBadRequest());
        verify(asignarEstudiantesProyectoGradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debe400_cuandoIdNoEsUuid() throws Exception {
        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), "{\"estudiantes\": [\"no-es-uuid\"]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)));
    }

    @Test
    void debe400_cuandoElSubjectDelJwtNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject("no-es-uuid"))
                                .authorities(new SimpleGrantedAuthority(
                                        ProyectosAuthorities.ESTUDIANTE_PROYECTO_GRADO_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(ProyectosFields.EstudianteProyectoGrado.COORDINADOR));
        verify(asignarEstudiantesProyectoGradoInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_sinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_sinClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("proyectos:otra-autoridad")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(1)))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoProyectoNoExiste() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();
        doThrow(new ProyectoGradoNoEncontradoException(proyectoGradoId))
                .when(asignarEstudiantesProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(proyectoGradoId, cuerpo(1))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.ProyectoGrado.NO_ENCONTRADO));
    }

    @Test
    void debePasarElSubjectDelJwtComoCoordinador_cuandoPeticionValida() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();

        // Act
        enviar(proyectoGradoId, cuerpo(1)).andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(AsignarEstudiantesProyectoGradoCommand.class);
        verify(asignarEstudiantesProyectoGradoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().coordinador()).isEqualTo(COORDINADOR);
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGradoId);
    }

    @Test
    void debe422_cuandoElProyectoPerteneceAOtroCoordinador() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();
        doThrow(new ProyectoGradoNoPerteneceCoordinadorException(proyectoGradoId, UtilUUID.generarNuevoUUID()))
                .when(asignarEstudiantesProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(proyectoGradoId, cuerpo(1))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.ProyectoGrado.NO_PERTENECE_COORDINADOR));
    }

    @Test
    void debe422_cuandoEstudianteYaVinculado() throws Exception {
        // Arrange
        doThrow(new EstudianteYaVinculadoException(UtilUUID.generarNuevoUUID()))
                .when(asignarEstudiantesProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), cuerpo(1))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_YA_VINCULADO));
    }

    @Test
    void debe422_cuandoProyectoFinalizado() throws Exception {
        // Arrange
        var proyectoGradoId = UtilUUID.generarNuevoUUID();
        doThrow(new ProyectoGradoFinalizadoException(proyectoGradoId))
                .when(asignarEstudiantesProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(proyectoGradoId, cuerpo(1))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.ProyectoGrado.FINALIZADO));
    }

    @Test
    void debe422_cuandoCupoExcedido() throws Exception {
        // Arrange
        doThrow(new CupoEstudiantesExcedidoException(ProyectosLimits.EstudianteProyectoGrado.MAX_ESTUDIANTES))
                .when(asignarEstudiantesProyectoGradoInteractor).ejecutar(any());

        // Act & Assert
        enviar(UtilUUID.generarNuevoUUID(), cuerpo(2))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.EstudianteProyectoGrado.CUPO_EXCEDIDO));
    }
}
