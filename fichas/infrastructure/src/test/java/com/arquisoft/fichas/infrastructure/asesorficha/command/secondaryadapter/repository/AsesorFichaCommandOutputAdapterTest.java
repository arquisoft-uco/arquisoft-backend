package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
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
class AsesorFichaCommandOutputAdapterTest {

    @Mock
    private AsesorFichaCommandRepository asesorFichaRepository;

    @Mock
    private AppLogger logger;

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
        var entity = AsesorFichaJpaEntity.builder()
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
        assertThat(resultado.get().identificador()).isEqualTo("A001");
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

    @Test
    void debeGuardarConOcurridoEn_cuandoSePersisteLaReplica() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var entity = new AsesorFichaEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        adapter.guardar(entity);

        // Assert
        verify(asesorFichaRepository, times(1)).save(any(AsesorFichaJpaEntity.class));
        verify(logger).debug(AsesorFichaKey.LOG_GUARDADO, id);
    }

    @Test
    void debeRetornarPresente_cuandoObtenerPorIdEncuentraLaFila() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var jpaEntity = AsesorFichaJpaEntity.builder()
                .id(id)
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .ocurridoEn(ocurridoEn)
                .build();
        when(asesorFichaRepository.findById(id)).thenReturn(Optional.of(jpaEntity));

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
        when(asesorFichaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeGuardarLaEntidadActualizada_cuandoSeInvocaActualizar() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var entity = new AsesorFichaEntity(id, "20161020999", "Ana Actualizada", "actualizada@uco.edu.co",
                ocurridoEn);

        // Act
        adapter.actualizar(entity);

        // Assert
        verify(asesorFichaRepository, times(1)).save(any(AsesorFichaJpaEntity.class));
        verify(logger).debug(AsesorFichaKey.LOG_ACTUALIZADO, id);
    }
}
