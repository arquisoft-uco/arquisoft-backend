package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarUsuarioPorCorreoInteractor;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarUsuarioPorCorreoQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.UsuarioPorCorreoReadModel;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarUsuarioPorCorreoController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarUsuarioPorCorreoControllerTest.TestSecurityConfig.class})
class ConsultarUsuarioPorCorreoControllerTest {

    private static final String RUTA = "/usuarios/por-correo";

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
    private ConsultarUsuarioPorCorreoInteractor consultarUsuarioPorCorreoInteractor;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor servicio() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }

    @Test
    void debeResponder200() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(consultarUsuarioPorCorreoInteractor.ejecutar(any())).thenReturn(new UsuarioPorCorreoReadModel(usuario));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(servicio()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"ana@uco.edu.co\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.toString()));
        verify(consultarUsuarioPorCorreoInteractor).ejecutar(new ConsultarUsuarioPorCorreoQuery("ana@uco.edu.co"));
    }

    @Test
    void debeResponder400_cuandoCorreoInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).with(servicio()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"no-es-correo\"}"))
                .andExpect(status().isBadRequest());
        verify(consultarUsuarioPorCorreoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content("{\"correo\":\"ana@uco.edu.co\"}"))
                .andExpect(status().isUnauthorized());
        verify(consultarUsuarioPorCorreoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422_cuandoNoExiste() throws Exception {
        // Arrange
        when(consultarUsuarioPorCorreoInteractor.ejecutar(any()))
                .thenThrow(new UsuarioNoEncontradoException(UtilUUID.obtenerUUIDPorDefecto()));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(servicio()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"ana@uco.edu.co\"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Usuario.NO_ENCONTRADO));
    }

    @Test
    void debeResponder400SinConsultar_cuandoElCuerpoNoTraeCorreo() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).with(servicio()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value(UsuariosFields.Usuario.CORREO));
        verify(consultarUsuarioPorCorreoInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRecortarElCorreo_cuandoLlegaConEspacios() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(consultarUsuarioPorCorreoInteractor.ejecutar(any())).thenReturn(new UsuarioPorCorreoReadModel(usuario));

        // Act & Assert
        mockMvc.perform(post(RUTA).with(servicio()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"correo\":\"  ana@uco.edu.co  \"}"))
                .andExpect(status().isOk());
        verify(consultarUsuarioPorCorreoInteractor).ejecutar(new ConsultarUsuarioPorCorreoQuery("ana@uco.edu.co"));
    }
}
