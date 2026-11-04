package com.arquisoft.entregables.application.entregableproyectogrado.query.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoAccesoQueryOutputPort;
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
class AsesorDeEntregableProyectoGradoQueryFinderImplTest {

    @Mock
    private EntregableProyectoGradoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private AsesorDeEntregableProyectoGradoQueryFinderImpl finder;

    @Test
    void debeRetornarElAsesor_cuandoElEntregableExiste() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorPorId(entregable)).thenReturn(Optional.of(asesor));

        // Act
        var resultado = finder.obtener(entregable);

        // Assert
        assertThat(resultado).isEqualTo(asesor);
    }

    @Test
    void debeRetornarUuidPorDefecto_cuandoElEntregableNoExiste() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorPorId(entregable)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(entregable);

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }
}
