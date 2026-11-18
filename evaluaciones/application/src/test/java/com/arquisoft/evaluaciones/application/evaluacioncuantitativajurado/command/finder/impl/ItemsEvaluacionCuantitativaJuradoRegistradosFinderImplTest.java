package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.model.ItemsPorEvaluacionCuantitativaJurado;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemsEvaluacionCuantitativaJuradoRegistradosFinderImplTest {

    @Mock
    private EvaluacionCuantitativaJuradoOutputPort outputPort;

    @InjectMocks
    private ItemsEvaluacionCuantitativaJuradoRegistradosFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaItemsYaRegistrados() {
        // Arrange
        var evaluacionJurado = UtilUUID.generarNuevoUUID();
        var itemRegistrado = UtilUUID.generarNuevoUUID();
        var items = Set.of(itemRegistrado, UtilUUID.generarNuevoUUID());
        when(outputPort.consultarItemsRegistrados(evaluacionJurado, items)).thenReturn(Set.of(itemRegistrado));

        // Act
        var resultado = finder.obtener(new ItemsPorEvaluacionCuantitativaJurado(evaluacionJurado, items));

        // Assert
        assertThat(resultado).containsExactly(itemRegistrado);
        verify(outputPort).consultarItemsRegistrados(evaluacionJurado, items);
    }
}
