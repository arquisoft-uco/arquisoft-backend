package com.arquisoft.evaluaciones.application.evaluacionjurado.query.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
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
class EvaluacionJuradoPerteneceEstudianteCuantitativaQueryFinderImplTest {

    @Mock
    private EvaluacionJuradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private EvaluacionJuradoPerteneceEstudianteCuantitativaQueryFinderImpl finder;

    @Test
    void debeDelegarConLosIdsDelCriteria_yRetornarTrueCuandoPertenece() {
        // Arrange
        var criteria = new EvaluacionCuantitativaJuradoCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(outputPort.perteneceAlEstudiante(criteria.evaluacionJuradoId(), criteria.estudianteId()))
                .thenReturn(true);

        // Act
        var resultado = finder.obtener(criteria);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).perteneceAlEstudiante(criteria.evaluacionJuradoId(), criteria.estudianteId());
    }

    @Test
    void debeRetornarFalse_cuandoElEstudianteNoPertenece() {
        // Arrange
        var criteria = new EvaluacionCuantitativaJuradoCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(outputPort.perteneceAlEstudiante(criteria.evaluacionJuradoId(), criteria.estudianteId()))
                .thenReturn(false);

        // Act
        var resultado = finder.obtener(criteria);

        // Assert
        assertThat(resultado).isFalse();
    }
}
