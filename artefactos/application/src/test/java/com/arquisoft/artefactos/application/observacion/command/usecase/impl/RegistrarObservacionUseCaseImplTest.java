package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionDescripcionExisteFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.entity.ObservacionEntity;
import com.arquisoft.artefactos.application.observacion.command.validator.RegistrarObservacionValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.RegistroObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.exception.ObservacionDuplicadaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.RegistrarObservacionKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarObservacionUseCaseImplTest {

    @Mock
    private RevisionAsesorFinder revisionAsesorFinder;
    @Mock
    private ArtefactoPorRevisionAsesorFinder artefactoPorRevisionAsesorFinder;
    @Mock
    private ObservacionDescripcionExisteFinder observacionDescripcionExisteFinder;
    @Mock
    private RegistrarObservacionValidator validator;
    @Mock
    private ObservacionOutputPort observacionOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private RegistrarObservacionUseCaseImpl useCase;

    private UUID revisionId;
    private UUID asesorId;
    private ObservacionDomain observacion;
    private RegistroObservacionDomain registro;
    private RevisionAsesorDomain revision;
    private ArtefactoDomain artefacto;

    @BeforeEach
    void setUp() {
        revisionId = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        observacion = ObservacionDomain.crear(revisionId, "Detalle");
        registro = RegistroObservacionDomain.crear(observacion, asesorId);
        revision = RevisionAsesorDomain.reconstruir(revisionId, UtilUUID.generarNuevoUUID(),
                EstadoRevisionAsesor.PENDIENTE, UtilFecha.generarInstanteActual());
        artefacto = ArtefactoDomain.reconstruir(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), asesorId, null);
        when(revisionAsesorFinder.obtener(revisionId)).thenReturn(revision);
        when(artefactoPorRevisionAsesorFinder.obtener(revisionId)).thenReturn(artefacto);
        when(observacionDescripcionExisteFinder.obtener(observacion)).thenReturn(false);
    }

    @Test
    void debeRegistrarYRetornarId_cuandoValidacionPasa() {
        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        var orden = inOrder(revisionAsesorFinder, artefactoPorRevisionAsesorFinder,
                observacionDescripcionExisteFinder, validator, observacionOutputPort);
        orden.verify(revisionAsesorFinder).obtener(revisionId);
        orden.verify(artefactoPorRevisionAsesorFinder).obtener(revisionId);
        orden.verify(observacionDescripcionExisteFinder).obtener(observacion);
        orden.verify(validator).validar(registro, revision, artefacto, false);
        var captor = ArgumentCaptor.forClass(ObservacionEntity.class);
        orden.verify(observacionOutputPort).registrar(captor.capture());
        verify(revisionAsesorFinder, times(1)).obtener(any());
        verify(artefactoPorRevisionAsesorFinder, times(1)).obtener(any());
        verify(observacionDescripcionExisteFinder, times(1)).obtener(any());
        assertThat(resultado).isEqualTo(observacion.getId());
        assertThat(captor.getValue().id()).isEqualTo(resultado);
        assertThat(captor.getValue().revisionAsesor()).isEqualTo(revisionId);
        assertThat(captor.getValue().descripcion()).isEqualTo("Detalle");
        assertThat(captor.getValue().estado()).isEqualTo(EstadoObservacion.NUEVA.getId());
        verify(logger).info(RegistrarObservacionKey.LOG_REGISTRADA, resultado, revisionId);
    }

    @Test
    void noDebeRegistrar_cuandoValidatorRechaza() {
        // Arrange
        doThrow(new ObservacionDuplicadaException(revisionId)).when(validator)
                .validar(any(), any(), any(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(ObservacionDuplicadaException.class);
        verifyNoInteractions(observacionOutputPort);
        verify(logger, never()).info(eq(RegistrarObservacionKey.LOG_REGISTRADA), any(), any());
    }
}
