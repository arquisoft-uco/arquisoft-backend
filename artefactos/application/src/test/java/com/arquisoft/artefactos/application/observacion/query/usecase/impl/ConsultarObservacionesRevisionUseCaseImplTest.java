package com.arquisoft.artefactos.application.observacion.query.usecase.impl;

import com.arquisoft.artefactos.application.observacion.query.criteria.ObservacionCriteria;
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
class ConsultarObservacionesRevisionUseCaseImplTest {

    @Mock
    private ObservacionQueryOutputPort observacionQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarObservacionesRevisionUseCaseImpl useCase;

    @Test
    void debeRetornarLosReadModels_cuandoHayObservaciones() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var observaciones = List.of(
                new ObservacionReadModel(UtilUUID.generarNuevoUUID(), revision, "A", "NUEVA", "Nueva"),
                new ObservacionReadModel(UtilUUID.generarNuevoUUID(), revision, "B", "CERRADA", "Cerrada"),
                new ObservacionReadModel(UtilUUID.generarNuevoUUID(), revision, "C", "NUEVA", "Nueva"));
        when(observacionQueryOutputPort.consultarDeAsesor(revision, asesor)).thenReturn(observaciones);

        // Act
        var resultado = useCase.ejecutar(new ObservacionCriteria(revision, asesor));

        // Assert
        assertThat(resultado).isSameAs(observaciones);
        verify(observacionQueryOutputPort, times(1)).consultarDeAsesor(revision, asesor);
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayObservaciones() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(observacionQueryOutputPort.consultarDeAsesor(revision, asesor)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new ObservacionCriteria(revision, asesor));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
