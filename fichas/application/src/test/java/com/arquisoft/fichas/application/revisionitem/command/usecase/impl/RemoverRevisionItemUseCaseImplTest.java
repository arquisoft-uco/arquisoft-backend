package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.AsesoriaRevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.validator.RemoverRevisionItemValidator;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.revisionitem.RemocionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.fichas.domain.revisionitem.model.AsesoriaRevisionItem;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverRevisionItemUseCaseImplTest {

    @Mock
    private AsesoriaRevisionItemFinder asesoriaRevisionItemFinder;

    @Mock
    private RemoverRevisionItemValidator removerRevisionItemValidator;

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private RemoverRevisionItemUseCaseImpl useCase;

    private UUID revisionItem;
    private UUID asesorSolicitante;
    private UUID asesorDeLaFicha;
    private UUID fichaPerfil;
    private RemocionRevisionItemDomain entrada;

    @BeforeEach
    void inicializar() {
        revisionItem = UtilUUID.generarNuevoUUID();
        asesorSolicitante = UtilUUID.generarNuevoUUID();
        asesorDeLaFicha = UtilUUID.generarNuevoUUID();
        fichaPerfil = UtilUUID.generarNuevoUUID();
        entrada = RemocionRevisionItemDomain.crear(revisionItem, asesorSolicitante);
    }

    @Test
    void debeRemoverLaRevision_cuandoLaValidacionPasa() {
        // Arrange
        when(asesoriaRevisionItemFinder.obtener(revisionItem))
                .thenReturn(new AsesoriaRevisionItem(fichaPerfil, asesorSolicitante, EstadoRevision.EN_PROGRESO));

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(asesoriaRevisionItemFinder, removerRevisionItemValidator, revisionItemOutputPort);
        orden.verify(asesoriaRevisionItemFinder, times(1)).obtener(revisionItem);
        orden.verify(removerRevisionItemValidator).validar(revisionItem, asesorSolicitante, fichaPerfil,
                asesorSolicitante, true, "EN_PROGRESO");
        orden.verify(revisionItemOutputPort, times(1)).removerRevision(revisionItem);
        verify(logger).info(any(ClaveMensaje.class), eq(revisionItem), eq(asesorSolicitante));
        verify(logger).info(any(ClaveMensaje.class), eq(revisionItem));
    }

    @Test
    void debePasarElAsesorCrudoDeLaFichaYNoRemover_cuandoElSolicitanteNoEsElAsesor() {
        // Arrange
        when(asesoriaRevisionItemFinder.obtener(revisionItem))
                .thenReturn(new AsesoriaRevisionItem(fichaPerfil, asesorDeLaFicha, EstadoRevision.NUEVA));
        doThrow(new FichaNoPerteneceAsesorException(fichaPerfil, asesorSolicitante))
                .when(removerRevisionItemValidator)
                .validar(revisionItem, asesorSolicitante, fichaPerfil, asesorDeLaFicha, true, "NUEVA");

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);
        verify(revisionItemOutputPort, never()).removerRevision(any());
        verify(logger, never()).info(eq(RevisionItemKey.LOG_REMOVIDA), eq(revisionItem));
    }

    @Test
    void debePropagarYNoRemover_cuandoLaRevisionNoExiste() {
        // Arrange
        when(asesoriaRevisionItemFinder.obtener(revisionItem)).thenReturn(AsesoriaRevisionItem.VACIO);
        doThrow(new RevisionItemNoEncontradoException(revisionItem))
                .when(removerRevisionItemValidator)
                .validar(eq(revisionItem), eq(asesorSolicitante), eq(UtilUUID.obtenerUUIDPorDefecto()),
                        eq(UtilUUID.obtenerUUIDPorDefecto()), eq(false), anyString());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemNoEncontradoException.class);
        verify(revisionItemOutputPort, never()).removerRevision(any());
    }

    @Test
    void debePropagarYNoRemover_cuandoLaRevisionEstaCerrada() {
        // Arrange
        when(asesoriaRevisionItemFinder.obtener(revisionItem))
                .thenReturn(new AsesoriaRevisionItem(fichaPerfil, asesorSolicitante, EstadoRevision.CERRADA));
        doThrow(new RevisionItemCerradaException(revisionItem))
                .when(removerRevisionItemValidator)
                .validar(eq(revisionItem), eq(asesorSolicitante), eq(fichaPerfil), eq(asesorSolicitante),
                        anyBoolean(), eq("CERRADA"));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemCerradaException.class);
        verify(revisionItemOutputPort, never()).removerRevision(any());
    }
}
