package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.interactor.DescargarDocumentacionInteractor;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.DescargarDocumentacionQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;
import com.arquisoft.biblioteca.application.documentacion.query.readmodel.EnlaceDescargaDocumentacionReadModel;
import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoDescargableException;
import com.arquisoft.biblioteca.infrastructure.documentacion.exception.ArchivoDocumentacionAusenteException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.BibliotecaCodes;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DescargarDocumentacionController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, DescargarDocumentacionControllerTest.TestSecurityConfig.class})
class DescargarDocumentacionControllerTest {

    private static final String RUTA = "/biblioteca/documentaciones/{id}/descarga";

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
                    .accessDeniedHandler((req, res, e)    -> res.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DescargarDocumentacionInteractor descargarDocumentacionInteractor;

    @Test
    void debeResponder200ConNoStoreYPasarLosRolesDelToken_cuandoSeGeneraElEnlace() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        var expiraEn = Instant.parse("2026-11-14T12:00:00Z");
        when(descargarDocumentacionInteractor.ejecutar(any(DescargarDocumentacionQuery.class)))
                .thenReturn(new EnlaceDescargaDocumentacionReadModel("https://minio.test/firmado", expiraEn));
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(sujeto).claim("roles", List.of("estudiante")))))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CACHE_CONTROL, "no-store"))
                .andExpect(jsonPath("$.url").value("https://minio.test/firmado"))
                .andExpect(jsonPath("$.expiraEn").exists());

        var captor = ArgumentCaptor.forClass(DescargarDocumentacionQuery.class);
        verify(descargarDocumentacionInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().documentacion()).isEqualTo(documentacion);
        assertThat(captor.getValue().roles()).containsExactly(RolConsultaDocumentacion.ESTUDIANTE);
    }

    @Test
    void debeResponder400SinInvocarElInteractor_cuandoElIdentificadorEstaMalFormado() throws Exception {
        // Arrange
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, "abc")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isBadRequest());
        verify(descargarDocumentacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401SinInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, UtilUUID.generarNuevoUUID()))
                .andExpect(status().isUnauthorized());
        verify(descargarDocumentacionInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder422ConElCodigoDeNoDescargable_cuandoLaReglaDeDominioRechaza() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(descargarDocumentacionInteractor.ejecutar(any(DescargarDocumentacionQuery.class)))
                .thenThrow(new DocumentacionNoDescargableException(documentacion));
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(BibliotecaCodes.Documentacion.NO_DESCARGABLE));
    }

    @Test
    void debeResponder503ConElCodigoDeArchivoAusente_cuandoElObjetoNoEstaEnElAlmacenamiento() throws Exception {
        // Arrange
        var documentacion = UtilUUID.generarNuevoUUID();
        when(descargarDocumentacionInteractor.ejecutar(any(DescargarDocumentacionQuery.class)))
                .thenThrow(new ArchivoDocumentacionAusenteException(documentacion, "entregables", "p/a.zip"));
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(get(RUTA, documentacion)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorCode").value(BibliotecaCodes.Documentacion.ARCHIVO_AUSENTE));
    }
}
