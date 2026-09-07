package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacionjurado.query.secondaryport.EvaluacionJuradoAccesoQueryOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionJuradoPerteneceEstudianteQueryFinderImplTest {

    @Mock
    private EvaluacionJuradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EvaluacionJuradoPerteneceEstudianteQueryFinderImpl finder;

    @Test
    void debeDelegarConLosIdsDelCriteria_yRetornarTrueCuandoPertenece() {
        // Arrange
        var criteria = new EvaluacionCualitativaJuradoCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(outputPort.perteneceAlEstudiante(criteria.evaluacionJuradoId(), criteria.estudianteId()))
                .thenReturn(true);

        // Act
        Boolean resultado = finder.obtener(criteria);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).perteneceAlEstudiante(criteria.evaluacionJuradoId(), criteria.estudianteId());
    }

    @Test
    void debeRetornarFalse_cuandoElEstudianteNoPertenece() {
        // Arrange
        var criteria = new EvaluacionCualitativaJuradoCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(outputPort.perteneceAlEstudiante(criteria.evaluacionJuradoId(), criteria.estudianteId()))
                .thenReturn(false);

        // Act
        Boolean resultado = finder.obtener(criteria);

        // Assert
        assertThat(resultado).isFalse();
    }
}
