package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.command.secondaryadapter.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaItemCuantitativoJuradoCommandOutputAdapterTest {

    @Mock
    private CategoriaItemCuantitativoJuradoCommandRepository repository;

    @InjectMocks
    private CategoriaItemCuantitativoJuradoCommandOutputAdapter adapter;

    @Test
    void debeRetornarVerdadero_cuandoLaCategoriaExiste() {
        // Arrange
        var categoria = UUID.randomUUID();
        when(repository.existsById(categoria)).thenReturn(true);

        // Act
        var existe = adapter.existePorId(categoria);

        // Assert
        assertThat(existe).isTrue();
        verify(repository).existsById(categoria);
    }

    @Test
    void debeRetornarFalso_cuandoLaCategoriaNoExiste() {
        // Arrange
        var categoria = UUID.randomUUID();
        when(repository.existsById(categoria)).thenReturn(false);

        // Act
        var existe = adapter.existePorId(categoria);

        // Assert
        assertThat(existe).isFalse();
        verify(repository).existsById(categoria);
    }
}
