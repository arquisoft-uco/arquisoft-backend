package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativojurado.command.secondaryport.CategoriaItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl.CategoriaItemCuantitativoJuradoExisteFinderImpl;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl.NombreItemCuantitativoJuradoPorCategoriaExisteFinderImpl;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCuantitativoJuradoFindersTest {

    @Mock
    private ItemCuantitativoJuradoOutputPort outputPort;

    @Mock
    private CategoriaItemCuantitativoJuradoOutputPort categoriaOutputPort;

    @Test
    void debeDelegarConsultaDeCategoria() {
        // Arrange
        UUID categoria = UUID.randomUUID();
        when(categoriaOutputPort.existePorId(categoria)).thenReturn(true);
        var finder = new CategoriaItemCuantitativoJuradoExisteFinderImpl(categoriaOutputPort);

        // Act & Assert
        assertThat(finder.obtener(categoria)).isTrue();
        verify(categoriaOutputPort).existePorId(categoria);
    }

    @Test
    void debeDelegarConsultaDeNombreYCategoria() {
        // Arrange
        ItemCuantitativoJuradoDomain item = ItemCuantitativoJuradoDomain.crear(
                "Calidad", "Descripción", UUID.randomUUID(), 100);
        when(outputPort.existePorNombreYCategoriaIgnorandoMayusculas(
                item.getNombre(), item.getCategoria())).thenReturn(true);
        var finder = new NombreItemCuantitativoJuradoPorCategoriaExisteFinderImpl(outputPort);

        // Act & Assert
        assertThat(finder.obtener(item)).isTrue();
        verify(outputPort).existePorNombreYCategoriaIgnorandoMayusculas(
                item.getNombre(), item.getCategoria());
    }
}
