package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.EliminarRevisionAsesorValidator;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.revisionasesor.EliminacionRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEliminableException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.artefactos.EliminarRevisionAsesorKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarRevisionAsesorUseCaseImplTest {

    @Mock
    private RevisionAsesorFinder revisionAsesorFinder;
    @Mock
    private ArtefactoPorRevisionAsesorFinder artefactoPorRevisionAsesorFinder;
    @Mock
    private EliminarRevisionAsesorValidator validator;
    @Mock
    private RevisionAsesorOutputPort revisionAsesorOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private EliminarRevisionAsesorUseCaseImpl useCase;

    @Test
    void debeEliminar_cuandoElValidatorPasa() {
        // Arrange
        var revisionId = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionRevisionAsesorDomain.crear(revisionId, asesor);
        var revision = RevisionAsesorDomain.reconstruir(revisionId, UtilUUID.generarNuevoUUID(),
                EstadoRevisionAsesor.PENDIENTE, UtilFecha.generarInstanteActual());
        var artefacto = ArtefactoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), asesor, null);
        when(revisionAsesorFinder.obtener(revisionId)).thenReturn(revision);
        when(artefactoPorRevisionAsesorFinder.obtener(revisionId)).thenReturn(artefacto);

        // Act
        useCase.ejecutar(eliminacion);

        // Assert
        var orden = inOrder(revisionAsesorFinder, artefactoPorRevisionAsesorFinder, validator,
                revisionAsesorOutputPort, logger);
        orden.verify(logger).info(EliminarRevisionAsesorKey.LOG_ELIMINANDO, revisionId, asesor);
        orden.verify(revisionAsesorFinder).obtener(revisionId);
        orden.verify(artefactoPorRevisionAsesorFinder).obtener(revisionId);
        orden.verify(logger).debug(EliminarRevisionAsesorKey.LOG_VERIFICACION_ELIMINAR, true, true,
                EstadoRevisionAsesor.PENDIENTE.getId());
        orden.verify(validator).validar(eliminacion, revision, artefacto);
        orden.verify(revisionAsesorOutputPort).eliminar(revisionId);
        orden.verify(logger).info(EliminarRevisionAsesorKey.LOG_ELIMINADA, revisionId);
        verify(revisionAsesorFinder, times(1)).obtener(any());
        verify(artefactoPorRevisionAsesorFinder, times(1)).obtener(any());
    }

    @Test
    void noDebeEliminar_cuandoElValidatorRechaza() {
        // Arrange
        var revisionId = UtilUUID.generarNuevoUUID();
        var eliminacion = EliminacionRevisionAsesorDomain.crear(revisionId, UtilUUID.generarNuevoUUID());
        var revision = RevisionAsesorDomain.reconstruir(revisionId, UtilUUID.generarNuevoUUID(),
                EstadoRevisionAsesor.RESUELTA, UtilFecha.generarInstanteActual());
        when(revisionAsesorFinder.obtener(revisionId)).thenReturn(revision);
        when(artefactoPorRevisionAsesorFinder.obtener(revisionId)).thenReturn(ArtefactoDomain.VACIO);
        doThrow(new RevisionAsesorNoEliminableException(revisionId, EstadoRevisionAsesor.RESUELTA))
                .when(validator).validar(any(), any(), any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(eliminacion))
                .isInstanceOf(RevisionAsesorNoEliminableException.class);
        verify(revisionAsesorOutputPort, never()).eliminar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(revisionId));
    }
}
