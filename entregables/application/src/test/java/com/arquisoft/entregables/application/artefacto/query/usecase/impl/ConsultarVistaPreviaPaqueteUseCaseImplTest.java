package com.arquisoft.entregables.application.artefacto.query.usecase.impl;

import com.arquisoft.entregables.application.artefacto.query.criteria.VistaPreviaPaqueteCriteria;
import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarVistaPreviaPaqueteUseCaseImplTest {

    @Mock
    private ArtefactoQueryOutputPort artefactoQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarVistaPreviaPaqueteUseCaseImpl useCase;

    @Test
    void debeDevolverLosArtefactosVigentesEnUnaSolaConsulta_cuandoElProyectoTieneVarios() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var artefactos = List.of(
                new ArtefactoVistaPreviaReadModel(UtilUUID.generarNuevoUUID(), "Anteproyecto", 1),
                new ArtefactoVistaPreviaReadModel(UtilUUID.generarNuevoUUID(), "Informe", 3),
                new ArtefactoVistaPreviaReadModel(UtilUUID.generarNuevoUUID(), "Manual", 2));
        when(artefactoQueryOutputPort.consultarVigentesPorProyecto(proyecto)).thenReturn(artefactos);

        // Act
        var resultado = useCase.ejecutar(new VistaPreviaPaqueteCriteria(proyecto));

        // Assert
        verify(artefactoQueryOutputPort, times(1)).consultarVigentesPorProyecto(any());
        assertThat(resultado).isSameAs(artefactos);
    }

    @Test
    void debeDevolverListaVacia_cuandoElProyectoNoTieneArtefactosVigentes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(artefactoQueryOutputPort.consultarVigentesPorProyecto(proyecto)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new VistaPreviaPaqueteCriteria(proyecto));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
