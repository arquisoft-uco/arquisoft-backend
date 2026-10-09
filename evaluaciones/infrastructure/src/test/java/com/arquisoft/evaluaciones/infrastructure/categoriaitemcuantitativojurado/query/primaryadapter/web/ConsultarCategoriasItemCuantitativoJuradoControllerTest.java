package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.primaryport.interactor.ConsultarCategoriasItemCuantitativoJuradoInteractor;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.query.readmodel.CategoriaItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.security.EvaluacionesAuthorities;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
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

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarCategoriasItemCuantitativoJuradoController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarCategoriasItemCuantitativoJuradoControllerTest.TestSecurityConfig.class
})
class ConsultarCategoriasItemCuantitativoJuradoControllerTest {

    private static final String RUTA = "/evaluaciones/categorias-item-cuantitativo-jurado";

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
    private ConsultarCategoriasItemCuantitativoJuradoInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar200ConLosCamposSerializadosEnOrden_cuandoTieneElClientRoleView() throws Exception {
        // Arrange
        var idPuntualidad = UUID.randomUUID();
        var idRigor = UUID.randomUUID();
        List<CategoriaItemCuantitativoJuradoReadModel> categorias = List.of(
                new CategoriaItemCuantitativoJuradoReadModel(idPuntualidad, "Puntualidad", "Evalúa la puntualidad"),
                new CategoriaItemCuantitativoJuradoReadModel(idRigor, "Rigor", "Evalúa el rigor")
        );
        when(interactor.ejecutar(any())).thenReturn(categorias);

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(idPuntualidad.toString()))
                .andExpect(jsonPath("$[0].nombre").value("Puntualidad"))
                .andExpect(jsonPath("$[0].descripcion").value("Evalúa la puntualidad"))
                .andExpect(jsonPath("$[1].id").value(idRigor.toString()))
                .andExpect(jsonPath("$[1].nombre").value("Rigor"))
                .andExpect(jsonPath("$[1].descripcion").value("Evalúa el rigor"));
    }

    @Test
    void debeRetornar200ConListaVacia_cuandoElInteractorRetornaVacio() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debePasarElNombreDelRequestParam_alInteractor() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA).param("nombre", "Puntualidad").with(jwtConPermisoView()))
                .andExpect(status().isOk());

        // Assert
        verify(interactor).ejecutar(argThat(query -> "Puntualidad".equals(query.nombre())));
    }

    @Test
    void debeRetornar400_cuandoElNombreDelFiltroExcedeLongitudMaxima() throws Exception {
        // Arrange
        var nombreDemasiadoLargo = "n".repeat(101);

        // Act & Assert
        mockMvc.perform(get(RUTA).param("nombre", nombreDemasiadoLargo).with(jwtConPermisoView()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value(EvaluacionesFields.CategoriaItemCuantitativoJurado.NOMBRE));
    }

    @Test
    void debeRetornar401_cuandoNoHayJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoJwtSinAuthorities() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA).with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeDelegarUnaSolaVez_enElInteractor() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(List.of());

        // Act
        mockMvc.perform(get(RUTA).with(jwtConPermisoView()))
                .andExpect(status().isOk());

        // Assert
        verify(interactor, times(1)).ejecutar(any());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermisoView() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.CATEGORIA_ITEM_CUANTITATIVO_JURADO_VIEW));
    }
}
