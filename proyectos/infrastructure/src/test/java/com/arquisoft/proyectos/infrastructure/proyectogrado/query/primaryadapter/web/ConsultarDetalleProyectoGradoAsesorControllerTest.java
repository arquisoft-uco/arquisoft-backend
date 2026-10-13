package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.estadoproyecto.query.readmodel.EstadoProyectoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarDetalleProyectoGradoAsesorInteractor;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarDetalleProyectoGradoAsesorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.AsesorProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoEncontradoException;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarDetalleProyectoGradoAsesorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarDetalleProyectoGradoAsesorControllerTest.TestSecurityConfig.class})
class ConsultarDetalleProyectoGradoAsesorControllerTest {

    private static final String RUTA = "/proyectos-grado/{proyectoGradoId}/asesor";
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
    private ConsultarDetalleProyectoGradoAsesorInteractor interactor;

    private ResultActions consultar(UUID proyectoGrado, String subject) throws Exception {
        return mockMvc.perform(get(RUTA, proyectoGrado)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(subject))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.PROYECTO_GRADO_ASESOR_DETALLE_VIEW))));
    }

    @Test
    void debeResponder200ConCamposYAsesorDelJwt_cuandoAsesorAutorizado() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(new ProyectoGradoReadModel(id, ficha, "Sistema de gestion",
                new EstadoProyectoReadModel("EN_PROCESO", "En proceso")));

        // Act & Assert
        consultar(id, ASESOR.toString())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.fichaPerfil").value(ficha.toString()))
                .andExpect(jsonPath("$.tituloProyecto").value("Sistema de gestion"))
                .andExpect(jsonPath("$.estado.id").value("EN_PROCESO"))
                .andExpect(jsonPath("$.estado.nombre").value("En proceso"));
        var captor = ArgumentCaptor.forClass(ConsultarDetalleProyectoGradoAsesorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(id);
        assertThat(captor.getValue().asesor()).isEqualTo(ASESOR);
    }

    @Test
    void debeResponder400_cuandoSubjectNoEsUuid() throws Exception {
        // Act & Assert
        consultar(UtilUUID.generarNuevoUUID(), "no-es-uuid").andExpect(status().isBadRequest());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())).andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoSinClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(ASESOR.toString()))
                                .authorities(new SimpleGrantedAuthority(
                                        ProyectosAuthorities.PROYECTO_GRADO_ASESOR_VIEW))))
                .andExpect(status().isForbidden());
        verifyNoInteractions(interactor);
    }

    @Test
    void debeResponder422_cuandoProyectoNoExiste() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenThrow(new ProyectoGradoNoEncontradoException(id));

        // Act & Assert
        consultar(id, ASESOR.toString()).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debeResponder422_cuandoAsesorNoAsignado() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenThrow(new AsesorProyectoGradoNoEncontradoException(ASESOR, id));

        // Act & Assert
        consultar(id, ASESOR.toString()).andExpect(status().isUnprocessableEntity());
    }
}
