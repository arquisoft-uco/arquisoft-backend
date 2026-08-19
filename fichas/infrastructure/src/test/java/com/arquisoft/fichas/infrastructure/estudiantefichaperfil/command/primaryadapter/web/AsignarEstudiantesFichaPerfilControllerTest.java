package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.message.constant.FichasLimits;
import com.arquisoft.fichas.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.interactor.AsignarEstudiantesFichaPerfilInteractor;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.domain.estudiantefichaperfil.exception.CupoEstudiantesExcedidoException;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AsignarEstudiantesFichaPerfilController.class)
@Import({com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        AsignarEstudiantesFichaPerfilControllerTest.TestSecurityConfig.class})
class AsignarEstudiantesFichaPerfilControllerTest {

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
    private AsignarEstudiantesFichaPerfilInteractor asignarEstudiantesFichaPerfilInteractor;

    @Test
    void debe204_cuandoPeticionValidaConListaDeUno() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s"]
                }
                """, estudiante1);
        doNothing().when(asignarEstudiantesFichaPerfilInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void debe204_cuandoPeticionValidaConListaDeTres() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        UUID estudiante3 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s", "%s", "%s"]
                }
                """, estudiante1, estudiante2, estudiante3);
        doNothing().when(asignarEstudiantesFichaPerfilInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNoContent());
    }

    @Test
    void debe400_cuandoListaVacia() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String body = """
                {
                  "estudiantes": []
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoListaTieneMasDeTres() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        UUID estudiante3 = UUID.randomUUID();
        UUID estudiante4 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s", "%s", "%s", "%s"]
                }
                """, estudiante1, estudiante2, estudiante3, estudiante4);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe422_cuandoFichaNoExiste() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s"]
                }
                """, estudiante1);
        doThrow(new FichaPerfilNoEncontradaException(fichaPerfilId))
                .when(asignarEstudiantesFichaPerfilInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoEstudianteNoExiste() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s"]
                }
                """, estudiante1);
        doThrow(new EstudianteNoEncontradoException(estudiante1))
                .when(asignarEstudiantesFichaPerfilInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe422_cuandoEstudianteDuplicadoEnLista() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s"]
                }
                """, estudiante1);
        doThrow(new EstudianteDuplicadoException(estudiante1))
                .when(asignarEstudiantesFichaPerfilInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s"]
                }
                """, estudiante1);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s"]
                }
                """, estudiante1);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:otra-autoridad")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe422_cuandoLimiteExcedido() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantes": ["%s", "%s"]
                }
                """, estudiante1, estudiante2);

        var exception = new CupoEstudiantesExcedidoException(FichasLimits.FichaPerfil.ESTUDIANTES_MAX);

        doThrow(exception).when(asignarEstudiantesFichaPerfilInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTUDIANTE_FICHA_PERFIL_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity());
    }
}
