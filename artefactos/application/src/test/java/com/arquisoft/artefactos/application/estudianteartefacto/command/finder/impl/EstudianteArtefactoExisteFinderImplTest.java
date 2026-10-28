package com.arquisoft.artefactos.application.estudianteartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteArtefactoExisteFinderImplTest {

    @Mock
    private EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;

    @InjectMocks
    private EstudianteArtefactoExisteFinderImpl finder;

    @Test
    void debeConsultarElPuerto_conArtefactoYEstudiante() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        when(estudianteArtefactoOutputPort.existePorArtefactoYEstudiante(artefacto, estudiante)).thenReturn(true);

        // Act
        var resultado = finder.obtener(new ParticipacionEstudianteArtefacto(estudiante, artefacto));

        // Assert
        assertThat(resultado).isTrue();
    }
}
