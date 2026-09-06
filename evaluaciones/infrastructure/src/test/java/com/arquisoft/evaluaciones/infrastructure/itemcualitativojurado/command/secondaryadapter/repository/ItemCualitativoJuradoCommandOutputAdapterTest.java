package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCualitativoJuradoCommandOutputAdapterTest {

    @Mock
    private ItemCualitativoJuradoCommandRepository repository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ItemCualitativoJuradoCommandOutputAdapter adapter;

    @Test
    void debeMapearYGuardar_cuandoEntidadEsValida() {
        // Arrange
        var entity = new ItemCualitativoJuradoEntity(
                UUID.randomUUID(), "Claridad", "Descripción");

        // Act
        adapter.registrar(entity);

        // Assert
        ArgumentCaptor<ItemCualitativoJuradoJpaEntity> captor =
                ArgumentCaptor.forClass(ItemCualitativoJuradoJpaEntity.class);
        verify(repository).save(captor.capture());
        verify(logger).debug(any(ClaveMensaje.class), eq(entity.id()));
        assertThat(captor.getValue().getId()).isEqualTo(entity.id());
        assertThat(captor.getValue().getNombre()).isEqualTo(entity.nombre());
        assertThat(captor.getValue().getDescripcion()).isEqualTo(entity.descripcion());
    }

    @Test
    void debeRetornarResultado_cuandoConsultaNombreIgnorandoMayusculas() {
        // Arrange
        String nombre = "claridad";
        when(repository.existsByNombreIgnoreCase(nombre)).thenReturn(true);

        // Act
        boolean resultado = adapter.existePorNombreIgnorandoMayusculas(nombre);

        // Assert
        assertThat(resultado).isTrue();
        verify(repository).existsByNombreIgnoreCase(nombre);
    }

    @Test
    void debeDelegarEnRepositorio_cuandoConsultaExistenciaPorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(true);

        // Act
        boolean resultado = adapter.existePorId(id);

        // Assert
        assertThat(resultado).isTrue();
        verify(repository).existsById(id);
    }

    @Test
    void debeActualizarDescripcionSinSaveAndFlush_cuandoItemExiste() {
        // Arrange
        UUID id = UUID.randomUUID();
        String descripcion = "Descripción nueva";

        // Act
        adapter.actualizarDescripcion(id, descripcion);

        // Assert
        verify(repository).actualizarDescripcion(id, descripcion);
        verify(repository, never()).saveAndFlush(any());
        verify(logger).debug(any(ClaveMensaje.class), eq(id));
    }
}
