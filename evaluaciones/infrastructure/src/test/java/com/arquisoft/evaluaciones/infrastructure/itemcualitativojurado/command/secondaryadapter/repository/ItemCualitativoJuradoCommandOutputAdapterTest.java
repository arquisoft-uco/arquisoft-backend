package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.NombreItemCualitativoJuradoDuplicadoException;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.secondaryadapter.entity.ItemCualitativoJuradoJpaEntity;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCualitativoJuradoCommandOutputAdapterTest {

    @Mock
    private ItemCualitativoJuradoCommandRepository repository;

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
        verify(repository).saveAndFlush(captor.capture());
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
        Boolean resultado = adapter.existePorNombreIgnorandoMayusculas(nombre);

        // Assert
        assertThat(resultado).isTrue();
        verify(repository).existsByNombreIgnoreCase(nombre);
    }

    @Test
    void debeTraducirADuplicado_cuandoIndiceUnicoEsViolado() {
        // Arrange
        var entity = new ItemCualitativoJuradoEntity(
                UUID.randomUUID(), "Claridad", "Descripción");
        when(repository.saveAndFlush(any()))
                .thenThrow(new DataIntegrityViolationException("nombre duplicado"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.registrar(entity))
                .isInstanceOfSatisfying(
                        NombreItemCualitativoJuradoDuplicadoException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_DUPLICADO));
    }

    @Test
    void debeTraducirAInfraestructura_cuandoConsultaFalla() {
        // Arrange
        String nombre = "Claridad";
        when(repository.existsByNombreIgnoreCase(nombre))
                .thenThrow(new DataAccessResourceFailureException("base no disponible"));

        // Act & Assert
        assertThatThrownBy(() -> adapter.existePorNombreIgnorandoMayusculas(nombre))
                .isInstanceOfSatisfying(
                        InfrastructureException.class,
                        exception -> assertThat(exception.getCodigoError())
                                .isEqualTo(EvaluacionesCodes.ItemCualitativoJurado.PERSISTENCIA_ERROR));
    }
}
