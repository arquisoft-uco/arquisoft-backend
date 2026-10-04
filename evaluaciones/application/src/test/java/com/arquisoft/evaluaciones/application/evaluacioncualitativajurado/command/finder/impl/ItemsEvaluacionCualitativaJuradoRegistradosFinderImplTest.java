package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.model.CriterioItemsEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemsEvaluacionCualitativaJuradoRegistradosFinderImplTest {

    @Mock
    private EvaluacionCualitativaJuradoOutputPort outputPort;

    @InjectMocks
    private ItemsEvaluacionCualitativaJuradoRegistradosFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaItemsYaRegistrados() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID itemRegistrado = UUID.randomUUID();
        Set<UUID> items = Set.of(itemRegistrado, UUID.randomUUID());
        var criterio = new CriterioItemsEvaluacion(evaluacionJurado, items);
        when(outputPort.consultarItemsRegistrados(evaluacionJurado, items)).thenReturn(Set.of(itemRegistrado));

        // Act
        Set<UUID> resultado = finder.obtener(criterio);

        // Assert
        assertThat(resultado).containsExactly(itemRegistrado);
        verify(outputPort).consultarItemsRegistrados(evaluacionJurado, items);
    }
}
