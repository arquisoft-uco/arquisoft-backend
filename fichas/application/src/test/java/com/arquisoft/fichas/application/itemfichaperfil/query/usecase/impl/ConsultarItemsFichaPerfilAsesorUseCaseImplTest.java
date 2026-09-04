package com.arquisoft.fichas.application.itemfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilCriteria;
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
class ConsultarItemsFichaPerfilAsesorUseCaseImplTest {

    @Mock
    private ItemFichaPerfilQueryOutputPort itemFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarItemsFichaPerfilAsesorUseCaseImpl useCase;

    @Test
    void debeDelegarEnPuertoConLosUuidDelCriteria_yRetornarSuResultado() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var asesorFicha = UUID.randomUUID();
        var criteria = new ItemFichaPerfilCriteria(fichaPerfil, asesorFicha);
        var esperado = List.of(new ItemFichaPerfilReadModel(
                UUID.randomUUID(), fichaPerfil, "OBJETIVO_GENERAL", "Objetivo General", "Contenido"));
        when(itemFichaPerfilQueryOutputPort.consultarPorFichaYAsesor(fichaPerfil, asesorFicha))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(itemFichaPerfilQueryOutputPort).consultarPorFichaYAsesor(fichaPerfil, asesorFicha);
    }

    @Test
    void debeDevolverListaVacia_cuandoPuertoNoDevuelveNada() {
        // Arrange
        var criteria = new ItemFichaPerfilCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(itemFichaPerfilQueryOutputPort.consultarPorFichaYAsesor(any(), any()))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRegistrarDebugEntradaYCierre_sinInfo() {
        // Arrange
        var criteria = new ItemFichaPerfilCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(itemFichaPerfilQueryOutputPort.consultarPorFichaYAsesor(any(), any()))
                .thenReturn(List.of());

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(logger).debug(eq(ItemFichaPerfilKey.LOG_CONSULTANDO_ASESOR), eq(criteria.fichaPerfil()));
        verify(logger).debug(eq(ItemFichaPerfilKey.LOG_CONSULTA_ASESOR_COMPLETADA), eq(0));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
