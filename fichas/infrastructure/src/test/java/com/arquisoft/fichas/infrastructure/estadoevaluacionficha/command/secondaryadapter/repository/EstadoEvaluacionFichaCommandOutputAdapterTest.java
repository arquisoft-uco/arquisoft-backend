package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.entity.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.repository.EstadoEvaluacionCommandRepository;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity.EstadoEvaluacionFichaEntity;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.mapper.EstadoEvaluacionFichaJpaMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoEvaluacionFichaCommandOutputAdapterTest {

    @Mock
    private EstadoEvaluacionFichaCommandRepository estadoEvaluacionFichaRepository;

    @Mock
    private EstadoEvaluacionCommandRepository estadoEvaluacionRepository;

    private EstadoEvaluacionFichaCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstadoEvaluacionFichaCommandOutputAdapter(
                estadoEvaluacionFichaRepository,
                estadoEvaluacionRepository);
    }

    @Test
    void debeMapearYGuardarLaEntidadComoJpaEntity_cuandoRegistraElEstadoInicial() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var aggregate = EstadoEvaluacionFichaDomain.crear(evaluacionId);

        EstadoEvaluacionFichaEntity entity = EstadoEvaluacionFichaMapper.toEntity(aggregate);

        // Act
        adapter.registrarEstadoInicial(entity);

        // Assert
        verify(estadoEvaluacionFichaRepository).save(argThat(jpaEntity ->
                jpaEntity.getId().equals(entity.id())
                        && jpaEntity.getEvaluacionFichaPerfil().getId().equals(evaluacionId)
                        && jpaEntity.getEstadoEvaluacion().getId().equals(EstadoEvaluacion.EN_EVALUACION.getId())));
    }

    @Test
    void debeReconstruirConReconstruir_cuandoFindByIdExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID evaluacionId = UUID.randomUUID();

        var evaluacionJpa = EvaluacionFichaPerfilJpaEntity.builder()
                .id(evaluacionId)
                .build();

        var estadoJpa = EstadoEvaluacionJpaEntity.builder()
                .id("APROBADA")
                .nombre("Aprobada")
                .descripcion("")
                .build();

        EstadoEvaluacionFichaJpaEntity jpaEntity = EstadoEvaluacionFichaJpaEntity.builder()
                .id(id)
                .evaluacionFichaPerfil(evaluacionJpa)
                .estadoEvaluacion(estadoJpa)
                .fechaActualizacion(Instant.now())
                .build();

        when(estadoEvaluacionFichaRepository.findById(id)).thenReturn(Optional.of(jpaEntity));

        // Act
        var resultado = estadoEvaluacionFichaRepository.findById(id);
        assertThat(resultado).isPresent();

        var entity = EstadoEvaluacionFichaJpaMapper.toEntity(resultado.get());
        var aggregateReconstruido = EstadoEvaluacionFichaMapper.toDomain(entity);

        // Assert
        assertThat(aggregateReconstruido.getId()).isEqualTo(id);
        assertThat(aggregateReconstruido.getEstadoEvaluacion()).isEqualTo(EstadoEvaluacion.APROBADA);
    }

    @Test
    void debeRetornarTrue_cuandoExisteByEvaluacionAndEstado() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();

        when(estadoEvaluacionFichaRepository
                .existsByEvaluacionFichaPerfilAndEstadoEvaluacion(evaluacionId, "EN_EVALUACION"))
                .thenReturn(true);

        // Act
        boolean existe = adapter.existePorEvaluacionYEstado(evaluacionId, "EN_EVALUACION");

        // Assert
        assertThat(existe).isTrue();
        verify(estadoEvaluacionFichaRepository)
                .existsByEvaluacionFichaPerfilAndEstadoEvaluacion(evaluacionId, "EN_EVALUACION");
    }

    @Test
    void debeContarEstados_cuandoContarEstadosPorEvaluacion() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();

        when(estadoEvaluacionFichaRepository.countByEvaluacionFichaPerfil(evaluacionId))
                .thenReturn(2L);

        // Act
        long count = adapter.contarEstadosPorEvaluacion(evaluacionId);

        // Assert
        assertThat(count).isEqualTo(2);
        verify(estadoEvaluacionFichaRepository).countByEvaluacionFichaPerfil(evaluacionId);
    }
}
