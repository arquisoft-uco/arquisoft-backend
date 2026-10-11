package com.arquisoft.fichas.infrastructure.estadoficha.query.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.fichas.application.estadoficha.query.primaryport.interactor.ConsultarEstadosFichaInteractor;
import com.arquisoft.fichas.application.estadoficha.query.primaryport.model.ConsultarEstadosFichaQuery;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.fichas.infrastructure.security.FichasRoles;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarEstadosFichaController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarEstadosFichaControllerTest.TestSecurityConfig.class})
class ConsultarEstadosFichaControllerTest {

    private static final String RUTA = "/fichas-perfil/estados-ficha";

    @TestConfiguration
    @EnableWebSecurity
    @EnableMethodSecurity(prePostEnabled = true)
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
    private ConsultarEstadosFichaInteractor consultarEstadosFichaInteractor;

    @Test
    void debe200ConLosEstadosDelRol_cuandoElLlamanteEsAsesorFicha() throws Exception {
        // Arrange
        var estados = List.of(
                new EstadoFichaReadModel("DISPONIBLE_PARA_EVALUACION", "Disponible Para Evaluacion", "Lista para evaluar"),
                new EstadoFichaReadModel("EN_CONSTRUCCION", "En Construccion", "Ficha en desarrollo"));
        when(consultarEstadosFichaInteractor.ejecutar(any(ConsultarEstadosFichaQuery.class))).thenReturn(estados);
        var queryCaptor = ArgumentCaptor.forClass(ConsultarEstadosFichaQuery.class);

        // Act
        mockMvc.perform(get(RUTA).with(conRolesRealm(FichasRoles.Realm.ASESOR_FICHA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("DISPONIBLE_PARA_EVALUACION"))
                .andExpect(jsonPath("$[0].nombre").value("Disponible Para Evaluacion"))
                .andExpect(jsonPath("$[0].descripcion").value("Lista para evaluar"))
                .andExpect(jsonPath("$[1].id").value("EN_CONSTRUCCION"));

        // Assert
        verify(consultarEstadosFichaInteractor).ejecutar(queryCaptor.capture());
        assertThat(queryCaptor.getValue().roles()).containsExactly(FichasRoles.Negocio.ASESOR_FICHA);
    }

    @Test
    void debe200ConListaVacia_cuandoElLlamanteNoTieneRolReconocido() throws Exception {
        // Arrange
        when(consultarEstadosFichaInteractor.ejecutar(any(ConsultarEstadosFichaQuery.class))).thenReturn(List.of());
        var queryCaptor = ArgumentCaptor.forClass(ConsultarEstadosFichaQuery.class);

        // Act
        mockMvc.perform(get(RUTA).with(conRolesRealm("estudiante")))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        // Assert
        verify(consultarEstadosFichaInteractor).ejecutar(queryCaptor.capture());
        assertThat(queryCaptor.getValue().roles()).isEmpty();
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA))
                .andExpect(status().isUnauthorized());

        verify(consultarEstadosFichaInteractor, never()).ejecutar(any());
    }

    @Test
    void debe403_cuandoNoTieneElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.claim("realm_access", Map.of("roles", List.of(FichasRoles.Realm.ASESOR_FICHA))))
                                .authorities(new SimpleGrantedAuthority("otro-permiso-incorrecto"))))
                .andExpect(status().isForbidden());

        verify(consultarEstadosFichaInteractor, never()).ejecutar(any());
    }

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor conRolesRealm(String... rolesRealm) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(j -> j.claim("realm_access", Map.of("roles", List.of(rolesRealm))))
                .authorities(new SimpleGrantedAuthority(FichasAuthorities.ESTADO_FICHA_VIEW));
    }
}
