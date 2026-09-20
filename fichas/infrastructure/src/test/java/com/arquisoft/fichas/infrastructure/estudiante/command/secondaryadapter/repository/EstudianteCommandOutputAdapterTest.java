package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilFecha;
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
class EstudianteCommandOutputAdapterTest {

    @Mock
    private EstudianteCommandRepository estudianteRepository;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private EstudianteCommandOutputAdapter adapter;

    @Test
    void debeGuardarYRegistrarLog_cuandoSePersiste() {
        // Arrange
        var id = UUID.randomUUID();
        var entity = new EstudianteEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), UtilFecha.VACIO);

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

    @Test
    void debeDevolverEliminadosConSuFecha_cuandoObtenerPorIdEncuentraUnaBaja() {
        // Arrange
        var id = UUID.randomUUID();
        var baja = Instant.parse("2026-09-16T10:00:00Z");
        when(estudianteRepository.findById(id)).thenReturn(Optional.of(EstudianteJpaEntity.builder()
                .id(id).identificador("20161020123").nombre("Ana Perez").email("ana@uco.edu.co")
                .ocurridoEn(baja).eliminadoEn(baja).build()));

        // Act
        var resultado = adapter.obtenerPorId(id);

        // Assert
        assertThat(resultado).map(EstudianteEntity::eliminadoEn).contains(baja);
    }

    @Test
    void debeDelegarBusquedaDeVigentes_cuandoBuscaIdsVigentes() {
        // Arrange
        var ids = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        when(estudianteRepository.findIdsVigentesByIdIn(ids)).thenReturn(List.of(ids.get(1)));

        // Act
        var resultado = adapter.buscarIdsVigentes(ids);

        // Assert
        assertThat(resultado).containsExactly(ids.get(1));
    }

    @Test
    void debeActualizarYRegistrarLog_cuandoSeEliminaLogicamente() {
        // Arrange
        var id = UUID.randomUUID();
        var baja = Instant.parse("2026-09-16T10:00:00Z");

        // Act
        adapter.eliminarLogica(id, baja);

        // Assert
        verify(estudianteRepository).eliminarLogica(id, baja);
        verify(logger).debug(EstudianteKey.LOG_ACTUALIZADO, id);
    }

    @Test
    void debeActualizarYRegistrarLog_cuandoSeReactiva() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.parse("2026-09-16T10:00:00Z");
        var entity = new EstudianteEntity(id, "20161020999", "Ana Gomez", "ana.gomez@uco.edu.co", ocurridoEn,
                UtilFecha.VACIO);

        // Act
        adapter.reactivar(entity);

        // Assert
        verify(estudianteRepository).reactivar(id, "20161020999", "Ana Gomez", "ana.gomez@uco.edu.co", ocurridoEn);
        verify(logger).debug(EstudianteKey.LOG_ACTUALIZADO, id);
    }
}
