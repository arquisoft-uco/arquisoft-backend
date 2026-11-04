package com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.secondaryport.ItemCualitativoAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCualitativoAsesorKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarItemsCualitativosAsesorUseCaseImplTest {

    @Mock
    private ItemCualitativoAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarItemsCualitativosAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarItems_cuandoExisten() {
        // Arrange
        var esperados = List.of(
                new ItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Actitud", "Evalúa la actitud"),
                new ItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Ortografía", "Evalúa la ortografía"),
                new ItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Redacción", "Evalúa la redacción"));
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultarTodos();
        verify(logger).debug(eq(ItemCualitativoAsesorKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayItems() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(ItemCualitativoAsesorKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
