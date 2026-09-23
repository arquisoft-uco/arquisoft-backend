package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.entity.ItemCuantitativoJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.secondaryadapter.entity.ItemCuantitativoJuradoJpaEntity;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCuantitativoJuradoCommandOutputAdapterTest {

    @Mock
    private ItemCuantitativoJuradoCommandRepository repository;

    @Mock
    private CategoriaItemCuantitativoJuradoCommandRepository categoriaRepository;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ItemCuantitativoJuradoCommandOutputAdapter adapter;

    @Test
    void debeMapearYGuardarItem() {
        // Arrange
        var item = new ItemCuantitativoJuradoEntity(
                UUID.randomUUID(), "Calidad", "Descripción", UUID.randomUUID(), 100);

        // Act
        adapter.registrar(item);

        // Assert
        var captor = ArgumentCaptor.forClass(ItemCuantitativoJuradoJpaEntity.class);
        verify(repository).save(captor.capture());
        verify(logger).debug(any(ClaveMensaje.class), eq(item.id()));
        assertThat(captor.getValue().getId()).isEqualTo(item.id());
        assertThat(captor.getValue().getCategoriaId()).isEqualTo(item.categoriaId());
        assertThat(captor.getValue().getValor()).isEqualTo(item.valor());
    }

    @Test
    void debeDelegarConsultaDeCategoria() {
        // Arrange
        UUID categoria = UUID.randomUUID();
        when(categoriaRepository.existsById(categoria)).thenReturn(true);

        // Act & Assert
        assertThat(adapter.existeCategoriaPorId(categoria)).isTrue();
        verify(categoriaRepository).existsById(categoria);
    }

    @Test
    void debeDelegarConsultaDeNombreYCategoria() {
        // Arrange
        UUID categoria = UUID.randomUUID();
        when(repository.existsByNombreIgnoreCaseAndCategoriaId("calidad", categoria))
                .thenReturn(true);

        // Act & Assert
        assertThat(adapter.existePorNombreYCategoriaIgnorandoMayusculas(
                "calidad", categoria)).isTrue();
        verify(repository).existsByNombreIgnoreCaseAndCategoriaId("calidad", categoria);
    }
}
