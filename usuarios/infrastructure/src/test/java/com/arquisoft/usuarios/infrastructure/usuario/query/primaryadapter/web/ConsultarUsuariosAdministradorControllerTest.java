package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarUsuariosAdministradorInteractor;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioReadModel;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarUsuariosAdministradorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarUsuariosAdministradorControllerTest.TestSecurityConfig.class})
class ConsultarUsuariosAdministradorControllerTest {

    private static final String RUTA = "/usuarios/administrador";

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
    private ConsultarUsuariosAdministradorInteractor consultarUsuariosAdministradorInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor administrador() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.USUARIO_ADMINISTRADOR_VIEW));
    }

    @Test
    void debeRetornar200ConResponseDTO_cuandoAutorizado() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var readModel = new UsuarioReadModel(id, "1002", "Bruno Diaz", "bruno.diaz@uco.edu.co",
                "3000000000", "ACTIVO", true, true, true, false, false, true);
        when(consultarUsuariosAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(readModel), 0, 20, 1L));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 20,
                  "ordenamiento": ["nombre:ASC"],
                  "filtros": { "tipo": "GRUPO", "conector": "OR", "nodos": [
                    { "tipo": "PREDICADO", "campo": "esEstudiante", "operador": "ES", "valor": "true" },
                    { "tipo": "PREDICADO", "campo": "esAsesor", "operador": "ES", "valor": "true" }
                  ]}
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
                .andExpect(jsonPath("$.content[0].identificador").value("1002"))
                .andExpect(jsonPath("$.content[0].estado").value("ACTIVO"))
                .andExpect(jsonPath("$.content[0].vigente").value(true))
                .andExpect(jsonPath("$.content[0].esEstudiante").value(true))
                .andExpect(jsonPath("$.content[0].esAsesor").value(true))
                .andExpect(jsonPath("$.content[0].esAsesorFicha").value(false))
                .andExpect(jsonPath("$.content[0].esCoordinador").value(false))
                .andExpect(jsonPath("$.content[0].esRepresentanteComite").value(true))
                .andExpect(jsonPath("$.totalElements").value(1));
        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(consultarUsuariosAdministradorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().tamanio()).isEqualTo(20);
        assertThat(captor.getValue().ordenamiento()).hasSize(1);
        assertThat(captor.getValue().raiz()).isNotNull();
    }

    @Test
    void debeRetornar200_cuandoSinBody() throws Exception {
        // Arrange
        when(consultarUsuariosAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));

        // Act
        var respuesta = mockMvc.perform(post(RUTA).with(administrador()));

        // Assert
        respuesta.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        var captor = ArgumentCaptor.forClass(ConsultaCriteriaQuery.class);
        verify(consultarUsuariosAdministradorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().raiz()).isNull();
    }

    @Test
    void debeRetornar400_cuandoFiltroInvalido() throws Exception {
        // Arrange
        when(consultarUsuariosAdministradorInteractor.ejecutar(any(ConsultaCriteriaQuery.class)))
                .thenThrow(new FiltroInvalidoException("operador IN no aplicable a un campo booleano"));
        var body = """
                {
                  "pagina": 0,
                  "tamanio": 10,
                  "filtros": {
                    "tipo": "PREDICADO_MULTIVALOR",
                    "campo": "esAsesor",
                    "operador": "IN",
                    "valores": ["true", "false"]
                  }
                }
                """;

        // Act
        var respuesta = mockMvc.perform(post(RUTA)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .with(administrador()));

        // Assert
        respuesta.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(AppCodes.Consulta.FILTRO_INVALIDO));
    }

    @Test
    void debeRetornar401_cuandoSinToken() throws Exception {
        // Act
        var respuesta = mockMvc.perform(post(RUTA));

        // Assert
        respuesta.andExpect(status().isUnauthorized());
        verify(consultarUsuariosAdministradorInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar403_cuandoSinClientRole() throws Exception {
        // Act
        var respuesta = mockMvc.perform(post(RUTA)
                .with(SecurityMockMvcRequestPostProcessors.jwt()
                        .authorities(new SimpleGrantedAuthority(UsuariosAuthorities.ESTUDIANTE_ADMINISTRADOR_VIEW))));

        // Assert
        respuesta.andExpect(status().isForbidden());
        verify(consultarUsuariosAdministradorInteractor, never()).ejecutar(any());
    }
}
