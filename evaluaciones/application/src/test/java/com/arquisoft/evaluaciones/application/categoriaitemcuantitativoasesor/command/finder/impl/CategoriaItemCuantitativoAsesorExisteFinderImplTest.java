package com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.categoriaitemcuantitativoasesor.command.secondaryport.CategoriaItemCuantitativoAsesorOutputPort;
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
class CategoriaItemCuantitativoAsesorExisteFinderImplTest {

    @Mock
    private CategoriaItemCuantitativoAsesorOutputPort outputPort;

    @InjectMocks
    private CategoriaItemCuantitativoAsesorExisteFinderImpl finder;

    @Test
    void debeRetornarResultadoDelPuerto_cuandoConsultaCategoria() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        when(outputPort.existePorId(categoria)).thenReturn(true);

        // Act
        var resultado = finder.obtener(categoria);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existePorId(categoria);
    }
}
