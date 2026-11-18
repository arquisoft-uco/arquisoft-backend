package com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web;

import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.interactor.DescargarPaqueteEntregableInteractor;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.DescargarPaqueteEntregableQuery;
import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.RolConsultaPaqueteEntregable;
import com.arquisoft.entregables.application.paqueteentregable.query.readmodel.EnlaceDescargaPaqueteEntregableReadModel;
import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableNoDisponibleException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.EntregablesCodes;
import com.arquisoft.shared.message.constant.UsuariosRoles;
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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DescargarPaqueteEntregableController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        DescargarPaqueteEntregableControllerTest.TestSecurityConfig.class})
class DescargarPaqueteEntregableControllerTest {

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
    private DescargarPaqueteEntregableInteractor interactor;

    private static String ruta(String entregable) {
        return "/entregables/" + entregable + "/paquete";
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt(UUID actor, String rol) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.subject(actor.toString()).claim("roles", List.of(rol)));
    }

    @Test
    void debeResponder200_cuandoEsValido() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var expira = Instant.parse("2026-11-08T12:00:00Z");
        when(interactor.ejecutar(any(DescargarPaqueteEntregableQuery.class)))
                .thenReturn(new EnlaceDescargaPaqueteEntregableReadModel("https://minio/firmado", expira));

        // Act & Assert
        mockMvc.perform(get(ruta(entregable.toString())).with(jwt(actor, UsuariosRoles.ESTUDIANTE)))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("no-store")))
                .andExpect(jsonPath("$.url").value("https://minio/firmado"))
                .andExpect(jsonPath("$.expiraEn").exists());
        var captor = ArgumentCaptor.forClass(DescargarPaqueteEntregableQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().actor()).isEqualTo(actor);
        assertThat(captor.getValue().roles()).containsExactly(RolConsultaPaqueteEntregable.ESTUDIANTE);
    }

    @Test
    void debeResponder400YNoInvocarElInteractor_cuandoIdMalFormado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta("no-es-uuid")).with(jwt(UtilUUID.generarNuevoUUID(), UsuariosRoles.ASESOR)))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any(DescargarPaqueteEntregableQuery.class));
    }

    @Test
    void debeResponder400_cuandoElTokenNoTraeRolesReconocidos() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID().toString()))
                        .with(jwt(UtilUUID.generarNuevoUUID(), "otro")))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any(DescargarPaqueteEntregableQuery.class));
    }

    @Test
    void debeResponder401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any(DescargarPaqueteEntregableQuery.class));
    }

    @Test
    void debeResponder422_cuandoElInteractorLanzaDominio() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(DescargarPaqueteEntregableQuery.class)))
                .thenThrow(new PaqueteEntregableNoDisponibleException(entregable));

        // Act & Assert
        mockMvc.perform(get(ruta(entregable.toString())).with(jwt(UtilUUID.generarNuevoUUID(), UsuariosRoles.ASESOR)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(EntregablesCodes.PaqueteEntregable.NO_DISPONIBLE));
    }
}
