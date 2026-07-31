package com.arquisoft.fichas.infrastructure.asesorficha.query.adapter.out.persistence;

import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaRepository;
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
class AsesorFichaQueryOutputAdapterTest {

    @Mock
    private AsesorFichaJpaRepository asesorFichaJpaRepository;

    @InjectMocks
    private AsesorFichaQueryOutputAdapter adapter;

    @Test
    void debeRetornarTrue_cuandoAsesorExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaJpaRepository.existsById(asesorId)).thenReturn(true);

        // Act
        boolean existe = adapter.existePorId(asesorId);

        // Assert
        assertThat(existe).isTrue();
        verify(asesorFichaJpaRepository, times(1)).existsById(asesorId);
    }

    @Test
    void debeRetornarFalse_cuandoAsesorNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaJpaRepository.existsById(asesorId)).thenReturn(false);

        // Act
        boolean existe = adapter.existePorId(asesorId);

        // Assert
        assertThat(existe).isFalse();
        verify(asesorFichaJpaRepository, times(1)).existsById(asesorId);
    }
}
