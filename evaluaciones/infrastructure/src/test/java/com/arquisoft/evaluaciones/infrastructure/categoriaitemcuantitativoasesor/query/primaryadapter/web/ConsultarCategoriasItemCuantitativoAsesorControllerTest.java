package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.primaryport.interactor.ConsultarCategoriasItemCuantitativoAsesorInteractor;
import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.query.readmodel.CategoriaItemCuantitativoAsesorReadModel;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarCategoriasItemCuantitativoAsesorController.class)
@Import({
        com.arquisoft.shared.logger.AppLoggerConfig.class,
        GlobalAppExceptionHandler.class,
        ConsultarCategoriasItemCuantitativoAsesorControllerTest.TestSecurityConfig.class
})
class ConsultarCategoriasItemCuantitativoAsesorControllerTest {

    private static final String RUTA = "/evaluaciones/categorias-item-cuantitativo-asesor";

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
    private ConsultarCategoriasItemCuantitativoAsesorInteractor interactor;

    @MockitoBean
    private GestorTraza gestorTraza;

    @Test
    void debeRetornar200ConLosCamposSerializadosEnOrden_cuandoTieneElClientRoleView() throws Exception {
        // Arrange
        var idPuntualidad = UUID.randomUUID();
        var idRigor = UUID.randomUUID();
        List<CategoriaItemCuantitativoAsesorReadModel> categorias = List.of(
                new CategoriaItemCuantitativoAsesorReadModel(idPuntualidad, "Puntualidad", "Evalúa la puntualidad"),
                new CategoriaItemCuantitativoAsesorReadModel(idRigor, "Rigor", "Evalúa el rigor")
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
                        .value(EvaluacionesFields.CategoriaItemCuantitativoAsesor.NOMBRE));
    }

    @Test
    void debeRetornar401_cuandoNoHayJwt() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeRetornar403_cuandoTieneElClientRoleDelJurado() throws Exception {
        // Arrange
        var jwtJurado = SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.CATEGORIA_ITEM_CUANTITATIVO_JURADO_VIEW));

        // Act & Assert
        mockMvc.perform(get(RUTA).with(jwtJurado))
                .andExpect(status().isForbidden());
        verifyNoInteractions(interactor);
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConPermisoView() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(
                        EvaluacionesAuthorities.CATEGORIA_ITEM_CUANTITATIVO_ASESOR_VIEW));
    }
}
