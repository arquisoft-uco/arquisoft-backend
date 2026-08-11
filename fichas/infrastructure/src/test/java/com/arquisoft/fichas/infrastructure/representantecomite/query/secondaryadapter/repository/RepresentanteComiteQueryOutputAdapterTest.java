package com.arquisoft.fichas.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.representantecomite.persistence.RepresentanteComiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RepresentanteComiteQueryOutputAdapterTest {

    @Mock
    private RepresentanteComiteRepository repository;

    private RepresentanteComiteQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RepresentanteComiteQueryOutputAdapter(repository);
    }

    @Test
    void debeRetornarBooleanoSegunExistencia_cuandoExistsByIdEsInvocado() {
        // Arrange
        UUID idExistente = UUID.randomUUID();
        UUID idNoExistente = UUID.randomUUID();

        when(repository.existsById(idExistente)).thenReturn(true);
        when(repository.existsById(idNoExistente)).thenReturn(false);

        // Act
        boolean existeTrue = adapter.existePorId(idExistente);
        boolean existeFalse = adapter.existePorId(idNoExistente);

        // Assert
        assertThat(existeTrue).isTrue();
        assertThat(existeFalse).isFalse();
        verify(repository).existsById(idExistente);
        verify(repository).existsById(idNoExistente);
    }
}
