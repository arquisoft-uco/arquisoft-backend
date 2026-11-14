package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NombreItemCuantitativoAsesorPorCategoriaExisteFinderImplTest {

    @Mock
    private ItemCuantitativoAsesorOutputPort outputPort;

    @InjectMocks
    private NombreItemCuantitativoAsesorPorCategoriaExisteFinderImpl finder;

    @Test
    void debeRetornarResultadoDelPuerto_cuandoConsultaNombreYCategoria() {
        // Arrange
        var item = ItemCuantitativoAsesorDomain.crear("Avance", "Porcentaje", UtilUUID.generarNuevoUUID(), 80);
        when(outputPort.existePorNombreYCategoriaIgnorandoMayusculas(item.getNombre(), item.getCategoria()))
                .thenReturn(true);

        // Act
        var resultado = finder.obtener(item);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existePorNombreYCategoriaIgnorandoMayusculas(item.getNombre(), item.getCategoria());
    }
}
