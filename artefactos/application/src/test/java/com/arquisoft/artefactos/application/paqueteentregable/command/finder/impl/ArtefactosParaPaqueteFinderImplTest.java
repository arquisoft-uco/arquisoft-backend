package com.arquisoft.artefactos.application.paqueteentregable.command.finder.impl;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoParaPaqueteEntity;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtefactosParaPaqueteFinderImplTest {

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @InjectMocks
    private ArtefactosParaPaqueteFinderImpl finder;

    @Test
    void debePedirLasVersionesCerradasYClasificar() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var fila = new ArtefactoParaPaqueteEntity(UtilUUID.generarNuevoUUID(), "Doc",
                UtilUUID.generarNuevoUUID(), 1, "uri", Instant.now(), 1,
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo");
        when(paqueteEntregableOutputPort.obtenerArtefactosParaPaquete(proyecto, EstadoRevisionAsesor.CERRADA.getId()))
                .thenReturn(List.of(fila));

        // Act
        var resultado = finder.obtener(proyecto);

        // Assert
        assertThat(resultado.incluidos()).hasSize(1);
        assertThat(resultado.excluidos()).isEmpty();
    }
}
