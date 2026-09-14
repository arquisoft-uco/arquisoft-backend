package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.entity.ItemCuantitativoJuradoEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCuantitativoJuradoPorIdFinderImplTest {

    @Mock
    private ItemCuantitativoJuradoOutputPort outputPort;

    @InjectMocks
    private ItemCuantitativoJuradoPorIdFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoElItemExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        var entity = new ItemCuantitativoJuradoEntity(id, "Rigor", "Descripción", UUID.randomUUID(), 500);
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(id);
        assertThat(resultado.get().getValor()).isEqualTo(500);
    }

    @Test
    void debeRetornarVacio_cuandoElItemNoExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(id)).isEmpty();
    }
}
