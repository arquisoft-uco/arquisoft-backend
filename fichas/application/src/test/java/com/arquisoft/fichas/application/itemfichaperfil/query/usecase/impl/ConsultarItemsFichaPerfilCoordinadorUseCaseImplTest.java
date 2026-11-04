package com.arquisoft.fichas.application.itemfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.readmodel.ItemFichaPerfilReadModel;
import com.arquisoft.fichas.application.itemfichaperfil.query.secondaryport.ItemFichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.ItemFichaPerfilKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarItemsFichaPerfilCoordinadorUseCaseImplTest {

    @Mock
    private ItemFichaPerfilQueryOutputPort itemFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarItemsFichaPerfilCoordinadorUseCaseImpl useCase;

    @Test
    void debeRetornarItems_cuandoLaFichaTieneItems() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var criteria = new ItemFichaPerfilCoordinadorCriteria(fichaPerfil);
        var esperado = List.of(
                new ItemFichaPerfilReadModel(UtilUUID.generarNuevoUUID(), fichaPerfil,
                        "OBJETIVO_GENERAL", "Objetivo General", "Contenido A"),
                new ItemFichaPerfilReadModel(UtilUUID.generarNuevoUUID(), fichaPerfil,
                        "OBJETIVO_ESPECIFICO", "Objetivo Especifico", "Contenido B"));
        when(itemFichaPerfilQueryOutputPort.consultarPorFicha(fichaPerfil)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(logger, itemFichaPerfilQueryOutputPort);
        orden.verify(logger).debug(eq(ItemFichaPerfilKey.LOG_CONSULTANDO_COORDINADOR), eq(fichaPerfil));
        orden.verify(itemFichaPerfilQueryOutputPort).consultarPorFicha(fichaPerfil);
        orden.verify(logger).debug(eq(ItemFichaPerfilKey.LOG_CONSULTA_COORDINADOR_COMPLETADA), eq(2));
        verify(itemFichaPerfilQueryOutputPort, times(1)).consultarPorFicha(any());
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayItems() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var criteria = new ItemFichaPerfilCoordinadorCriteria(fichaPerfil);
        when(itemFichaPerfilQueryOutputPort.consultarPorFicha(fichaPerfil)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(ItemFichaPerfilKey.LOG_CONSULTA_COORDINADOR_COMPLETADA), eq(0));
    }
}
