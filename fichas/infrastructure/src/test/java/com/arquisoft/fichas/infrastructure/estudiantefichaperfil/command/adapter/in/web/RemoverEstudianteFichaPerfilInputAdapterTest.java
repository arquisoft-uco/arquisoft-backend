package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web;

import com.arquisoft.fichas.application.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.port.in.RemoverEstudianteFichaPerfilInputPort;
import com.arquisoft.fichas.application.estudiantefichaperfil.exception.EstudianteFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.web.exception.GlobalAppExceptionHandler;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverEstudianteFichaPerfilInputAdapter.class)
@Import({GlobalAppExceptionHandler.class,
        RemoverEstudianteFichaPerfilInputAdapterTest.TestSecurityConfig.class})
class RemoverEstudianteFichaPerfilInputAdapterTest {

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
    private RemoverEstudianteFichaPerfilInputPort inputPort;

    @Test
    void debe204_cuandoPeticionValida() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        doNothing().when(inputPort).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}",
                        fichaPerfilId, estudianteId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:delete"))))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        ArgumentCaptor<RemoverEstudianteFichaPerfilCommand> captor =
                ArgumentCaptor.forClass(RemoverEstudianteFichaPerfilCommand.class);
        verify(inputPort, times(1)).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfilId()).isEqualTo(fichaPerfilId);
        assertThat(captor.getValue().estudianteId()).isEqualTo(estudianteId);
    }

    @Test
    void debe400_cuandoFichaNoExiste() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        doThrow(new FichaPerfilNoEncontradaException(fichaPerfilId))
                .when(inputPort)
                .ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}",
                        fichaPerfilId, estudianteId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:delete"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoEstudianteNoExiste() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        doThrow(new EstudianteNoEncontradoException(estudianteId))
                .when(inputPort)
                .ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}",
                        fichaPerfilId, estudianteId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:delete"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoRelacionNoExiste() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        doThrow(new EstudianteFichaPerfilNoEncontradoException(estudianteId, fichaPerfilId))
                .when(inputPort)
                .ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}",
                        fichaPerfilId, estudianteId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:delete"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}",
                        fichaPerfilId, estudianteId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoRolInsuficiente() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        UUID estudianteId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}",
                        fichaPerfilId, estudianteId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:otra-autoridad"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debe400_cuandoFichaPerfilIdInvalido() throws Exception {
        // Arrange
        String fichaPerfilIdInvalido = "no-es-un-uuid";
        UUID estudianteId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}",
                        fichaPerfilIdInvalido, estudianteId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:delete"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void debe400_cuandoEstudianteIdInvalido() throws Exception {
        // Arrange
        UUID fichaPerfilId = UUID.randomUUID();
        String estudianteIdInvalido = "no-es-un-uuid";

        // Act & Assert
        mockMvc.perform(delete("/fichas-perfil/{fichaPerfilId}/estudiantes/{estudianteId}",
                        fichaPerfilId, estudianteIdInvalido)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("fichas:estudiante-ficha-perfil:delete"))))
                .andExpect(status().isBadRequest());
    }

}
