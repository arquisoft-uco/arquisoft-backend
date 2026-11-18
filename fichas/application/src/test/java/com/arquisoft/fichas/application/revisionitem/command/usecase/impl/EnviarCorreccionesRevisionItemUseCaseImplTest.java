package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.ObservacionesDeRevisionFinder;
import com.arquisoft.fichas.application.revisionitem.command.finder.PertenenciaRevisionItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.validator.EnviarCorreccionesRevisionItemValidator;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.revisionitem.EnvioCorreccionesRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.event.CorreccionesRevisionItemEnviadasEvent;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemConObservacionesPendientesException;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.PertenenciaRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.model.RevisionItemDeEstudiante;
import com.arquisoft.shared.events.DomainEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.RevisionItemKey;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarCorreccionesRevisionItemUseCaseImplTest {

    @Mock
    private PertenenciaRevisionItemFinder pertenenciaRevisionItemFinder;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private AsesorDeFichaFinder asesorDeFichaFinder;

    @Mock
    private ObservacionesDeRevisionFinder observacionesDeRevisionFinder;

    @Mock
    private EnviarCorreccionesRevisionItemValidator validator;

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private EnviarCorreccionesRevisionItemUseCaseImpl useCase;

    private UUID revisionItem;
    private UUID estudiante;
    private UUID fichaPerfilId;
    private UUID asesorFichaId;
    private EnvioCorreccionesRevisionItemDomain entrada;
    private PertenenciaRevisionItem pertenencia;
    private FichaPerfilDomain ficha;
    private AsesorFichaDomain asesor;
    private ObservacionesRevisionItem observaciones;

    @BeforeEach
    void setUp() {
        revisionItem = UtilUUID.generarNuevoUUID();
        estudiante = UtilUUID.generarNuevoUUID();
        fichaPerfilId = UtilUUID.generarNuevoUUID();
        asesorFichaId = UtilUUID.generarNuevoUUID();
        entrada = EnvioCorreccionesRevisionItemDomain.crear(revisionItem, estudiante);
        pertenencia = new PertenenciaRevisionItem(fichaPerfilId, true, EstadoRevision.EN_PROGRESO);
        ficha = FichaPerfilDomain.reconstruir(fichaPerfilId, "Sistema de gestión", asesorFichaId);
        asesor = AsesorFichaDomain.reconstruir(
                asesorFichaId, "123", "Luis Perez", "luis@uco.edu.co", Instant.now(), UtilFecha.VACIO);
        observaciones = new ObservacionesRevisionItem(revisionItem, 3, 0);
    }

    @Test
    void debeActualizarEstadoYPublicarEvento_cuandoEsValido() {
        // Arrange
        stubFinders();

        // Act
        useCase.ejecutar(entrada);

        // Assert
        var orden = inOrder(pertenenciaRevisionItemFinder, fichaPerfilFinder, asesorDeFichaFinder,
                observacionesDeRevisionFinder, validator, revisionItemOutputPort, eventPublisher, logger);
        orden.verify(pertenenciaRevisionItemFinder, times(1))
                .obtener(new RevisionItemDeEstudiante(revisionItem, estudiante));
        orden.verify(fichaPerfilFinder, times(1)).obtener(fichaPerfilId);
        orden.verify(asesorDeFichaFinder, times(1)).obtener(fichaPerfilId);
        orden.verify(observacionesDeRevisionFinder, times(1)).obtener(revisionItem);
        orden.verify(validator).validar(entrada, pertenencia, ficha, asesor, observaciones);
        orden.verify(revisionItemOutputPort).actualizarEstado(revisionItem,
                EstadoRevision.EN_PROGRESO.getId(), EstadoRevision.CORRECCION_DISPONIBLE.getId());

        var captor = ArgumentCaptor.forClass(DomainEvent.class);
        orden.verify(eventPublisher).publish(captor.capture());
        orden.verify(logger).info(RevisionItemKey.LOG_CORRECCIONES_ENVIADAS, revisionItem);

        assertThat(captor.getValue()).isInstanceOfSatisfying(CorreccionesRevisionItemEnviadasEvent.class, evento -> {
            assertThat(evento.getRevisionItemId()).isEqualTo(revisionItem);
            assertThat(evento.getFichaPerfilId()).isEqualTo(fichaPerfilId);
            assertThat(evento.getTituloProyecto()).isEqualTo("Sistema de gestión");
            assertThat(evento.getAsesor().nombre()).isEqualTo("Luis Perez");
            assertThat(evento.getAsesor().email()).isEqualTo("luis@uco.edu.co");
        });
    }

    @Test
    void noDebeActualizarNiPublicar_cuandoElValidatorRechaza() {
        // Arrange
        stubFinders();
        doThrow(new RevisionItemConObservacionesPendientesException(revisionItem, 1))
                .when(validator).validar(entrada, pertenencia, ficha, asesor, observaciones);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemConObservacionesPendientesException.class);
        verify(revisionItemOutputPort, never()).actualizarEstado(any(), anyString(), anyString());
        verify(eventPublisher, never()).publish(any());
        verify(logger, never()).info(any(ClaveMensaje.class), eq(revisionItem));
    }

    private void stubFinders() {
        when(pertenenciaRevisionItemFinder.obtener(new RevisionItemDeEstudiante(revisionItem, estudiante)))
                .thenReturn(pertenencia);
        when(fichaPerfilFinder.obtener(fichaPerfilId)).thenReturn(ficha);
        when(asesorDeFichaFinder.obtener(fichaPerfilId)).thenReturn(asesor);
        when(observacionesDeRevisionFinder.obtener(revisionItem)).thenReturn(observaciones);
    }
}
