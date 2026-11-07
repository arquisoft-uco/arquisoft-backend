package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.RegistrarItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RegistrarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.CategoriaItemCuantitativoAsesorNoEncontradaException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrarItemCuantitativoAsesorController.class)
@Import({
        AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RegistrarItemCuantitativoAsesorControllerTest.TestSecurityConfig.class
})
class RegistrarItemCuantitativoAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/items-cuantitativos-asesor";

    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .exceptionHandling(exception -> exception
                            .authenticationEntryPoint((request, response, error) ->
                                    response.sendError(401, "Unauthorized"))
                            .accessDeniedHandler((request, response, error) ->
                                    response.sendError(403, "Forbidden")));
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarItemCuantitativoAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar201ConId_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var categoria = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any())).thenReturn(id);

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(categoria.toString(), 80)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()));
        var captor = ArgumentCaptor.forClass(RegistrarItemCuantitativoAsesorCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().nombre()).isEqualTo("Avance");
        assertThat(captor.getValue().categoria()).isEqualTo(categoria);
        assertThat(captor.getValue().valor()).isEqualTo(80);
    }

    @Test
    void debeRetornar400ConFieldErrors_cuandoDatosInvalidos() throws Exception {
        // Arrange
        var body = """
                {
                  "nombre": " ",
                  "descripcion": "",
                  "categoria": "no-es-uuid",
                  "valor": 501
                }
                """;

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()").value(4));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(UtilUUID.generarNuevoUUID().toString(), 80)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar422_cuandoCategoriaNoExiste() throws Exception {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        when(interactor.ejecutar(any()))
                .thenThrow(new CategoriaItemCuantitativoAsesorNoEncontradaException(categoria));

        // Act & Assert
        mockMvc.perform(post(RUTA)
                        .with(jwtValido())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(categoria.toString(), 80)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(
                        EvaluacionesCodes.ItemCuantitativoAsesor.CATEGORIA_NO_ENCONTRADA));
    }

    private static String cuerpo(String categoria, int valor) {
        return """
                {
                  "nombre": "Avance",
                  "descripcion": "Porcentaje de avance",
                  "categoria": "%s",
                  "valor": %d
                }
                """.formatted(categoria, valor);
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtValido() {
        return SecurityMockMvcRequestPostProcessors.jwt();
    }
}
