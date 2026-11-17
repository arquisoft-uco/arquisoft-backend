package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.interactor.ConsultarEntregablesDisponiblesInteractor;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesDisponiblesQuery;
import com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel.EntregableDisponibleReadModel;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.BibliotecaFields;
import com.arquisoft.shared.query.pagination.PaginatedResult;
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
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

@WebMvcTest(ConsultarEntregablesDisponiblesController.class)
@Import({IdentidadActualTestConfig.class, AppLoggerConfig.class, GlobalAppExceptionHandler.class,
        TrazabilidadConfig.class, ConsultarEntregablesDisponiblesControllerTest.TestSecurityConfig.class})
class ConsultarEntregablesDisponiblesControllerTest {

    private static final String RUTA = "/biblioteca/entregables-proyecto-grado/consultar";

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
    private ConsultarEntregablesDisponiblesInteractor consultarEntregablesDisponiblesInteractor;

    @Test
    void debeRetornar200ConAutoresYTituloNulo_cuandoLaFilaNoTieneProyecto() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var autor = UtilUUID.generarNuevoUUID();
        var conAutores = new EntregableDisponibleReadModel(id, proyecto, "Sistema", 1, "entregable-v1.zip",
                List.of(new EntregableDisponibleReadModel.AutorResumen(autor, "AUT-1", "Ana")));
        var sinProyecto = new EntregableDisponibleReadModel(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), null, 2, null, List.of());
        when(consultarEntregablesDisponiblesInteractor.ejecutar(any(ConsultarEntregablesDisponiblesQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(conAutores, sinProyecto), 0, 10, 2L));
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                .andExpect(jsonPath("$.content[0].proyectoGrado").value(proyecto.toString()))
                .andExpect(jsonPath("$.content[0].tituloProyecto").value("Sistema"))
                .andExpect(jsonPath("$.content[0].versionEntregable").value(1))
                .andExpect(jsonPath("$.content[0].nombreArchivo").value("entregable-v1.zip"))
                .andExpect(jsonPath("$.content[0].autores[0].id").value(autor.toString()))
                .andExpect(jsonPath("$.content[0].autores[0].identificador").value("AUT-1"))
                .andExpect(jsonPath("$.content[0].autores[0].nombre").value("Ana"))
                .andExpect(jsonPath("$.content[1].tituloProyecto").hasJsonPath())
                .andExpect(jsonPath("$.content[1].tituloProyecto").value((Object) null))
                .andExpect(jsonPath("$.content[1].autores").isArray())
                .andExpect(jsonPath("$.content[1].autores").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void debeRetornar200ConValoresPorDefecto_cuandoNoHayBody() throws Exception {
        // Arrange
        when(consultarEntregablesDisponiblesInteractor.ejecutar(any(ConsultarEntregablesDisponiblesQuery.class)))
                .thenReturn(PaginatedResult.of(List.of(), 0, 10, 0L));
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));

        var captor = ArgumentCaptor.forClass(ConsultarEntregablesDisponiblesQuery.class);
        verify(consultarEntregablesDisponiblesInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().criterio().pagina()).isZero();
        assertThat(captor.getValue().criterio().tamanio()).isEqualTo(10);
        assertThat(captor.getValue().titulo()).isEmpty();
    }

    @Test
    void debeRetornar400SinInvocarElInteractor_cuandoElTituloExcede100() throws Exception {
        // Arrange
        var sujeto = UtilUUID.generarNuevoUUID().toString();
        var cuerpo = "{\"titulo\":\"" + "a".repeat(101) + "\"}";

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo)
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field").value(BibliotecaFields.ProyectoGrado.TITULO));
        verify(consultarEntregablesDisponiblesInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar400_cuandoTamanioEsInvalido() throws Exception {
        // Arrange
        var sujeto = UtilUUID.generarNuevoUUID().toString();

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tamanio\":0}")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(j -> j.subject(sujeto))))
                .andExpect(status().isBadRequest());
        verify(consultarEntregablesDisponiblesInteractor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar401SinInvocarElInteractor_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        verify(consultarEntregablesDisponiblesInteractor, never()).ejecutar(any());
    }
}
