package com.arquisoft.proyectos.infrastructure.estadoasesor.query.primaryadapter.web;

import com.arquisoft.proyectos.application.estadoasesor.query.primaryport.interactor.ConsultarEstadosPosiblesAsesorInteractor;
import com.arquisoft.proyectos.application.estadoasesor.query.readmodel.EstadoAsesorReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosPosiblesAsesorController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarEstadosPosiblesAsesorControllerTest.TestSecurityConfig.class})
class ConsultarEstadosPosiblesAsesorControllerTest {

    private static final String RUTA = "/proyectos-grado/estados-asesor";

    @TestConfiguration
    @EnableWebSecurity
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
    private ConsultarEstadosPosiblesAsesorInteractor interactor;

    private ResultActions consultar(String body) throws Exception {
        var peticion = post(RUTA)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .jwt(jwt -> jwt.subject(UtilUUID.generarNuevoUUID().toString())));
        if (body != null) {
            peticion.content(body);
        }
        return mockMvc.perform(peticion);
    }

    @Test
    void debeResponder200ConPagina_cuandoHayEstados() throws Exception {
        // Arrange
        var body = """
                {"pagina": 1, "tamanio": 5, "ordenamiento": ["nombre:DESC"]}
                """;
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(
                new EstadoAsesorReadModel("ASIGNADO", "Asignado", "Asesor asignado")), 1, 5, 6L));

        // Act & Assert
        consultar(body)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("ASIGNADO"))
                .andExpect(jsonPath("$.content[0].nombre").value("Asignado"))
                .andExpect(jsonPath("$.content[0].descripcion").value("Asesor asignado"))
                .andExpect(jsonPath("$.totalElements").value(6));
        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().pagina()).isEqualTo(1);
        assertThat(captor.getValue().tamanio()).isEqualTo(5);
        assertThat(captor.getValue().ordenamiento().getFirst().getCampo()).isEqualTo("nombre");
    }

    @Test
    void debeResponder200_cuandoNoHayCuerpo() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act & Assert
        consultar(null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().pagina()).isZero();
        assertThat(captor.getValue().tamanio()).isEqualTo(10);
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
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
