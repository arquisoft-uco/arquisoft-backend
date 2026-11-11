package com.arquisoft.artefactos.application.paqueteentregable.command.finder.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
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
class ClavesPaquetesProyectoGradoFinderImplTest {

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @InjectMocks
    private ClavesPaquetesProyectoGradoFinderImpl finder;

    @Test
    void debePedirTodasLasClavesDelProyectoDeLaPurga() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var purga = PurgaPaquetesProyectoGradoDomain.crear(proyecto);
        when(paqueteEntregableOutputPort.obtenerClavesPorProyectoGrado(proyecto))
                .thenReturn(List.of("paquetes/v1.zip", "paquetes/v2.zip"));

        // Act
        var claves = finder.obtener(purga);

        // Assert
        assertThat(claves).containsExactly("paquetes/v1.zip", "paquetes/v2.zip");
    }
}
