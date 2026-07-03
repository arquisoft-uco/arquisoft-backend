package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.AsignarEstudiantesFichaPerfilInputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
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

@WebMvcTest(AsignarEstudiantesFichaPerfilInputAdapter.class)
@Import({GlobalAppExceptionHandler.class,
        AsignarEstudiantesFichaPerfilInputAdapterTest.TestSecurityConfig.class})
class AsignarEstudiantesFichaPerfilInputAdapterTest {

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
    private AsignarEstudiantesFichaPerfilInputPort asignarEstudiantesFichaPerfilInputPort;

    @Test
    void debe201_cuandoPeticionValidaConListaDeUno() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantesIds": ["%s"]
                }
                """, estudiante1);
        doNothing().when(asignarEstudiantesFichaPerfilInputPort).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:create")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void debe201_cuandoPeticionValidaConListaDeTres() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        UUID estudiante2 = UUID.randomUUID();
        UUID estudiante3 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantesIds": ["%s", "%s", "%s"]
                }
                """, estudiante1, estudiante2, estudiante3);
        doNothing().when(asignarEstudiantesFichaPerfilInputPort).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:create")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void debe400_cuandoListaVacia() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String body = """
                {
                  "estudiantesIds": []
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:create")))
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
                  "estudiantesIds": ["%s", "%s", "%s", "%s"]
                }
                """, estudiante1, estudiante2, estudiante3, estudiante4);

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:create")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoFichaNoExiste() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantesIds": ["%s"]
                }
                """, estudiante1);
        doThrow(new FichaPerfilNoEncontradaException(fichaPerfilId))
                .when(asignarEstudiantesFichaPerfilInputPort).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:create")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoEstudianteNoExiste() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantesIds": ["%s"]
                }
                """, estudiante1);
        doThrow(new EstudianteNoEncontradoException(estudiante1))
                .when(asignarEstudiantesFichaPerfilInputPort).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:create")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoEstudianteDuplicadoEnLista() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantesIds": ["%s"]
                }
                """, estudiante1);
        doThrow(new EstudianteDuplicadoException(estudiante1))
                .when(asignarEstudiantesFichaPerfilInputPort).ejecutar(any());

        // Act & Assert
        mockMvc.perform(post("/fichas-perfil/{fichaPerfilId}/estudiantes", fichaPerfilId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:create")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudiante1 = UUID.randomUUID();
        String body = String.format("""
                {
                  "estudiantesIds": ["%s"]
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
                  "estudiantesIds": ["%s"]
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
}
