package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.ItemCualitativoJuradoOutputPort;
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
class ItemsCualitativosJuradoExistentesFinderImplTest {

    @Mock
    private ItemCualitativoJuradoOutputPort outputPort;

    @InjectMocks
    private ItemsCualitativosJuradoExistentesFinderImpl finder;

    @Test
    void debeDelegarEnPuerto_cuandoConsultaIdsExistentes() {
        // Arrange
        UUID item = UUID.randomUUID();
        Set<UUID> solicitados = Set.of(item);
        when(outputPort.consultarIdsExistentes(solicitados)).thenReturn(Set.of(item));

        // Act
        Set<UUID> resultado = finder.obtener(solicitados);

        // Assert
        assertThat(resultado).containsExactly(item);
        verify(outputPort).consultarIdsExistentes(solicitados);
    }
}
