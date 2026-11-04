package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.OmisionEvaluacionesCualitativasJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionesCualitativasJuradoDeEvaluacionFinderImplTest {

    @Mock
    private EvaluacionCualitativaJuradoOutputPort outputPort;

    @InjectMocks
    private EvaluacionesCualitativasJuradoDeEvaluacionFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaLasEvaluacionesDeLaEvaluacionJurado() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var perteneciente = UUID.randomUUID();
        var ajena = UUID.randomUUID();
        var omision = OmisionEvaluacionesCualitativasJuradoDomain.crear(evaluacionJurado, List.of(perteneciente, ajena));
        when(outputPort.consultarIdsPorEvaluacionJurado(evaluacionJurado, Set.of(perteneciente, ajena)))
                .thenReturn(Set.of(perteneciente));

        // Act
        var resultado = finder.obtener(omision);

        // Assert
        assertThat(resultado).containsExactly(perteneciente);
        verify(outputPort).consultarIdsPorEvaluacionJurado(evaluacionJurado, Set.of(perteneciente, ajena));
    }
}
