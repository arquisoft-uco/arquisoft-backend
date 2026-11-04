package com.arquisoft.entregables.application.entregableproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UltimaVersionEntregableProyectoGradoFinderImplTest {

    @Mock
    private EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;

    @InjectMocks
    private UltimaVersionEntregableProyectoGradoFinderImpl finder;

    @Test
    void debeRetornarLaUltimaVersionDelPuerto_cuandoSeConsultaElProyecto() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(entregableProyectoGradoOutputPort.obtenerUltimaVersion(proyecto)).thenReturn(7);

        // Act
        var version = finder.obtener(proyecto);

        // Assert
        assertThat(version).isEqualTo(7);
    }
}
