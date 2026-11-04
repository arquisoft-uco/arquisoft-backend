package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.model.ItemsPorEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
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
class ItemsEvaluacionCuantitativaAsesorRegistradosFinderImplTest {

    @Mock
    private EvaluacionCuantitativaAsesorOutputPort outputPort;

    @InjectMocks
    private ItemsEvaluacionCuantitativaAsesorRegistradosFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaItemsRegistrados() {
        // Arrange
        var evaluacionAsesor = UtilUUID.generarNuevoUUID();
        var item = UtilUUID.generarNuevoUUID();
        var items = Set.of(item, UtilUUID.generarNuevoUUID());
        when(outputPort.consultarItemsRegistrados(evaluacionAsesor, items)).thenReturn(Set.of(item));

        // Act
        var resultado = finder.obtener(new ItemsPorEvaluacionCuantitativaAsesor(evaluacionAsesor, items));

        // Assert
        assertThat(resultado).containsExactly(item);
        verify(outputPort).consultarItemsRegistrados(evaluacionAsesor, items);
    }
}
