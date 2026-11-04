package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.util.UtilFecha;
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
class RevisionVersionSiguientePorObservacionFinderImplTest {

    @Mock
    private RevisionAsesorOutputPort revisionAsesorOutputPort;

    @InjectMocks
    private RevisionVersionSiguientePorObservacionFinderImpl finder;

    @Test
    void debeDevolverLaRevision_cuandoLaVersionSiguienteLaTiene() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(revisionAsesorOutputPort.obtenerBloqueadaDeVersionSiguientePorObservacion(observacion))
                .thenReturn(Optional.of(new RevisionAsesorEntity(id, UtilUUID.generarNuevoUUID(),
                        EstadoRevisionAsesor.PENDIENTE.getId(), UtilFecha.generarInstanteActual())));

        // Act
        var resultado = finder.obtener(observacion);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getEstado()).isEqualTo(EstadoRevisionAsesor.PENDIENTE);
    }

    @Test
    void debeDevolverVacio_cuandoNoHayRevisionEnLaVersionSiguiente() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        when(revisionAsesorOutputPort.obtenerBloqueadaDeVersionSiguientePorObservacion(observacion))
                .thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(observacion);

        // Assert
        assertThat(resultado).isSameAs(RevisionAsesorDomain.VACIO);
    }
}
