package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel.EstadoProyectoGradoReadModel;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.interactor.ConsultarProyectosGradoCoordinadorInteractor;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarProyectosGradoCoordinadorQuery;
import com.arquisoft.proyectos.application.proyectogrado.query.readmodel.ProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.security.ProyectosAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarProyectosGradoCoordinadorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarProyectosGradoCoordinadorControllerTest.TestSecurityConfig.class})
class ConsultarProyectosGradoCoordinadorControllerTest {

    private static final String RUTA = "/proyectos-grado/coordinador";
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
    private ConsultarProyectosGradoCoordinadorInteractor interactor;

    private ResultActions consultar(String body) throws Exception {
        var peticion = post(RUTA)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(COORDINADOR.toString()))
                        .authorities(new SimpleGrantedAuthority(
                                ProyectosAuthorities.PROYECTO_GRADO_COORDINADOR_VIEW)));
        if (body != null) {
            peticion.content(body);
        }
        return mockMvc.perform(peticion);
    }

    @Test
    void debeResponder200ConCamposYCoordinadorDelJwt_cuandoSinBody() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ficha = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(
                new ProyectoGradoReadModel(id, ficha, "Sistema de gestion",
                        new EstadoProyectoGradoReadModel("EN_PROCESO", "En proceso"))), 0, 10, 1L));

        // Act & Assert
        consultar(null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].fichaPerfil").value(ficha.toString()))
                .andExpect(jsonPath("$.content[0].tituloProyecto").value("Sistema de gestion"))
                .andExpect(jsonPath("$.content[0].estado.id").value("EN_PROCESO"))
                .andExpect(jsonPath("$.content[0].estado.nombre").value("En proceso"))
                .andExpect(jsonPath("$.totalElements").value(1));
        var captor = ArgumentCaptor.forClass(ConsultarProyectosGradoCoordinadorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().coordinador()).isEqualTo(COORDINADOR);
    }

    @Test
    void debeResponder200ConPaginaVacia_cuandoConFiltrosYOrden() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 0, 5, 0L));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 5,
                  "ordenamiento": ["tituloProyecto:ASC"],
                  "filtros": {"tipo": "PREDICADO", "campo": "estadoProyectoGrado", "operador": "ES", "valor": "EN_PROCESO"}
                }
                """;

        // Act & Assert
        consultar(body)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        var captor = ArgumentCaptor.forClass(ConsultarProyectosGradoCoordinadorQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(5);
        assertThat(captor.getValue().criterio().raiz()).isNotNull();
    }

    @Test
    void debeResponder400_cuandoFiltroInvalido() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenThrow(new FiltroException("campo no permitido", "proyectos.consulta.campo-no-permitido"));

        // Act & Assert
        consultar("{}").andExpect(status().isBadRequest());
    }

    @Test
    void debeResponder401_cuandoSinToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoSinClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(jwt -> jwt.subject(COORDINADOR.toString()))
                                .authorities(new SimpleGrantedAuthority(
                                        ProyectosAuthorities.ASESOR_PROYECTO_GRADO_COORDINADOR_VIEW))))
                .andExpect(status().isForbidden());
    }
}
