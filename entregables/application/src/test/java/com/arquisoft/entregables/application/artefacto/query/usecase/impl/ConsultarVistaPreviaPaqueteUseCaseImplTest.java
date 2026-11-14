package com.arquisoft.entregables.application.artefacto.query.usecase.impl;

import com.arquisoft.entregables.application.artefacto.query.criteria.VistaPreviaPaqueteCriteria;
import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.application.artefacto.query.secondaryport.ArtefactoQueryOutputPort;
import com.arquisoft.entregables.application.artefacto.query.validator.ConsultarVistaPreviaPaqueteValidator;
import com.arquisoft.entregables.application.asesorproyectogrado.query.finder.AsesorVigenteProyectoGradoQueryFinder;
import com.arquisoft.entregables.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarVistaPreviaPaqueteUseCaseImplTest {

    @Mock
    private AsesorVigenteProyectoGradoQueryFinder asesorVigenteProyectoGradoQueryFinder;

    @Mock
    private ConsultarVistaPreviaPaqueteValidator consultarVistaPreviaPaqueteValidator;

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
        var asesor = UtilUUID.generarNuevoUUID();
        var artefactos = List.of(
                new ArtefactoVistaPreviaReadModel(UtilUUID.generarNuevoUUID(), "Anteproyecto", 1),
                new ArtefactoVistaPreviaReadModel(UtilUUID.generarNuevoUUID(), "Informe", 3),
                new ArtefactoVistaPreviaReadModel(UtilUUID.generarNuevoUUID(), "Manual", 2));
        when(asesorVigenteProyectoGradoQueryFinder.obtener(proyecto)).thenReturn(asesor);
        when(artefactoQueryOutputPort.consultarVigentesPorProyecto(proyecto)).thenReturn(artefactos);

        // Act
        var resultado = useCase.ejecutar(new VistaPreviaPaqueteCriteria(proyecto, asesor));

        // Assert
        var orden = inOrder(asesorVigenteProyectoGradoQueryFinder, consultarVistaPreviaPaqueteValidator,
                artefactoQueryOutputPort);
        orden.verify(asesorVigenteProyectoGradoQueryFinder).obtener(proyecto);
        orden.verify(consultarVistaPreviaPaqueteValidator).validar(proyecto, asesor, asesor);
        orden.verify(artefactoQueryOutputPort).consultarVigentesPorProyecto(proyecto);
        verify(artefactoQueryOutputPort, times(1)).consultarVigentesPorProyecto(any());
        assertThat(resultado).isSameAs(artefactos);
    }

    @Test
    void noDebeLeerArtefactos_cuandoElSolicitanteNoEsElAsesorVigente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();
        var vigente = UtilUUID.generarNuevoUUID();
        var rechazo = new AsesorNoAsignadoProyectoGradoException(solicitante, proyecto);
        when(asesorVigenteProyectoGradoQueryFinder.obtener(proyecto)).thenReturn(vigente);
        doThrow(rechazo).when(consultarVistaPreviaPaqueteValidator).validar(proyecto, vigente, solicitante);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(new VistaPreviaPaqueteCriteria(proyecto, solicitante)))
                .isSameAs(rechazo);
        verify(artefactoQueryOutputPort, never()).consultarVigentesPorProyecto(any());
    }

    @Test
    void debeDevolverListaVacia_cuandoElProyectoNoTieneArtefactosVigentes() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(artefactoQueryOutputPort.consultarVigentesPorProyecto(proyecto)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new VistaPreviaPaqueteCriteria(proyecto, asesor));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
