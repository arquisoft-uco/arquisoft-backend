package com.arquisoft.fichas.infrastructure.asesorficha.query.adapter.out.persistence;

import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaRepository;
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
    private AsesorFichaRepository asesorFichaRepository;

    @InjectMocks
    private AsesorFichaQueryOutputAdapter adapter;

    @Test
    void debeRetornarTrue_cuandoAsesorExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaRepository.existsById(asesorId)).thenReturn(true);

        // Act
        boolean existe = adapter.existePorId(asesorId);

        // Assert
        assertThat(existe).isTrue();
        verify(asesorFichaRepository, times(1)).existsById(asesorId);
    }

    @Test
    void debeRetornarFalse_cuandoAsesorNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaRepository.existsById(asesorId)).thenReturn(false);

        // Act
        boolean existe = adapter.existePorId(asesorId);

        // Assert
        assertThat(existe).isFalse();
        verify(asesorFichaRepository, times(1)).existsById(asesorId);
    }
}
