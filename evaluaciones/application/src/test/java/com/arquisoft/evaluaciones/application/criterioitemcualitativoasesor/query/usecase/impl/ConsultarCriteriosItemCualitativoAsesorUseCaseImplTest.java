package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.secondaryport.CriterioItemCualitativoAsesorQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CriterioItemCualitativoAsesorKey;
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
class ConsultarCriteriosItemCualitativoAsesorUseCaseImplTest {

    @Mock
    private CriterioItemCualitativoAsesorQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarCriteriosItemCualitativoAsesorUseCaseImpl useCase;

    @Test
    void debeRetornarCriterios_cuandoExisten() {
        // Arrange
        var esperados = List.of(
                new CriterioItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Actitud", "Evalúa la actitud"),
                new CriterioItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Ortografía", "Evalúa la ortografía"),
                new CriterioItemCualitativoAsesorReadModel(UtilUUID.generarNuevoUUID(), "Redacción", "Evalúa la redacción"));
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultarTodos();
        verify(logger).debug(eq(CriterioItemCualitativoAsesorKey.LOG_CONSULTA_COMPLETADA), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayCriterios() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar();

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(CriterioItemCualitativoAsesorKey.LOG_CONSULTA_COMPLETADA), eq(0));
    }
}
