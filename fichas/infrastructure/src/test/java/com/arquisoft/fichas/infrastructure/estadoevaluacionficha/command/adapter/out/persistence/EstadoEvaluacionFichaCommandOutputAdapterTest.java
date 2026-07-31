package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionJpaRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoEvaluacionFichaCommandOutputAdapterTest {

    @Mock
    private EstadoEvaluacionFichaJpaRepository estadoEvaluacionFichaJpaRepository;

    @Mock
    private EvaluacionFichaPerfilJpaRepository evaluacionFichaPerfilJpaRepository;

    @Mock
    private EstadoEvaluacionJpaRepository estadoEvaluacionJpaRepository;

    private EstadoEvaluacionFichaCommandOutputAdapter adapter;
    private EstadoEvaluacionFichaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EstadoEvaluacionFichaMapper(estadoEvaluacionJpaRepository);
        adapter = new EstadoEvaluacionFichaCommandOutputAdapter(
                estadoEvaluacionFichaJpaRepository,
                mapper,
                evaluacionFichaPerfilJpaRepository,
                estadoEvaluacionJpaRepository);
    }

    @Test
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var aggregate = EstadoEvaluacionFichaAggregate.crear(evaluacionId);

        var evaluacionJpa = EvaluacionFichaPerfilJpaEntity.builder()
                .id(evaluacionId)
                .build();
        when(evaluacionFichaPerfilJpaRepository.getReferenceById(evaluacionId))
                .thenReturn(evaluacionJpa);

        var estadoJpa = new EstadoEvaluacionJpaEntity("EN_EVALUACION", "En Evaluación", "");
        when(estadoEvaluacionJpaRepository.getReferenceById("EN_EVALUACION"))
                .thenReturn(estadoJpa);

        EstadoEvaluacionFichaJpaEntity entityGuardada = EstadoEvaluacionFichaJpaEntity.builder()
                .id(aggregate.getId())
                .evaluacionFichaPerfil(evaluacionJpa)
                .estadoEvaluacion(estadoJpa)
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();

        when(estadoEvaluacionFichaJpaRepository.save(any(EstadoEvaluacionFichaJpaEntity.class)))
                .thenReturn(entityGuardada);

        // Act
        adapter.guardar(aggregate);

        // Assert
        verify(estadoEvaluacionFichaJpaRepository).save(any(EstadoEvaluacionFichaJpaEntity.class));
    }

    @Test
    void debeReconstruirConReconstruir_cuandoFindByIdExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID evaluacionId = UUID.randomUUID();

        var evaluacionJpa = EvaluacionFichaPerfilJpaEntity.builder()
                .id(evaluacionId)
                .build();

        var estadoJpa = new EstadoEvaluacionJpaEntity("APROBADA", "Aprobada", "");

        EstadoEvaluacionFichaJpaEntity entity = EstadoEvaluacionFichaJpaEntity.builder()
                .id(id)
                .evaluacionFichaPerfil(evaluacionJpa)
                .estadoEvaluacion(estadoJpa)
                .fechaActualizacion(Instant.now())
                .build();

        when(estadoEvaluacionFichaJpaRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = estadoEvaluacionFichaJpaRepository.findById(id);
        assertThat(resultado).isPresent();

        var aggregateReconstruido = mapper.toDomain(resultado.get());

        // Assert
        assertThat(aggregateReconstruido.getId()).isEqualTo(id);
        assertThat(aggregateReconstruido.getEstadoEvaluacion()).isEqualTo(EstadoEvaluacion.APROBADA);
    }

    @Test
    void debeRetornarTrue_cuandoExisteByEvaluacionAndEstado() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();

        when(estadoEvaluacionFichaJpaRepository
                .existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(evaluacionId, "EN_EVALUACION"))
                .thenReturn(true);

        // Act
        boolean existe = adapter.existePorEvaluacionYEstado(evaluacionId, "EN_EVALUACION");

        // Assert
        assertThat(existe).isTrue();
        verify(estadoEvaluacionFichaJpaRepository)
                .existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(evaluacionId, "EN_EVALUACION");
    }

    @Test
    void debeContarEstados_cuandoContarEstadosPorEvaluacion() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();

        when(estadoEvaluacionFichaJpaRepository.countByEvaluacionFichaPerfilId(evaluacionId))
                .thenReturn(2L);

        // Act
        long count = adapter.contarEstadosPorEvaluacion(evaluacionId);

        // Assert
        assertThat(count).isEqualTo(2);
        verify(estadoEvaluacionFichaJpaRepository).countByEvaluacionFichaPerfilId(evaluacionId);
    }
}
