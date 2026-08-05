package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilRepository;
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
    private EstadoEvaluacionFichaRepository estadoEvaluacionFichaRepository;

    @Mock
    private EvaluacionFichaPerfilRepository evaluacionFichaPerfilRepository;

    @Mock
    private EstadoEvaluacionRepository estadoEvaluacionRepository;

    private EstadoEvaluacionFichaCommandOutputAdapter adapter;
    private EstadoEvaluacionFichaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new EstadoEvaluacionFichaMapper(estadoEvaluacionRepository);
        adapter = new EstadoEvaluacionFichaCommandOutputAdapter(
                estadoEvaluacionFichaRepository,
                mapper,
                evaluacionFichaPerfilRepository,
                estadoEvaluacionRepository);
    }

    @Test
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var aggregate = EstadoEvaluacionFichaDomain.crear(evaluacionId);

        var evaluacionJpa = EvaluacionFichaPerfilEntity.builder()
                .id(evaluacionId)
                .build();
        when(evaluacionFichaPerfilRepository.getReferenceById(evaluacionId))
                .thenReturn(evaluacionJpa);

        var estadoJpa = new EstadoEvaluacionEntity("EN_EVALUACION", "En Evaluación", "");
        when(estadoEvaluacionRepository.getReferenceById("EN_EVALUACION"))
                .thenReturn(estadoJpa);

        EstadoEvaluacionFichaEntity entityGuardada = EstadoEvaluacionFichaEntity.builder()
                .id(aggregate.getId())
                .evaluacionFichaPerfil(evaluacionJpa)
                .estadoEvaluacion(estadoJpa)
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();

        when(estadoEvaluacionFichaRepository.save(any(EstadoEvaluacionFichaEntity.class)))
                .thenReturn(entityGuardada);

        // Act
        adapter.registrarEstadoInicial(aggregate);

        // Assert
        verify(estadoEvaluacionFichaRepository).save(any(EstadoEvaluacionFichaEntity.class));
    }

    @Test
    void debeReconstruirConReconstruir_cuandoFindByIdExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID evaluacionId = UUID.randomUUID();

        var evaluacionJpa = EvaluacionFichaPerfilEntity.builder()
                .id(evaluacionId)
                .build();

        var estadoJpa = new EstadoEvaluacionEntity("APROBADA", "Aprobada", "");

        EstadoEvaluacionFichaEntity entity = EstadoEvaluacionFichaEntity.builder()
                .id(id)
                .evaluacionFichaPerfil(evaluacionJpa)
                .estadoEvaluacion(estadoJpa)
                .fechaActualizacion(Instant.now())
                .build();

        when(estadoEvaluacionFichaRepository.findById(id)).thenReturn(Optional.of(entity));

        // Act
        var resultado = estadoEvaluacionFichaRepository.findById(id);
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

        when(estadoEvaluacionFichaRepository
                .existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(evaluacionId, "EN_EVALUACION"))
                .thenReturn(true);

        // Act
        boolean existe = adapter.existePorEvaluacionYEstado(evaluacionId, "EN_EVALUACION");

        // Assert
        assertThat(existe).isTrue();
        verify(estadoEvaluacionFichaRepository)
                .existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(evaluacionId, "EN_EVALUACION");
    }

    @Test
    void debeContarEstados_cuandoContarEstadosPorEvaluacion() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();

        when(estadoEvaluacionFichaRepository.countByEvaluacionFichaPerfilId(evaluacionId))
                .thenReturn(2L);

        // Act
        long count = adapter.contarEstadosPorEvaluacion(evaluacionId);

        // Assert
        assertThat(count).isEqualTo(2);
        verify(estadoEvaluacionFichaRepository).countByEvaluacionFichaPerfilId(evaluacionId);
    }
}
