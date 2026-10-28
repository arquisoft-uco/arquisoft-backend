package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteParticipaRevisionAsesorFinderImplTest {

    @Mock
    private RevisionAsesorOutputPort outputPort;

    @InjectMocks
    private EstudianteParticipaRevisionAsesorFinderImpl finder;

    @Test
    void debeConsultarParticipacionConRevisionYEstudiante_cuandoSeObtiene() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoRevisionAsesorDomain.crear(revision, "RESUELTA", estudiante);
        when(outputPort.existeParticipacion(revision, estudiante)).thenReturn(true);

        // Act
        var resultado = finder.obtener(cambio);

        // Assert
        assertThat(resultado).isTrue();
        verify(outputPort).existeParticipacion(revision, estudiante);
    }
}
