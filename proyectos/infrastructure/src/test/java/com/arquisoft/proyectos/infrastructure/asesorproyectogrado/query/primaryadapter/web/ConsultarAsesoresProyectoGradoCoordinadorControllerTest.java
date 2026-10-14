package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.interactor.ConsultarAsesoresProyectoGradoCoordinadorInteractor;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoPerteneceCoordinadorException;
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

import java.time.Instant;
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

@WebMvcTest(ConsultarAsesoresProyectoGradoCoordinadorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarAsesoresProyectoGradoCoordinadorControllerTest.TestSecurityConfig.class})
class ConsultarAsesoresProyectoGradoCoordinadorControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/asesores";
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
    private ConsultarAsesoresProyectoGradoCoordinadorInteractor interactor;

    private ResultActions consultar(String proyectoGradoId) throws Exception {
        return mockMvc.perform(get(RUTA, proyectoGradoId)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(COORDINADOR.toString()))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.ASESOR_PROYECTO_GRADO_COORDINADOR_VIEW))));
    }

    @Test
    void debeRetornar200ConCampos_cuandoConsultaExitosa() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        var vinculo = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(List.of(
                new AsesorProyectoGradoReadModel(vinculo, proyectoGrado, asesor, "1001", "Ana",
                        "ana@soyuco.edu.co", "ASIGNADO", "Asignado", Instant.parse("2026-10-05T10:00:00Z"), true, true),
                new AsesorProyectoGradoReadModel(UtilUUID.generarNuevoUUID(), proyectoGrado,
                        UtilUUID.generarNuevoUUID(), "1002", "Beto", "beto@soyuco.edu.co", "FINALIZADO",
                        "Finalizado", Instant.parse("2026-09-10T10:00:00Z"), false, false)));

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(vinculo.toString()))
                .andExpect(jsonPath("$[0].proyectoGradoId").value(proyectoGrado.toString()))
                .andExpect(jsonPath("$[0].asesorId").value(asesor.toString()))
                .andExpect(jsonPath("$[0].identificador").value("1001"))
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].email").value("ana@soyuco.edu.co"))
                .andExpect(jsonPath("$[0].estadoAsesor").value("ASIGNADO"))
                .andExpect(jsonPath("$[0].estadoAsesorNombre").value("Asignado"))
                .andExpect(jsonPath("$[0].vigente").value(true))
                .andExpect(jsonPath("$[0].actual").value(true))
                .andExpect(jsonPath("$[1].estadoAsesor").value("FINALIZADO"))
                .andExpect(jsonPath("$[1].vigente").value(false))
                .andExpect(jsonPath("$[1].actual").value(false));
        var captor = ArgumentCaptor.forClass(ConsultarAsesoresProyectoGradoCoordinadorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().coordinador()).isEqualTo(COORDINADOR);
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoProyectoSinAsesores() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        consultar(UtilUUID.generarNuevoUUID().toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void debeRetornar400_cuandoProyectoGradoIdNoEsUuid() throws Exception {
        // Act & Assert
        consultar("no-es-uuid").andExpect(status().isBadRequest());
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
                                .authorities(new SimpleGrantedAuthority("proyectos:otra-autoridad"))))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoProyectoAjeno() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        doThrow(new ProyectoGradoNoPerteneceCoordinadorException(proyectoGrado, COORDINADOR))
                .when(interactor).ejecutar(any());

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.ProyectoGrado.NO_PERTENECE_COORDINADOR));
    }

    @Test
    void debeRetornar422_cuandoProyectoNoExiste() throws Exception {
        // Arrange
        var proyectoGrado = UtilUUID.generarNuevoUUID();
        doThrow(new ProyectoGradoNoEncontradoException(proyectoGrado)).when(interactor).ejecutar(any());

        // Act & Assert
        consultar(proyectoGrado.toString())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(ProyectosCodes.ProyectoGrado.NO_ENCONTRADO));
    }
}
