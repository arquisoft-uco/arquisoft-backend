package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.model.ParticipacionEstudianteObservacion;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
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
class EstudianteParticipaObservacionFinderImplTest {

    @Mock
    private ObservacionOutputPort outputPort;

    @InjectMocks
    private EstudianteParticipaObservacionFinderImpl finder;

    @Test
    void debeConsultarElPuerto_conLaObservacionYElEstudiante() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        when(outputPort.existeParticipacionEstudiante(observacion, estudiante)).thenReturn(true);

        // Act
        var participa = finder.obtener(new ParticipacionEstudianteObservacion(observacion, estudiante));

        // Assert
        assertThat(participa).isTrue();
        verify(outputPort).existeParticipacionEstudiante(observacion, estudiante);
    }
}
