package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.primaryadapter.web;

import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.interactor.ConsultarItemsFichaPerfilCoordinadorInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilCoordinadorQuery;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarItemsFichaPerfilCoordinadorController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarItemsFichaPerfilCoordinadorControllerTest.TestSecurityConfig.class})
class ConsultarItemsFichaPerfilCoordinadorControllerTest {

    private static final String RUTA = "/fichas-perfil/{fichaPerfilId}/items/coordinador";
    private static final UUID FICHA_ID = UtilUUID.generarNuevoUUID();

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
    private ConsultarItemsFichaPerfilCoordinadorInteractor consultarItemsFichaPerfilCoordinadorInteractor;

    private static SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtConRol(String authority) {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority(authority));
    }

    @Test
    void debeResponder200ConItems() throws Exception {
        // Arrange
        var itemId = UtilUUID.generarNuevoUUID();
        when(consultarItemsFichaPerfilCoordinadorInteractor.ejecutar(any(ConsultarItemsFichaPerfilCoordinadorQuery.class)))
                .thenReturn(List.of(new ItemFichaPerfilReadModel(
                        itemId, FICHA_ID, "OBJETIVO_GENERAL", "Objetivo General", "Contenido")));

        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ITEM_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(itemId.toString()))
                .andExpect(jsonPath("$[0].fichaPerfilId").value(FICHA_ID.toString()))
                .andExpect(jsonPath("$[0].tipoItem").value("OBJETIVO_GENERAL"))
                .andExpect(jsonPath("$[0].tipoItemNombre").value("Objetivo General"))
                .andExpect(jsonPath("$[0].contenido").value("Contenido"));

        var captor = ArgumentCaptor.forClass(ConsultarItemsFichaPerfilCoordinadorQuery.class);
        verify(consultarItemsFichaPerfilCoordinadorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().fichaPerfil()).isEqualTo(FICHA_ID);
    }

    @Test
    void debeResponder200Vacio_cuandoNoHayItems() throws Exception {
        // Arrange
        when(consultarItemsFichaPerfilCoordinadorInteractor.ejecutar(any(ConsultarItemsFichaPerfilCoordinadorQuery.class)))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ITEM_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void debeResponder400_cuandoFichaPerfilIdNoEsUUID() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, "no-es-uuid")
                        .with(jwtConRol(FichasAuthorities.ITEM_FICHA_PERFIL_COORDINADOR_VIEW)))
                .andExpect(status().isBadRequest());

        verify(consultarItemsFichaPerfilCoordinadorInteractor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401_cuandoNoHayToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debeResponder403_cuandoFaltaElClientRole() throws Exception {
        // Act & Assert
        mockMvc.perform(get(RUTA, FICHA_ID)
                        .with(jwtConRol(FichasAuthorities.ITEM_FICHA_PERFIL_ASESOR_VIEW)))
                .andExpect(status().isForbidden());

        verify(consultarItemsFichaPerfilCoordinadorInteractor, never()).ejecutar(any());
    }
}
