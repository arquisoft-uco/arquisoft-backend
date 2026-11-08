package com.arquisoft.artefactos.application.proyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
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
class ProyectoGradoPorIdFinderImplTest {

    @Mock
    private ProyectoGradoOutputPort proyectoGradoOutputPort;

    @InjectMocks
    private ProyectoGradoPorIdFinderImpl finder;

    @Test
    void debeMapearAlDomain_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.of(new ProyectoGradoEntity(id, "Proyecto")));

        // Act
        var proyecto = finder.obtener(id);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getNombre()).isEqualTo("Proyecto");
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(proyectoGradoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var proyecto = finder.obtener(id);

        // Assert
        assertThat(proyecto.esVacio()).isTrue();
    }
}
