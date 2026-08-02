package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionFichaPerfilCommandOutputAdapterTest {

    @Mock
    private EvaluacionFichaPerfilRepository jpaRepository;

    private EvaluacionFichaPerfilMapper mapper;
    private EvaluacionFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        mapper = new EvaluacionFichaPerfilMapper();
        adapter = new EvaluacionFichaPerfilCommandOutputAdapter(jpaRepository, mapper);
    }

    @Test
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();
        var aggregate = EvaluacionFichaPerfilAggregate.crear(representanteId, fichaId);

        EvaluacionFichaPerfilEntity entityGuardada = EvaluacionFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .representanteComiteId(representanteId)
                .fichaPerfilId(fichaId)
                .fechaCreacion(aggregate.getFechaCreacion())
                .build();

        when(jpaRepository.save(any(EvaluacionFichaPerfilEntity.class))).thenReturn(entityGuardada);

        // Act
        adapter.guardar(aggregate);

        // Assert
        verify(jpaRepository).save(any(EvaluacionFichaPerfilEntity.class));
    }

    @Test
    void debeRetornarTrue_cuandoExistsByRepresentanteAndFicha() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();

        when(jpaRepository.existsByRepresentanteComiteIdAndFichaPerfilId(representanteId, fichaId))
                .thenReturn(true);

        // Act
        boolean existe = adapter.existePorRepresentanteYFicha(representanteId, fichaId);

        // Assert
        assertThat(existe).isTrue();
        verify(jpaRepository).existsByRepresentanteComiteIdAndFichaPerfilId(representanteId, fichaId);
    }

    @Test
    void debeRetornarFalse_cuandoNoExistsByRepresentanteAndFicha() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();

        when(jpaRepository.existsByRepresentanteComiteIdAndFichaPerfilId(representanteId, fichaId))
                .thenReturn(false);

        // Act
        boolean existe = adapter.existePorRepresentanteYFicha(representanteId, fichaId);

        // Assert
        assertThat(existe).isFalse();
        verify(jpaRepository).existsByRepresentanteComiteIdAndFichaPerfilId(representanteId, fichaId);
    }
}
