package com.arquisoft.fichas.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.estudiante.persistence.EstudianteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EstudianteQueryOutputAdapterTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @InjectMocks
    private EstudianteQueryOutputAdapter adapter;

    @Test
    void debeRetornarTrue_cuandoEstudianteExiste() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        when(estudianteRepository.existsById(estudianteId)).thenReturn(true);

        // Act
        boolean existe = adapter.existePorId(estudianteId);

        // Assert
        assertThat(existe).isTrue();
        verify(estudianteRepository, times(1)).existsById(estudianteId);
    }

    @Test
    void debeRetornarFalse_cuandoEstudianteNoExiste() {
        // Arrange
        UUID estudianteId = UUID.randomUUID();
        when(estudianteRepository.existsById(estudianteId)).thenReturn(false);

        // Act
        boolean existe = adapter.existePorId(estudianteId);

        // Assert
        assertThat(existe).isFalse();
        verify(estudianteRepository, times(1)).existsById(estudianteId);
    }
}
