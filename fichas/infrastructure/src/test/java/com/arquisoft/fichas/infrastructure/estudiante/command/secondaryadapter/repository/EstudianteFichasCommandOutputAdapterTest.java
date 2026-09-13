package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteFichasCommandOutputAdapterTest {

    @Mock
    private EstudianteFichasCommandRepository estudianteRepository;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private EstudianteFichasCommandOutputAdapter adapter;

    @Test
    void debeGuardarYRegistrarLog_cuandoSePersiste() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

        // Act
        adapter.guardar(entity);

        // Assert
        verify(estudianteRepository).save(any());
        verify(logger).debug(EstudianteKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarTrue_cuandoExistePorId() {
        // Arrange
        var id = UUID.randomUUID();
        when(estudianteRepository.existsById(id)).thenReturn(true);

        // Act
        var resultado = adapter.existePorId(id);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarEntidadesMapeadas_cuandoBuscaPorIds() {
        // Arrange
        var id = UUID.randomUUID();
        var jpaEntity = EstudianteJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez")
                .email("ana@uco.edu.co").ocurridoEn(Instant.now()).build();
        when(estudianteRepository.findAllById(List.of(id))).thenReturn(List.of(jpaEntity));

        // Act
        var resultado = adapter.buscarPorIds(List.of(id));

        // Assert
        assertThat(resultado).extracting(EstudianteEntity::id).containsExactly(id);
    }

    @Test
    void debeRetornarVacio_cuandoObtenerPorIdNoEncuentra() {
        // Arrange
        var id = UUID.randomUUID();
        when(estudianteRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
