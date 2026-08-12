package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper.EvaluacionFichaPerfilMapper;
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
class EvaluacionFichaPerfilCommandOutputAdapterTest {

    @Mock
    private EvaluacionFichaPerfilCommandRepository repository;

    private EvaluacionFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionFichaPerfilCommandOutputAdapter(repository);
    }

    @Test
    void debeGuardar_cuandoEntidadEsValida() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();
        var aggregate = EvaluacionFichaPerfilDomain.crear(representanteId, fichaId);

        EvaluacionFichaPerfilEntity entity = EvaluacionFichaPerfilMapper.toEntity(aggregate);

        // Act
        adapter.registrarEvaluacion(entity);

        // Assert — el adapter ya no traduce: guarda la entidad que le entrego el caso de uso
        verify(repository).save(entity);
    }

    @Test
    void debeRetornarTrue_cuandoExistsByRepresentanteAndFicha() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();

        when(repository.existsByRepresentanteComiteIdAndFichaPerfilId(representanteId, fichaId))
                .thenReturn(true);

        // Act
        boolean existe = adapter.existePorRepresentanteYFicha(representanteId, fichaId);

        // Assert
        assertThat(existe).isTrue();
        verify(repository).existsByRepresentanteComiteIdAndFichaPerfilId(representanteId, fichaId);
    }

    @Test
    void debeRetornarFalse_cuandoNoExistsByRepresentanteAndFicha() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();

        when(repository.existsByRepresentanteComiteIdAndFichaPerfilId(representanteId, fichaId))
                .thenReturn(false);

        // Act
        boolean existe = adapter.existePorRepresentanteYFicha(representanteId, fichaId);

        // Assert
        assertThat(existe).isFalse();
        verify(repository).existsByRepresentanteComiteIdAndFichaPerfilId(representanteId, fichaId);
    }
}
