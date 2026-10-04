package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.entity.EvaluacionCualitativaJuradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionCualitativaJuradoCommandOutputAdapterTest {

    @Mock
    private EvaluacionCualitativaJuradoCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private EvaluacionCualitativaJuradoCommandOutputAdapter adapter;

    @Test
    void debeMapearYGuardar_cuandoRegistraTodas() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        var entidad = new EvaluacionCualitativaJuradoEntity(
                UUID.randomUUID(), evaluacionJurado, UUID.randomUUID(), UUID.randomUUID());
        List<EvaluacionCualitativaJuradoEntity> entidades = List.of(entidad);

        // Act
        adapter.registrarTodas(entidades);

        // Assert
        ArgumentCaptor<List<EvaluacionCualitativaJuradoJpaEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getId()).isEqualTo(entidad.id());
        verify(logger).debug(any(ClaveMensaje.class), eq(1));
    }

    @Test
    void debeDelegarEnRepositorio_cuandoConsultaItemsRegistrados() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        UUID itemRegistrado = UUID.randomUUID();
        Set<UUID> items = Set.of(itemRegistrado);
        when(repository.findItemsRegistrados(evaluacionJurado, items)).thenReturn(Set.of(itemRegistrado));

        // Act
        Set<UUID> resultado = adapter.consultarItemsRegistrados(evaluacionJurado, items);

        // Assert
        assertThat(resultado).containsExactly(itemRegistrado);
        verify(repository).findItemsRegistrados(evaluacionJurado, items);
    }
}
