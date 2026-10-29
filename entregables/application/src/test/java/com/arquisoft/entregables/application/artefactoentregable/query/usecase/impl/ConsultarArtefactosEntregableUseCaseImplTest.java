package com.arquisoft.entregables.application.artefactoentregable.query.usecase.impl;

import com.arquisoft.entregables.application.artefactoentregable.query.criteria.ArtefactoEntregableCriteria;
import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.entregables.application.artefactoentregable.query.secondaryport.ArtefactoEntregableQueryOutputPort;
import com.arquisoft.entregables.application.artefactoentregable.query.validator.ConsultarArtefactosEntregableValidator;
import com.arquisoft.entregables.application.entregableproyectogrado.query.finder.AsesorDeEntregableProyectoGradoQueryFinder;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoEntregableKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarArtefactosEntregableUseCaseImplTest {

    @Mock
    private ArtefactoEntregableQueryOutputPort outputPort;

    @Mock
    private AsesorDeEntregableProyectoGradoQueryFinder finder;

    @Mock
    private ConsultarArtefactosEntregableValidator validator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarArtefactosEntregableUseCaseImpl useCase;

    private static ArtefactoEntregableReadModel artefacto(UUID entregable) {
        return new ArtefactoEntregableReadModel(UtilUUID.generarNuevoUUID(), entregable,
                UtilUUID.generarNuevoUUID(), "uri", UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Doc", UtilUUID.generarNuevoUUID(), "Repo");
    }

    @Test
    void debeRetornarLoQueDevuelveElPuerto_enOrdenFinderValidatorPuerto() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var artefactos = List.of(artefacto(entregable), artefacto(entregable), artefacto(entregable));
        when(finder.obtener(entregable)).thenReturn(asesor);
        when(outputPort.consultarPorEntregable(entregable)).thenReturn(artefactos);

        // Act
        var resultado = useCase.ejecutar(new ArtefactoEntregableCriteria(entregable, asesor));

        // Assert
        assertThat(resultado).isSameAs(artefactos);
        var orden = inOrder(finder, validator, outputPort);
        orden.verify(finder, times(1)).obtener(entregable);
        orden.verify(validator).validar(entregable, asesor, asesor);
        orden.verify(outputPort, times(1)).consultarPorEntregable(entregable);
        verify(logger).debug(eq(ArtefactoEntregableKey.LOG_CONSULTANDO), eq(entregable));
        verify(logger).debug(eq(ArtefactoEntregableKey.LOG_CONSULTA_COMPLETADA), eq(entregable), eq(3));
    }

    @Test
    void debeRetornarListaVacia_cuandoElPuertoNoDevuelveNada() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(finder.obtener(entregable)).thenReturn(asesor);
        when(outputPort.consultarPorEntregable(entregable)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(new ArtefactoEntregableCriteria(entregable, asesor));

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(ArtefactoEntregableKey.LOG_CONSULTA_COMPLETADA), eq(entregable), eq(0));
    }
}
