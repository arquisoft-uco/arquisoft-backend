package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.secondaryport.CriterioItemCualitativoJuradoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCriteriosItemCualitativoJuradoUseCaseImplTest {

    @Mock
    private CriterioItemCualitativoJuradoQueryOutputPort queryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarCriteriosItemCualitativoJuradoUseCaseImpl useCase;

    @Test
    void debeRetornarTodosLosReadModel_cuandoElPuertoLosEntrega() {
        // Arrange
        List<CriterioItemCualitativoJuradoReadModel> esperados = List.of(
                new CriterioItemCualitativoJuradoReadModel(UUID.randomUUID(), "Claridad", "Evalúa la claridad conceptual"),
                new CriterioItemCualitativoJuradoReadModel(UUID.randomUUID(), "Rigor", "Evalúa el rigor metodológico")
        );
        when(queryOutputPort.consultarTodos()).thenReturn(esperados);

        // Act
        List<CriterioItemCualitativoJuradoReadModel> resultado = useCase.ejecutar(null);

        // Assert
        assertThat(resultado).containsExactlyElementsOf(esperados);
        verify(queryOutputPort, times(1)).consultarTodos();
        verifyNoMoreInteractions(queryOutputPort);
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoEncuentraRegistros() {
        // Arrange
        when(queryOutputPort.consultarTodos()).thenReturn(List.of());

        // Act
        List<CriterioItemCualitativoJuradoReadModel> resultado = useCase.ejecutar(null);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
