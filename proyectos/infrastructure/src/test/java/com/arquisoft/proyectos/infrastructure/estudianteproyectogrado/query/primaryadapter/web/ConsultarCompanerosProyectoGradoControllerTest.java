package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.interactor.ConsultarCompanerosProyectoGradoInteractor;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarCompanerosProyectoGradoQuery;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteNoVinculadoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.ProyectosCodes;
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

@WebMvcTest(ConsultarCompanerosProyectoGradoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarCompanerosProyectoGradoControllerTest.TestSecurityConfig.class})
class ConsultarCompanerosProyectoGradoControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/companeros";
    private static final UUID ESTUDIANTE = UtilUUID.generarNuevoUUID();

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
    private ConsultarCompanerosProyectoGradoInteractor interactor;

    private ResultActions consultar(String proyectoGradoId) throws Exception {
        return mockMvc.perform(get(RUTA, proyectoGradoId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(ESTUDIANTE.toString()))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_VIEW))));
    }

    @Test
    void debeResponder200ConLosCuatroCampos_cuandoConsultaExitosa() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of(
                new CompaneroProyectoGradoReadModel(ESTUDIANTE, "2020", "Ana", "ana@soyuco.edu.co"),
                new CompaneroProyectoGradoReadModel(otro, "2021", "Beto", "beto@soyuco.edu.co")));

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].estudianteId").value(ESTUDIANTE.toString()))
                .andExpect(jsonPath("$[0].identificador").value("2020"))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].email").value("ana@soyuco.edu.co"))
                .andExpect(jsonPath("$[1].estudianteId").value(otro.toString()));
        var captor = ArgumentCaptor.forClass(ConsultarCompanerosProyectoGradoQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().estudiante()).isEqualTo(ESTUDIANTE);
    }

    @Test
    void debeResponder400_cuandoProyectoGradoIdNoEsUuid() throws Exception {
        // Act & Assert
        consultar("no-es-uuid").andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())).andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoSoloTieneElRoleDelCoordinador() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        ProyectosAuthorities.ESTUDIANTE_PROYECTO_GRADO_COORDINADOR_VIEW))))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoEstudianteNoVinculado() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        doThrow(new EstudianteNoVinculadoException(ESTUDIANTE, proyectoGrado)).when(interactor).ejecutar(any());

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.EstudianteProyectoGrado.ESTUDIANTE_NO_VINCULADO));
    }

    @Test
    void debeResponder422_cuandoProyectoNoExiste() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        doThrow(new ProyectoGradoNoEncontradoException(proyectoGrado)).when(interactor).ejecutar(any());

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.ProyectoGrado.NO_ENCONTRADO));
    }
}
