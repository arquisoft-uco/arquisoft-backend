package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCuantitativoAsesorExisteFinderImplTest {

    @Mock
    private ItemCuantitativoAsesorOutputPort outputPort;

    @InjectMocks
    private ItemCuantitativoAsesorExisteFinderImpl finder;

    @Test
    void debeRetornarResultadoDelPuerto_cuandoConsultaExistenciaPorId() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        when(outputPort.existePorId(item)).thenReturn(true);

        // Act
        var resultado = finder.obtener(item);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existePorId(item);
    }
}
