package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.interactor.RemoverItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RemoverItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorNoEncontradoException;
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

@WebMvcTest(RemoverItemCuantitativoAsesorController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RemoverItemCuantitativoAsesorControllerTest.TestSecurityConfig.class
})
class RemoverItemCuantitativoAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/items-cuantitativos-asesor/{itemId}";

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
    private RemoverItemCuantitativoAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204YDelegarCommandCorrecto_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isNoContent());

        verify(interactor).ejecutar(RemoverItemCuantitativoAsesorCommand.crear(itemId));
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
        doThrow(new ItemCuantitativoAsesorNoEncontradoException(itemId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debeRetornar422_cuandoItemEstaEnUso() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();
        doThrow(new ItemCuantitativoAsesorEnUsoException(itemId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ItemCuantitativoAsesor.ITEM_EN_USO));
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
