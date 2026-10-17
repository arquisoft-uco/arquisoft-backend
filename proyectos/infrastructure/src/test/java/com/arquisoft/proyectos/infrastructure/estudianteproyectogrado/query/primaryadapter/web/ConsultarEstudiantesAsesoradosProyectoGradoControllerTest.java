package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.ConsultarEstudiantesAsesoradosProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarEstudiantesAsesoradosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteAsesoradoProyectoGradoReadModel;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstudiantesAsesoradosProyectoGradoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstudiantesAsesoradosProyectoGradoControllerTest.TestSecurityConfig.class})
class ConsultarEstudiantesAsesoradosProyectoGradoControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/estudiantes-asesorados";
    private static final UUID ASESOR = UtilUUID.generarNuevoUUID();

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
    private ConsultarEstudiantesAsesoradosProyectoGradoInteractor interactor;

    private ResultActions consultar(String proyectoGradoId, String subject) throws Exception {
        return mockMvc.perform(get(RUTA, proyectoGradoId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(subject))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.ESTUDIANTE_PROYECTO_GRADO_ASESOR_VIEW))));
    }

    @Test
    void debeRetornar200_cuandoAsesorAutorizado() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var primero = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of(
                new EstudianteAsesoradoProyectoGradoReadModel(primero, "2020", "Ana", "ana@soyuco.edu.co"),
                new EstudianteAsesoradoProyectoGradoReadModel(otro, "2021", "Beto", "beto@soyuco.edu.co")));

        // Act & Assert
        consultar(proyectoGrado.toString(), ASESOR.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estudianteId").value(primero.toString()))
                .andExpect(jsonPath("$[0].identificador").value("2020"))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].email").value("ana@soyuco.edu.co"))
                .andExpect(jsonPath("$[1].estudianteId").value(otro.toString()));
        var captor = ArgumentCaptor.forClass(ConsultarEstudiantesAsesoradosProyectoGradoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR);
    }

    @Test
    void debeRetornar400_cuandoSubjectNoEsUuid() throws Exception {
        // Act & Assert
        consultar(UtilUUID.generarNuevoUUID().toString(), "no-es-uuid")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(ProyectosFields.EstudianteProyectoGrado.ASESOR));
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())).andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoSinAuthority() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        ProyectosAuthorities.ESTUDIANTE_PROYECTO_GRADO_COORDINADOR_VIEW))))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoProyectoNoExiste() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        doThrow(new ProyectoGradoNoEncontradoException(proyectoGrado)).when(interactor).ejecutar(any());

        // Act & Assert
        consultar(proyectoGrado.toString(), ASESOR.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.ProyectoGrado.NO_ENCONTRADO));
    }

    @Test
    void debeRetornar422_cuandoAsesorNoAsignado() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        doThrow(new AsesorProyectoGradoNoEncontradoException(ASESOR, proyectoGrado))
                .when(interactor).ejecutar(any());

        // Act & Assert
        consultar(proyectoGrado.toString(), ASESOR.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(ProyectosCodes.AsesorProyectoGrado.ASESOR_PROYECTO_GRADO_NO_ENCONTRADO));
    }
}
