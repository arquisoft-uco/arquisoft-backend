package com.arquisoft.fichas.infrastructure.tipoitem.query.primaryadapter.web;

import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.fichas.application.tipoitem.query.primaryport.interactor.ConsultarTiposItemInteractor;
import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarTiposItemController.class)
@Import({GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ConsultarTiposItemControllerTest.TestSecurityConfig.class})
class ConsultarTiposItemControllerTest {

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
    private ConsultarTiposItemInteractor consultarTiposItemInteractor;

    @Test
    void debe200ConCatalogo_cuandoConsultaExitosa() throws Exception {
        // Arrange
        List<TipoItemReadModel> tipos = List.of(
                new TipoItemReadModel("OBJETIVO_GENERAL", "Objetivo General", "Proposito principal del proyecto."),
                new TipoItemReadModel("OBJETIVO_ESPECIFICO", "Objetivo Especifico", "Metas concretas y medibles."),
                new TipoItemReadModel("ESTADO_DEL_ARTE", "Estado Del Arte", "Revision de estudios previos."),
                new TipoItemReadModel("ANTECEDENTES", "Antecedentes", "Estudios previos que contextualizan."),
                new TipoItemReadModel("JUSTIFICACION", "Justificacion", "Importancia del proyecto."),
                new TipoItemReadModel("REFERENCIAS", "Referencias", "Fuentes bibliograficas.")
        );
        when(consultarTiposItemInteractor.ejecutar()).thenReturn(tipos);

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/tipos-item")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.TIPO_ITEM_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].id").value("OBJETIVO_GENERAL"))
                .andExpect(jsonPath("$[0].nombre").value("Objetivo General"))
                .andExpect(jsonPath("$[0].descripcion").value("Proposito principal del proyecto."));
    }

    @Test
    void debe200ConListaVacia_cuandoNoHayTipos() throws Exception {
        // Arrange
        when(consultarTiposItemInteractor.ejecutar()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/tipos-item")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.TIPO_ITEM_VIEW))))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void debeRetornarJsonConEstructuraCorrecta_cuandoHayElementos() throws Exception {
        // Arrange
        List<TipoItemReadModel> tipos = List.of(
                new TipoItemReadModel("REFERENCIAS", "Referencias", "Fuentes bibliograficas."),
                new TipoItemReadModel("JUSTIFICACION", "Justificacion", "Importancia del proyecto.")
        );
        when(consultarTiposItemInteractor.ejecutar()).thenReturn(tipos);

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/tipos-item")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.TIPO_ITEM_VIEW))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("REFERENCIAS"))
                .andExpect(jsonPath("$[0].nombre").value("Referencias"))
                .andExpect(jsonPath("$[0].descripcion").value("Fuentes bibliograficas."))
                .andExpect(jsonPath("$[1].id").value("JUSTIFICACION"));
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/tipos-item"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoClientRoleInsuficiente() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/tipos-item")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority("otro-permiso"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void debeInvocarInteractorUnaVez_cuandoEndpointEsLlamado() throws Exception {
        // Arrange
        List<TipoItemReadModel> tipos = List.of(
                new TipoItemReadModel("OBJETIVO_GENERAL", "Objetivo General", "Proposito principal del proyecto.")
        );
        when(consultarTiposItemInteractor.ejecutar()).thenReturn(tipos);

        // Act
        mockMvc.perform(get("/fichas-perfil/tipos-item")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.TIPO_ITEM_VIEW))))
                .andExpect(status().isOk());

        // Assert
        verify(consultarTiposItemInteractor, times(1)).ejecutar();
    }

    @Test
    void debeExigirClientRoleTipoItemView_cuandoAutoriza() throws Exception {
        // Arrange
        List<TipoItemReadModel> tipos = List.of(
                new TipoItemReadModel("ANTECEDENTES", "Antecedentes", "Estudios previos que contextualizan.")
        );
        when(consultarTiposItemInteractor.ejecutar()).thenReturn(tipos);

        // Act & Assert
        mockMvc.perform(get("/fichas-perfil/tipos-item")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.TIPO_ITEM_VIEW))))
                .andExpect(status().isOk());
    }
}
