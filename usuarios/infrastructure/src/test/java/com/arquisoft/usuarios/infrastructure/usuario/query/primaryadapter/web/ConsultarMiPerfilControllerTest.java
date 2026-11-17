package com.arquisoft.usuarios.infrastructure.usuario.query.primaryadapter.web;

import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import com.arquisoft.shared.web.identidad.IdentidadActualTestConfig;
import com.arquisoft.usuarios.application.usuario.query.primaryport.interactor.ConsultarMiPerfilInteractor;
import com.arquisoft.usuarios.application.usuario.query.primaryport.model.ConsultarMiPerfilQuery;
import com.arquisoft.usuarios.application.usuario.query.readmodel.EstadoPerfilReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.RolPerfilReadModel;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.infrastructure.usuario.exception.PerfilUsuarioNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarMiPerfilController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarMiPerfilControllerTest.TestSecurityConfig.class})
class ConsultarMiPerfilControllerTest {

    private static final String RUTA = "/usuarios/mi/perfil";

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
    private ConsultarMiPerfilInteractor consultarMiPerfilInteractor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor autenticado(UUID usuario) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(usuario.toString()));
    }

    private static PerfilUsuarioReadModel perfil(UUID id, String identificador) {
        return new PerfilUsuarioReadModel(id, identificador, "Nombre " + identificador, identificador + "@uco.edu.co",
                "573001112233", new EstadoPerfilReadModel("ACTIVO", "Activo"),
                List.of(new RolPerfilReadModel("estudiante", "Estudiante")));
    }

    @Test
    void debe200ConPerfil_cuandoEstaAutenticado() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(consultarMiPerfilInteractor.ejecutar(any())).thenReturn(perfil(usuario, "1001"));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(autenticado(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.toString()))
                .andExpect(jsonPath("$.identificador").value("1001"))
                .andExpect(jsonPath("$.nombre").value("Nombre 1001"))
                .andExpect(jsonPath("$.email").value("1001@uco.edu.co"))
                .andExpect(jsonPath("$.contacto").value("573001112233"))
                .andExpect(jsonPath("$.estado.id").value("ACTIVO"))
                .andExpect(jsonPath("$.estado.nombre").value("Activo"))
                .andExpect(jsonPath("$.roles[0].id").value("estudiante"))
                .andExpect(jsonPath("$.roles[0].nombre").value("Estudiante"));
    }

    @Test
    void debeUsarSoloElIdDelToken_cuandoConsultanDosIdentidades() throws Exception {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        when(consultarMiPerfilInteractor.ejecutar(new ConsultarMiPerfilQuery(primero)))
                .thenReturn(perfil(primero, "1001"));
        when(consultarMiPerfilInteractor.ejecutar(new ConsultarMiPerfilQuery(segundo)))
                .thenReturn(perfil(segundo, "1002"));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(autenticado(primero)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(primero.toString()))
                .andExpect(jsonPath("$.identificador").value("1001"));
        mockMvc.perform(get(RUTA).with(autenticado(segundo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(segundo.toString()))
                .andExpect(jsonPath("$.identificador").value("1002"));
        verify(consultarMiPerfilInteractor).ejecutar(new ConsultarMiPerfilQuery(primero));
        verify(consultarMiPerfilInteractor).ejecutar(new ConsultarMiPerfilQuery(segundo));
    }

    @Test
    void debeIgnorarUsuarioIdDeLaPeticion_cuandoSeEnvia() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var ajeno = UtilUUID.generarNuevoUUID();
        when(consultarMiPerfilInteractor.ejecutar(any())).thenReturn(perfil(usuario, "1001"));

        // Act & Assert
        mockMvc.perform(get(RUTA).param("usuarioId", ajeno.toString()).with(autenticado(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuario.toString()));
        verify(consultarMiPerfilInteractor).ejecutar(new ConsultarMiPerfilQuery(usuario));
        verify(consultarMiPerfilInteractor, never()).ejecutar(new ConsultarMiPerfilQuery(ajeno));
    }

    @Test
    void debe401SinInvocarInteractor_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
        verify(consultarMiPerfilInteractor, never()).ejecutar(any());
    }

    @Test
    void debe422ConSuCodigo_cuandoLaReglaDeDominioRechazaAlUsuario() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(consultarMiPerfilInteractor.ejecutar(any()))
                .thenThrow(new UsuarioNoEncontradoException(usuario));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(autenticado(usuario)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Usuario.NO_ENCONTRADO));
    }

    @Test
    void debe503ConSuCodigo_cuandoElPerfilNoEstaDisponible() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(consultarMiPerfilInteractor.ejecutar(any()))
                .thenThrow(new PerfilUsuarioNoDisponibleException(usuario));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(autenticado(usuario)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorCode").value(UsuariosCodes.Usuario.PERFIL_NO_DISPONIBLE));
    }
}
