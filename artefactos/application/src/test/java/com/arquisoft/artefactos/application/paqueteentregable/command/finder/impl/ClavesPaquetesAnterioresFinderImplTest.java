package com.arquisoft.artefactos.application.paqueteentregable.command.finder.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesAnterioresDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClavesPaquetesAnterioresFinderImplTest {

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @InjectMocks
    private ClavesPaquetesAnterioresFinderImpl finder;

    @Test
    void debePedirLasClavesMenoresALaVersionDeLaPurga() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var purga = PurgaPaquetesAnterioresDomain.crear(proyecto, 3);
        when(paqueteEntregableOutputPort.obtenerClavesAnteriores(proyecto, 3)).thenReturn(List.of("paquetes/v1.zip"));

        // Act
        var claves = finder.obtener(purga);

        // Assert
        assertThat(claves).containsExactly("paquetes/v1.zip");
    }
}
