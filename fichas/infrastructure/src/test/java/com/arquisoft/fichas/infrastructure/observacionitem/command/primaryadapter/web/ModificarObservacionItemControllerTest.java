package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.interactor.ModificarObservacionItemInteractor;
import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.ModificarObservacionItemCommand;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemDuplicadaException;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEncontradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.infrastructure.security.FichasAuthorities;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.logger.AppLoggerConfig;
import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.tracing.infrastructure.traza.config.TrazabilidadConfig;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ModificarObservacionItemController.class)
@Import({AppLoggerConfig.class, GlobalAppExceptionHandler.class, TrazabilidadConfig.class,
        ModificarObservacionItemControllerTest.TestSecurityConfig.class})
class ModificarObservacionItemControllerTest {

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

    private static final String RUTA = "/fichas-perfil/observaciones-item/{observacionItemId}";
    private static final String CUERPO_VALIDO = """
            {"observacion": "El objetivo general no cumple el formato"}
            """;

    private static final UUID OBSERVACION_ITEM_ID = UtilUUID.generarNuevoUUID();
    private static final UUID ASESOR_ID = UtilUUID.generarNuevoUUID();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ModificarObservacionItemInteractor modificarObservacionItemInteractor;

    static Stream<Object[]> reglasDeDominioQueRechazan() {
        return Stream.of(
                new Object[]{new ObservacionItemNoEncontradaException(OBSERVACION_ITEM_ID),
                        FichasCodes.ObservacionItem.NO_ENCONTRADA},
                new Object[]{new RevisionItemCerradaException(UtilUUID.generarNuevoUUID()),
                        FichasCodes.RevisionItem.CERRADA},
                new Object[]{new FichaNoPerteneceAsesorException(UtilUUID.generarNuevoUUID(), ASESOR_ID),
                        FichasCodes.FichaPerfil.FICHA_NO_PERTENECE_ASESOR},
                new Object[]{new ObservacionItemDuplicadaException(UtilUUID.generarNuevoUUID(), "Texto"),
                        FichasCodes.ObservacionItem.OBSERVACION_ITEM_DUPLICADA});
    }

    @Test
    void debe204YEntregarElCommandConElAsesorDelJwt_cuandoPeticionValida() throws Exception {
        // Act
        mockMvc.perform(patch(RUTA, OBSERVACION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "  El objetivo general no cumple el formato  "}
                                """))
                .andExpect(status().isNoContent());

        // Assert
        var captor = ArgumentCaptor.forClass(ModificarObservacionItemCommand.class);
        verify(modificarObservacionItemInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().observacionItem()).isEqualTo(OBSERVACION_ITEM_ID);
        assertThat(captor.getValue().observacion()).isEqualTo("El objetivo general no cumple el formato");
        assertThat(captor.getValue().asesorFicha()).isEqualTo(ASESOR_ID);
    }

    @Test
    void debe400_cuandoObservacionEnBlanco() throws Exception {
        // Act & Assert — Command.crear() rechaza antes de llegar al dominio: 400, no 422
        mockMvc.perform(patch(RUTA, OBSERVACION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"observacion": "   "}
                                """))
                .andExpect(status().isBadRequest());

        verify(modificarObservacionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debe400_cuandoElSubjectDelJwtNoEsUuid() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, OBSERVACION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject("no-es-un-uuid"))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isBadRequest());

        verify(modificarObservacionItemInteractor, never()).ejecutar(any());
    }

    @Test
    void debe401_cuandoNoAutenticado() throws Exception {
        // Act & Assert
        mockMvc.perform(patch(RUTA, OBSERVACION_ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void debe403_cuandoElClientRoleEsElDeOtroEndpoint() throws Exception {
        // Act & Assert — OBSERVACION_ITEM_CREATE protege agregar, no modificar
        mockMvc.perform(patch(RUTA, OBSERVACION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_CREATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isForbidden());

        verify(modificarObservacionItemInteractor, never()).ejecutar(any());
    }

    @ParameterizedTest
    @MethodSource("reglasDeDominioQueRechazan")
    void debe422ConSuCodigo_cuandoUnaReglaDeDominioRechaza(DomainException excepcion, String codigoEsperado)
            throws Exception {
        // Arrange
        doThrow(excepcion).when(modificarObservacionItemInteractor).ejecutar(any());

        // Act & Assert
        mockMvc.perform(patch(RUTA, OBSERVACION_ITEM_ID)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(j -> j.subject(ASESOR_ID.toString()))
                                .authorities(new SimpleGrantedAuthority(FichasAuthorities.OBSERVACION_ITEM_UPDATE)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CUERPO_VALIDO))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(codigoEsperado));
    }
}
