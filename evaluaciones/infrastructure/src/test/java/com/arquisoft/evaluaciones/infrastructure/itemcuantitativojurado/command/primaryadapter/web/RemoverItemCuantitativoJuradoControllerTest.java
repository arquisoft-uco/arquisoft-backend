package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.interactor.RemoverItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RemoverItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoNoEncontradoException;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RemoverItemCuantitativoJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        RemoverItemCuantitativoJuradoControllerTest.TestSecurityConfig.class
})
class RemoverItemCuantitativoJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/items-cuantitativos-jurado/{itemId}";

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
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
    private RemoverItemCuantitativoJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar204YDelegarCommandCorrecto_cuandoPeticionEsValida() throws Exception {
        // Arrange
        var itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(jwtConPermiso()))
                .andExpect(status().isNoContent());

        verify(interactor).ejecutar(RemoverItemCuantitativoJuradoCommand.crear(itemId));
    }

    @Test
    void debeRetornar400_cuandoItemIdNoEsUuid() throws Exception {
        // Arrange
        var itemIdInvalido = "no-es-un-uuid";

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemIdInvalido).with(jwtConPermiso()))
                .andExpect(status().isBadRequest());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar422_cuandoItemNoExiste() throws Exception {
        // Arrange
        var itemId = UUID.randomUUID();
        doThrow(new ItemCuantitativoJuradoNoEncontradoException(itemId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(jwtConPermiso()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ItemCuantitativoJurado.ITEM_NO_ENCONTRADO));
    }

    @Test
    void debeRetornar422_cuandoItemEstaEnUso() throws Exception {
        // Arrange
        var itemId = UUID.randomUUID();
        doThrow(new ItemCuantitativoJuradoEnUsoException(itemId))
                .when(interactor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId).with(jwtConPermiso()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode")
                        .value(EvaluacionesCodes.ItemCuantitativoJurado.ITEM_EN_USO));
    }

    @Test
    void debeRetornar401_cuandoNoEstaAutenticado() throws Exception {
        // Arrange
        var itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId))
                .andExpect(status().isUnauthorized());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeRetornar403_cuandoSoloTienePermisoDeModificar() throws Exception {
        // Arrange
        var itemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete(RUTA, itemId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(
                                        EvaluacionesAuthorities.ITEM_CUANTITATIVO_JURADO_UPDATE))))
                .andExpect(status().isForbidden());
        verify(interactor, never()).ejecutar(any());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermiso() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.ITEM_CUANTITATIVO_JURADO_DELETE));
    }
}
