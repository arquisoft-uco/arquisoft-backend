package com.arquisoft.usuarios.infrastructure.bibliotecario.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.bibliotecario.query.primaryport.interactor.ConsultarBibliotecariosAdministradorInteractor;
import com.arquisoft.usuarios.application.bibliotecario.query.readmodel.BibliotecarioReadModel;
import com.arquisoft.usuarios.infrastructure.security.UsuariosAuthorities;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarBibliotecariosAdministradorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarBibliotecariosAdministradorControllerTest.TestSecurityConfig.class})
class ConsultarBibliotecariosAdministradorControllerTest {

    private static final String RUTA = "/usuarios/bibliotecarios/administrador";

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
    private ConsultarBibliotecariosAdministradorInteractor consultarBibliotecariosAdministradorInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.BIBLIOTECARIO_ADMINISTRADOR_VIEW));
    }

    @Test
    void debeRetornar200ConResponseDTO_cuandoAutorizado() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var readModel = new BibliotecarioReadModel(id, "1003", "Carla Vidal",
                "carla.vidal@uco.edu.co", "3000000000", "INACTIVO", false);
        when(consultarBibliotecariosAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel), 0, 20, 1L));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 20,
                  "ordenamiento": ["nombre:ASC"],
                  "filtros": { "tipo": "PREDICADO", "campo": "vigente", "operador": "ES", "valor": "false" }
                }
                """;

        // Act
        var respuesta = mockMvc.perform(post(RUTA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(administrador()));

        // Assert
        respuesta.andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].identificador").value("1003"))
                .andExpect(jsonPath("$.content[0].nombre").value("Carla Vidal"))
                .andExpect(jsonPath("$.content[0].email").value("carla.vidal@uco.edu.co"))
                .andExpect(jsonPath("$.content[0].contacto").value("3000000000"))
                .andExpect(jsonPath("$.content[0].estado").value("INACTIVO"))
                .andExpect(jsonPath("$.content[0].vigente").value(false))
                .andExpect(jsonPath("$.totalElements").value(1));
        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(consultarBibliotecariosAdministradorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().tamanio()).isEqualTo(20);
        assertThat(captor.getValue().ordenamiento()).hasSize(1);
        assertThat(captor.getValue().raiz()).isNotNull();
    }

    @Test
    void debeRetornar400_cuandoFiltroInvalido() throws Exception {
        // Arrange
        when(consultarBibliotecariosAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenThrow(new FiltroException("campo de filtro no permitido: contacto",
                        "usuarios.consulta.campo-filtro-no-permitido"));
        var body = """
                {
                  "filtros": { "tipo": "PREDICADO", "campo": "contacto", "operador": "ES", "valor": "x" }
                }
                """;

        // Act
        var respuesta = mockMvc.perform(post(RUTA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(administrador()));

        // Assert
        respuesta.andExpect(status().isBadRequest());
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act
        var respuesta = mockMvc.perform(post(RUTA));

        // Assert
        respuesta.andExpect(status().isUnauthorized());
        verifyNoInteractions(consultarBibliotecariosAdministradorInteractor);
    }

    @Test
    void debeRetornar403_cuandoSinClientRole() throws Exception {
        // Act
        var respuesta = mockMvc.perform(post(RUTA)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ADMINISTRADOR_ADMINISTRADOR_VIEW))));

        // Assert
        respuesta.andExpect(status().isForbidden());
        verifyNoInteractions(consultarBibliotecariosAdministradorInteractor);
    }
}
