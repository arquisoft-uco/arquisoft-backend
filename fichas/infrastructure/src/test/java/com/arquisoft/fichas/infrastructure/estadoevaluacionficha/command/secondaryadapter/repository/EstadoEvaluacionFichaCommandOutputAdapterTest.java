package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.application.estadoevaluacion.command.secondaryport.entity.EstadoEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.repository.EstadoEvaluacionCommandRepository;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity.EstadoEvaluacionFichaEntity;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID evaluacionId = UUID.randomUUID();
        var aggregate = EstadoEvaluacionFichaDomain.crear(evaluacionId);

        EstadoEvaluacionFichaEntity entity = EstadoEvaluacionFichaMapper.toEntity(aggregate);

        // Act
        adapter.registrarEstadoInicial(entity);

        // Assert — el adapter ya no resuelve referencias: guarda lo que le entrego el mapper
        verify(estadoEvaluacionFichaRepository).save(entity);
        assertThat(entity.getEvaluacionFichaPerfil().getId()).isEqualTo(evaluacionId);
        assertThat(entity.getEstadoEvaluacion().getId()).isEqualTo(EstadoEvaluacion.EN_EVALUACION.getId());
    }

    @Test
    void debeReconstruirConReconstruir_cuandoFindByIdExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID evaluacionId = UUID.randomUUID();

        var evaluacionJpa = EvaluacionFichaPerfilEntity.builder()
                .id(evaluacionId)
                .build();

        var estadoJpa = EstadoEvaluacionEntity.builder()
                .id("APROBADA")
                .nombre("Aprobada")
                .descripcion("")
                .build();

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

        var aggregateReconstruido = EstadoEvaluacionFichaMapper.toDomain(resultado.get());

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
