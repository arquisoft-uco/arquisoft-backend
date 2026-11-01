package com.arquisoft.artefactos.application.observacion.query.usecase.impl;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionEstudianteCriteria;
import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.application.observacion.query.secondaryport.ObservacionQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarObservacionesRevisionEstudianteUseCaseImplTest {

    @Mock
    private ObservacionQueryOutputPort observacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesRevisionEstudianteUseCaseImpl useCase;

    @Test
    void debeRetornarLosReadModels_cuandoHayObservaciones() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var observaciones = List.of(
                new ObservacionReadModel(UtilUUID.generarNuevoUUID(), revision, "A", "NUEVA", "Nueva"),
                new ObservacionReadModel(UtilUUID.generarNuevoUUID(), revision, "B", "CERRADA", "Cerrada"));
        when(observacionQueryOutputPort.consultarDeEstudiante(revision, estudiante)).thenReturn(observaciones);

        // Act
        var resultado = useCase.ejecutar(new ObservacionEstudianteCriteria(revision, estudiante));

        // Assert
        assertThat(resultado).isSameAs(observaciones);
        verify(observacionQueryOutputPort, times(1)).consultarDeEstudiante(revision, estudiante);
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayObservaciones() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        when(observacionQueryOutputPort.consultarDeEstudiante(revision, estudiante)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new ObservacionEstudianteCriteria(revision, estudiante));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
