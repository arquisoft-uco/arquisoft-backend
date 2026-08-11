package com.arquisoft.fichas.infrastructure.fichaperfil.command.primaryadapter.web;

import com.arquisoft.shared.web.config.CatalogoMensajesConfig;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.command.primaryport.interactor.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.domain.fichaperfil.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarFichaPerfilController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, CatalogoMensajesConfig.class,
        RegistrarFichaPerfilControllerTest.TestSecurityConfig.class})
class RegistrarFichaPerfilControllerTest {

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
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarFichaPerfilInteractor registrarFichaPerfilInteractor;

    @Test
    void debe201_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorId = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título válido",
                  "asesorFicha": "%s"
                }
                """, asesorId);

        when(registrarFichaPerfilInteractor.ejecutar(any())).thenReturn(fichaId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fichaId.toString()));
    }

    @Test
    void debe422_cuandoRequestInvalido() throws Exception {
        // Arrange
        String bodyInvalido = """
                {
                  "tituloProyecto": "",
                  "asesorFicha": null
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyInvalido))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título de prueba",
                  "asesorFicha": "%s"
                }
                """, asesorId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título de prueba",
                  "asesorFicha": "%s"
                }
                """, asesorId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("estudiante:perfil:read")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe400_cuandoTituloDuplicado() throws Exception {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        String tituloDuplicado = "Título duplicado";
        String body = String.format("""
                {
                  "tituloProyecto": "%s",
                  "asesorFicha": "%s"
                }
                """, tituloDuplicado, asesorId);

        when(registrarFichaPerfilInteractor.ejecutar(any()))
                .thenThrow(new FichaTituloDuplicadoException(tituloDuplicado));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoAsesorNoEncontrado() throws Exception {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título de prueba",
                  "asesorFicha": "%s"
                }
                """, asesorId);

        when(registrarFichaPerfilInteractor.ejecutar(any()))
                .thenThrow(new AsesorFichaNoEncontradoException(asesorId));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe201_cuandoPeticionValidaConEstudiantes() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título válido",
                  "asesorFicha": "%s",
                  "estudiantes": ["%s", "%s"]
                }
                """, asesorId, estudiante1, estudiante2);

        when(registrarFichaPerfilInteractor.ejecutar(any())).thenReturn(fichaId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fichaId.toString()));
    }

    @Test
    void debe201_cuandoPeticionValidaSinEstudiantes() throws Exception {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorId = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título válido",
                  "asesorFicha": "%s",
                  "estudiantes": []
                }
                """, asesorId);

        when(registrarFichaPerfilInteractor.ejecutar(any())).thenReturn(fichaId);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(fichaId.toString()));
    }

    @Test
    void debe400_cuandoMasDeTresEstudiantes() throws Exception {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        UUID e1 = UUID.randomUUID();
        UUID e2 = UUID.randomUUID();
        UUID e3 = UUID.randomUUID();
        UUID e4 = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título válido",
                  "asesorFicha": "%s",
                  "estudiantes": ["%s", "%s", "%s", "%s"]
                }
                """, asesorId, e1, e2, e3, e4);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoEstudianteNoExiste() throws Exception {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título válido",
                  "asesorFicha": "%s",
                  "estudiantes": ["%s"]
                }
                """, asesorId, estudianteId);

        when(registrarFichaPerfilInteractor.ejecutar(any()))
                .thenThrow(new EstudianteNoEncontradoException(estudianteId));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoEstudianteDuplicadoEnLista() throws Exception {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();
        String body = String.format("""
                {
                  "tituloProyecto": "Título válido",
                  "asesorFicha": "%s",
                  "estudiantes": ["%s", "%s"]
                }
                """, asesorId, estudianteId, estudianteId);

        when(registrarFichaPerfilInteractor.ejecutar(any()))
                .thenThrow(new EstudianteDuplicadoException(estudianteId));

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
