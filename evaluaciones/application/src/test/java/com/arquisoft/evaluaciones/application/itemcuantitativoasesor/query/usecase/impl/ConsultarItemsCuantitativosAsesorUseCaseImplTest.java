package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.secondaryport.ItemCuantitativoAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ItemCuantitativoAsesorKey;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarItemsCuantitativosAsesorUseCaseImplTest {

    @Mock
    private ItemCuantitativoAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarItemsCuantitativosAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarItems_cuandoExisten() {
        // Arrange
        var esperados = List.of(
                new ItemCuantitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Puntualidad",
                        "Evalúa la puntualidad", UtilUUID.generarNuevoUUID(), 50),
                new ItemCuantitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Presentación",
                        "Evalúa la presentación", UtilUUID.generarNuevoUUID(), 30),
                new ItemCuantitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Vestimenta",
                        "Evalúa la vestimenta", UtilUUID.generarNuevoUUID(), 20));
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultarTodos();
        verify(logger).debug(any(ItemCuantitativoAsesorKey.class), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayItems() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(any(ItemCuantitativoAsesorKey.class), eq(0));
    }
}
