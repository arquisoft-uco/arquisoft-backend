package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
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
class ValoresMaximosItemsCuantitativosJuradoFinderImplTest {

    @Mock
    private ItemCuantitativoJuradoOutputPort outputPort;

    @InjectMocks
    private ValoresMaximosItemsCuantitativosJuradoFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaValoresMaximosPorIds() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var ids = Set.of(item);
        var valores = Map.of(item, 100);
        when(outputPort.consultarValoresMaximosPorIds(ids)).thenReturn(valores);

        // Act
        var resultado = finder.obtener(ids);

        // Assert
        assertThat(resultado).isSameAs(valores);
        verify(outputPort).consultarValoresMaximosPorIds(ids);
    }
}
