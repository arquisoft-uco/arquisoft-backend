package com.arquisoft.entregables.application.asesorproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.query.secondaryport.AsesorProyectoGradoAccesoQueryOutputPort;
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
class AsesorVigenteProyectoGradoQueryFinderImplTest {

    @Mock
    private AsesorProyectoGradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private AsesorVigenteProyectoGradoQueryFinderImpl finder;

    @Test
    void debeRetornarElAsesorVigente_cuandoElProyectoLoTiene() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorVigentePorProyectoGrado(proyecto)).thenReturn(Optional.of(asesor));

        // Act
        var resultado = finder.obtener(proyecto);

        // Assert
        assertThat(resultado).isEqualTo(asesor);
    }

    @Test
    void debeRetornarUuidPorDefecto_cuandoElProyectoNoTieneAsesorVigente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorVigentePorProyectoGrado(proyecto)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(proyecto);

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
