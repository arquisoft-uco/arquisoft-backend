package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValoresMaximosItemsCuantitativosAsesorFinderImplTest {

    @Mock
    private ItemCuantitativoAsesorOutputPort outputPort;

    @InjectMocks
    private ValoresMaximosItemsCuantitativosAsesorFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaValoresMaximos() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var ids = Set.of(item, UtilUUID.generarNuevoUUID());
        when(outputPort.consultarValoresMaximosPorIds(ids)).thenReturn(Map.of(item, 40));

        // Act
        var resultado = finder.obtener(ids);

        // Assert
        assertThat(resultado).containsExactly(Map.entry(item, 40));
        verify(outputPort).consultarValoresMaximosPorIds(ids);
    }
}
