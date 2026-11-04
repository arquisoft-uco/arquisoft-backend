package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web;

import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.interactor.ConsultarArtefactosEntregableInteractor;
import com.arquisoft.entregables.application.artefactoentregable.query.primaryport.model.ConsultarArtefactosEntregableQuery;
import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.entregables.domain.entregableproyectogrado.exception.AsesorNoPropietarioEntregableProyectoGradoException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.EntregablesCodes;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarArtefactosEntregableController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class,
        GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarArtefactosEntregableControllerTest.TestSecurityConfig.class})
class ConsultarArtefactosEntregableControllerTest {

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
    private ConsultarArtefactosEntregableInteractor interactor;

    private static String ruta(String entregable) {
        return "/entregables-proyecto-grado/" + entregable + "/artefactos";
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt(UUID asesor) {
        return SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(asesor.toString()));
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt() {
        return jwt(UtilUUID.generarNuevoUUID());
    }

    @Test
    void debeResponder200ConLosArtefactos() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var readModel = new ArtefactoEntregableReadModel(id, entregable, UtilUUID.generarNuevoUUID(), "uri",
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Doc", UtilUUID.generarNuevoUUID(), "Repo");
        var asesor = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarArtefactosEntregableQuery.class))).thenReturn(List.of(readModel));

        // Act & Assert
        mockMvc.perform(get(ruta(entregable.toString())).with(jwt(asesor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].entregableProyectoGrado").value(entregable.toString()))
                .andExpect(jsonPath("$[0].nombreDocumento").value("Doc"))
                .andExpect(jsonPath("$[0].nombreRepositorio").value("Repo"))
                .andExpect(jsonPath("$[0].uriArchivo").value("uri"));
        var captor = ArgumentCaptor.forClass(ConsultarArtefactosEntregableQuery.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
    }

    @Test
    void debeResponder200Vacio_cuandoNoHayArtefactos() throws Exception {
        // Arrange
        when(interactor.ejecutar(any(ConsultarArtefactosEntregableQuery.class))).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID().toString())).with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeResponder400YNoInvocarElInteractor_cuandoElIdNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta("no-es-uuid")).with(jwt()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any(ConsultarArtefactosEntregableQuery.class));
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(ruta(UtilUUID.generarNuevoUUID().toString())))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any(ConsultarArtefactosEntregableQuery.class));
    }

    @Test
    void debeResponder422_cuandoElEntregableNoEsDelAsesor() throws Exception {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any(ConsultarArtefactosEntregableQuery.class)))
                .thenThrow(new AsesorNoPropietarioEntregableProyectoGradoException(
                        UtilUUID.generarNuevoUUID(), entregable));

        // Act & Assert
        mockMvc.perform(get(ruta(entregable.toString())).with(jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EntregablesCodes.EntregableProyectoGrado.ASESOR_NO_PROPIETARIO));
    }
}
