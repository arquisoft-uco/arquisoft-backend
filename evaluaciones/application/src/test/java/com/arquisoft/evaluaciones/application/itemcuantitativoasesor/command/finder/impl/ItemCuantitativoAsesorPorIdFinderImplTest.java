package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity.ItemCuantitativoAsesorEntity;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemCuantitativoAsesorPorIdFinderImplTest {

    @Mock
    private ItemCuantitativoAsesorOutputPort outputPort;

    @InjectMocks
    private ItemCuantitativoAsesorPorIdFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var categoria = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id))
                .thenReturn(Optional.of(new ItemCuantitativoAsesorEntity(id, "Rigor", "Descripción", categoria, 300)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getNombre()).isEqualTo("Rigor");
        assertThat(resultado.getValor()).isEqualTo(300);
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(ItemCuantitativoAsesorDomain.VACIO);
    }
}
