package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorFichaCommandOutputAdapterTest {

    @Mock
    private AsesorFichaRepository asesorFichaRepository;

    @InjectMocks
    private AsesorFichaCommandOutputAdapter adapter;

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

    @Test
    void debeRetornarElContacto_cuandoAsesorExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        var entity = AsesorFichaEntity.builder()
                .id(asesorId)
                .identificador("A001")
                .nombre("Ana Asesora")
                .email("ana@arquisoft.com")
                .build();
        when(asesorFichaRepository.findById(asesorId)).thenReturn(Optional.of(entity));

        // Act
        var resultado = adapter.buscarContactoPorId(asesorId);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(asesorId);
        assertThat(resultado.get().nombre()).isEqualTo("Ana Asesora");
        assertThat(resultado.get().email()).isEqualTo("ana@arquisoft.com");
    }

    @Test
    void debeRetornarVacio_cuandoAsesorNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaRepository.findById(asesorId)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.buscarContactoPorId(asesorId);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
