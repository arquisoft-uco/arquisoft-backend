package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.interactor.RemoverItemCualitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RemoverItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverItemCualitativoAsesorController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RemoverItemCualitativoAsesorControllerTest.TestSecurityConfig.class
})
class RemoverItemCualitativoAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/items-cualitativos-asesor/{itemId}";

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
    private RemoverItemCualitativoAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204YDelegarCommandCorrecto_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isNoContent());

        verify(interactor).ejecutar(RemoverItemCualitativoAsesorCommand.crear(itemId));
    }

    @Test
    void debeRetornar400_cuandoItemIdNoEsUuid() throws Exception {
        // Arrange
        var itemIdInvalido = "no-es-un-uuid";

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemIdInvalido).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoItemNoExiste() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();
        doThrow(new ItemCualitativoAsesorNoEncontradoException(itemId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ItemCualitativoAsesor.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debeRetornar422_cuandoItemEstaEnUso() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();
        doThrow(new ItemCualitativoAsesorEnUsoException(itemId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ItemCualitativoAsesor.ITEM_EN_USO));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }
}
