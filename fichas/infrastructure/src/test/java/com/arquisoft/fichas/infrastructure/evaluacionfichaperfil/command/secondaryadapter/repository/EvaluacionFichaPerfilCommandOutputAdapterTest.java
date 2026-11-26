package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.ConteoEvaluacionesPorEstadoEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionExistenteEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper.EvaluacionFichaPerfilMapper;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionFichaPerfilCommandOutputAdapterTest {

    @Mock
    private EvaluacionFichaPerfilCommandRepository repository;

    private EvaluacionFichaPerfilCommandOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EvaluacionFichaPerfilCommandOutputAdapter(repository, mock(AppLogger.class));
    }

    @Test
    void debeMapearYGuardarLaEntidadComoJpaEntity_cuandoRegistraLaEvaluacion() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();
        var aggregate = EvaluacionFichaPerfilDomain.crear(representanteId, fichaId);

        EvaluacionFichaPerfilEntity entity = EvaluacionFichaPerfilMapper.toEntity(aggregate);

        // Act
        adapter.registrarEvaluacion(entity);

        // Assert
        verify(repository).save(argThat(jpaEntity ->
                jpaEntity.getId().equals(entity.id())
                        && jpaEntity.getRepresentanteComiteId().equals(representanteId)
                        && jpaEntity.getFichaPerfilId().equals(fichaId)));
    }

    @Test
    void debeRetornarLaEvaluacionExistente_cuandoElRepresentanteYaTieneUnaEnLaFicha() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();

        var existente = new EvaluacionExistenteEntity(UUID.randomUUID(), "DESCARTADA");
        when(repository.obtenerEvaluacionDeRepresentanteEnFicha(representanteId, fichaId))
                .thenReturn(Optional.of(existente));

        // Act
        var resultado = adapter.obtenerEvaluacionDeRepresentanteEnFicha(representanteId, fichaId);

        // Assert
        assertThat(resultado).contains(existente);
    }

    @Test
    void debeDevolverLosConteosDelRepositorio_cuandoSeConsultaElResumenDeLaFicha() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var conteos = List.of(new ConteoEvaluacionesPorEstadoEntity("APROBADA", 1, 0));
        when(repository.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaId)).thenReturn(conteos);

        // Act
        var resultado = adapter.contarEvaluacionesDeFichaPorEstadoEvaluacionActual(fichaId);

        // Assert
        assertThat(resultado).isSameAs(conteos);
    }

    @Test
    void debeRetornarVacio_cuandoElRepresentanteNoTieneEvaluacionEnLaFicha() {
        // Arrange
        UUID representanteId = UUID.randomUUID();
        UUID fichaId = UUID.randomUUID();

        when(repository.obtenerEvaluacionDeRepresentanteEnFicha(representanteId, fichaId))
                .thenReturn(Optional.empty());

        // Act
        var resultado = adapter.obtenerEvaluacionDeRepresentanteEnFicha(representanteId, fichaId);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
