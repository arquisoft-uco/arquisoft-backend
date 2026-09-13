package com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.CoordinadorKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordinadorFichasCommandOutputAdapterTest {

    @Mock
    private CoordinadorFichasCommandRepository coordinadorCommandRepository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CoordinadorFichasCommandOutputAdapter adapter;

    @Test
    void debeGuardarConOcurridoEn_cuandoSePersisteLaReplica() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var entity = new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        adapter.guardar(entity);

        // Assert
        verify(coordinadorCommandRepository, times(1)).save(any(CoordinadorJpaEntity.class));
        verify(logger).debug(CoordinadorKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarPresente_cuandoObtenerPorIdEncuentraLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var jpaEntity = CoordinadorJpaEntity.builder()
                .id(id)
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .ocurridoEn(ocurridoEn)
                .build();
        when(coordinadorCommandRepository.findById(id)).thenReturn(Optional.of(jpaEntity));

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().ocurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeRetornarVacio_cuandoObtenerPorIdNoEncuentraLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        when(coordinadorCommandRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
