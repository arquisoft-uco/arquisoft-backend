package com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
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
class EntregableProyectoGradoBloqueadoPorIdFinderImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @InjectMocks
    private EntregableProyectoGradoBloqueadoPorIdFinderImpl finder;

    @Test
    void debeRetornarDomainReconstruido_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoOutputPort.obtenerBloqueadoPorId(id)).thenReturn(Optional.of(
                new EntregableProyectoGradoEntity(id, proyecto, 1, "Aprobado", "entregables", "a/b.zip", "b.zip")));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(resultado.getBucket()).isEqualTo("entregables");
        assertThat(resultado.getClaveObjeto()).isEqualTo("a/b.zip");
        assertThat(resultado.getVersionEntregable()).isEqualTo(1);
        assertThat(resultado.getEstadoEntregableNombre()).isEqualTo("Aprobado");
        assertThat(resultado.getNombreArchivo()).isEqualTo("b.zip");
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoOutputPort.obtenerBloqueadoPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(EntregableProyectoGradoDomain.VACIO);
    }
}
