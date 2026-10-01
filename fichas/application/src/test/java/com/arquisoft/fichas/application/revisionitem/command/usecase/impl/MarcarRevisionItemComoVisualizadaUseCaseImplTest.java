package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.revisionitem.command.finder.PertenenciaRevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.validator.MarcarRevisionItemComoVisualizadaValidator;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.revisionitem.VisualizacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradoException;
import com.arquisoft.fichas.domain.revisionitem.model.PertenenciaRevisionItem;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
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
class MarcarRevisionItemComoVisualizadaUseCaseImplTest {

    @Mock
    private PertenenciaRevisionItemFinder pertenenciaRevisionItemFinder;

    @Mock
    private MarcarRevisionItemComoVisualizadaValidator marcarRevisionItemComoVisualizadaValidator;

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private MarcarRevisionItemComoVisualizadaUseCaseImpl useCase;

    private UUID revisionItem;
    private UUID estudiante;
    private UUID fichaPerfil;
    private VisualizacionRevisionItemDomain entrada;

    @BeforeEach
    void inicializar() {
        revisionItem = UtilUUID.generarNuevoUUID();
        estudiante = UtilUUID.generarNuevoUUID();
        fichaPerfil = UtilUUID.generarNuevoUUID();
        entrada = VisualizacionRevisionItemDomain.crear(revisionItem, estudiante);
    }

    @Test
    void debeActualizarAVisualizada_cuandoLaRevisionEstaNueva() {
        // Arrange
        when(pertenenciaRevisionItemFinder.obtener(entrada))
                .thenReturn(new PertenenciaRevisionItem(fichaPerfil, true, EstadoRevision.NUEVA));

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(pertenenciaRevisionItemFinder, marcarRevisionItemComoVisualizadaValidator,
                revisionItemOutputPort);
        orden.verify(pertenenciaRevisionItemFinder, times(1)).obtener(entrada);
        orden.verify(marcarRevisionItemComoVisualizadaValidator).validar(revisionItem, estudiante, fichaPerfil,
                true, true, "NUEVA");
        orden.verify(revisionItemOutputPort).actualizarEstado(revisionItem, "NUEVA", "VISUALIZADA");
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevision.class, names = {"VISUALIZADA", "EN_PROGRESO", "CORRECCION_DISPONIBLE"})
    void noDebeActualizarNiLanzar_cuandoLaRevisionYaNoEstaNueva(EstadoRevision estado) {
        // Arrange
        when(pertenenciaRevisionItemFinder.obtener(entrada))
                .thenReturn(new PertenenciaRevisionItem(fichaPerfil, true, estado));

        // Act & Assert
        assertThatCode(() -> useCase.ejecutar(entrada)).doesNotThrowAnyException();
        verify(marcarRevisionItemComoVisualizadaValidator).validar(revisionItem, estudiante, fichaPerfil,
                true, true, estado.getId());
        verify(revisionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
    }

    @Test
    void debePropagarYNoActualizar_cuandoLaRevisionNoExiste() {
        // Arrange
        when(pertenenciaRevisionItemFinder.obtener(entrada)).thenReturn(PertenenciaRevisionItem.VACIO);
        doThrow(new RevisionItemNoEncontradoException(revisionItem))
                .when(marcarRevisionItemComoVisualizadaValidator)
                .validar(eq(revisionItem), eq(estudiante), any(UUID.class), eq(false), eq(false), anyString());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemNoEncontradoException.class);
        verify(revisionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
    }

    @Test
    void debePropagarYNoActualizar_cuandoElEstudianteNoEsPropietario() {
        // Arrange
        when(pertenenciaRevisionItemFinder.obtener(entrada))
                .thenReturn(new PertenenciaRevisionItem(fichaPerfil, false, EstadoRevision.NUEVA));
        doThrow(new FichaNoPropietarioException(fichaPerfil, estudiante))
                .when(marcarRevisionItemComoVisualizadaValidator)
                .validar(eq(revisionItem), eq(estudiante), eq(fichaPerfil), eq(true), eq(false), anyString());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(FichaNoPropietarioException.class);
        verify(revisionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
    }

    @Test
    void debePropagarYNoActualizar_cuandoLaRevisionEstaCerrada() {
        // Arrange
        when(pertenenciaRevisionItemFinder.obtener(entrada))
                .thenReturn(new PertenenciaRevisionItem(fichaPerfil, true, EstadoRevision.CERRADA));
        doThrow(new RevisionItemCerradaException(revisionItem))
                .when(marcarRevisionItemComoVisualizadaValidator)
                .validar(eq(revisionItem), eq(estudiante), eq(fichaPerfil), anyBoolean(), anyBoolean(), eq("CERRADA"));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemCerradaException.class);
        verify(revisionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
    }
}
