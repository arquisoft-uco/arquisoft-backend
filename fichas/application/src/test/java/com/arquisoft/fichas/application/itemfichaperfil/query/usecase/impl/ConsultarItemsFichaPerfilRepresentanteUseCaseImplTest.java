package com.arquisoft.fichas.application.itemfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport.ItemFichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarItemsFichaPerfilRepresentanteUseCaseImplTest {

    @Mock
    private ItemFichaPerfilQueryOutputPort itemFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarItemsFichaPerfilRepresentanteUseCaseImpl useCase;

    @Test
    void debeDelegarEnPuertoConLosUuidDelCriteria_yRetornarSuResultado() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var representanteComite = UUID.randomUUID();
        var criteria = new ItemFichaPerfilRepresentanteCriteria(fichaPerfil, representanteComite);
        var esperado = List.of(new ItemFichaPerfilReadModel(
                UUID.randomUUID(), fichaPerfil, "OBJETIVO_GENERAL", "Objetivo General", "Contenido"));
        when(itemFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(fichaPerfil, representanteComite))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(itemFichaPerfilQueryOutputPort).consultarPorFichaYRepresentante(fichaPerfil, representanteComite);
    }

    @Test
    void debeDevolverListaVacia_cuandoPuertoNoDevuelveNada() {
        // Arrange
        var criteria = new ItemFichaPerfilRepresentanteCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(itemFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(any(), any()))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRegistrarDebugEntradaYCierre_sinInfo() {
        // Arrange
        var criteria = new ItemFichaPerfilRepresentanteCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(itemFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(any(), any()))
                .thenReturn(List.of());

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(ItemFichaPerfilKey.LOG_CONSULTANDO_REPRESENTANTE), eq(criteria.fichaPerfil()));
        verify(logger).debug(eq(ItemFichaPerfilKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA), eq(0));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
