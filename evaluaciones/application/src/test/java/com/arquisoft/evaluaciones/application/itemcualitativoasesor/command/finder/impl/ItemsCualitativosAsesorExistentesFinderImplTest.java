package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.ItemCualitativoAsesorOutputPort;
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
class ItemsCualitativosAsesorExistentesFinderImplTest {

    @Mock
    private ItemCualitativoAsesorOutputPort outputPort;

    @InjectMocks
    private ItemsCualitativosAsesorExistentesFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaIdsExistentes() {
        // Arrange
        var item = UtilUUID.generarNuevoUUID();
        var solicitados = Set.of(item, UtilUUID.generarNuevoUUID());
        when(outputPort.consultarIdsExistentes(solicitados)).thenReturn(Set.of(item));

        // Act
        var resultado = finder.obtener(solicitados);

        // Assert
        assertThat(resultado).containsExactly(item);
        verify(outputPort).consultarIdsExistentes(solicitados);
    }
}
