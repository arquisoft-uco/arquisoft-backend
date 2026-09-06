package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.ContactosDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.validator.ModificarRevisionItemValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.revisionitem.ModificacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.event.RevisionItemModificadoEvent;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEncontradaException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarRevisionItemUseCaseImplTest {

    @Mock
    private RevisionesDelItemFinder revisionesDelItemFinder;

    @Mock
    private FichaPerfilDelItemFinder fichaPerfilDelItemFinder;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private ContactosDeFichaFinder contactosDeFichaFinder;

    @Mock
    private ModificarRevisionItemValidator modificarRevisionItemValidator;

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarRevisionItemUseCaseImpl modificarRevisionItemUseCase;

    private static final List<ContactoEstudiante> ESTUDIANTES =
            List.of(new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co"));

    private final UUID asesorFicha = UUID.randomUUID();
    private final UUID fichaPerfilId = UUID.randomUUID();

    @Test
    void debeActualizarElEstado_cuandoDatosValidos() {
        // Arrange
        var entrada = modificacionValida();
        stubConsultas(entrada, Optional.of(fichaPerfilId), asesorFicha, 1L);
        stubContactosDeFicha(fichaPerfilId);

        // Act
        modificarRevisionItemUseCase.ejecutar(entrada);

        // Assert
        verify(revisionItemOutputPort).actualizarEstado(entrada.getItem(), entrada.getEstadoRevision().getId());
    }

    @Test
    void debeActualizarElEstado_cuandoEsElMismoValorYaVigente() {
        // Arrange — no-op válido: no hay rama especial, el flujo es idéntico al caso exitoso
        var entrada = modificacionValida();
        stubConsultas(entrada, Optional.of(fichaPerfilId), asesorFicha, 1L);
        stubContactosDeFicha(fichaPerfilId);

        // Act
        modificarRevisionItemUseCase.ejecutar(entrada);

        // Assert
        verify(revisionItemOutputPort).actualizarEstado(entrada.getItem(), entrada.getEstadoRevision().getId());
        verify(eventPublisher).publish(any(RevisionItemModificadoEvent.class));
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var entrada = modificacionValida();
        stubConsultas(entrada, Optional.of(fichaPerfilId), asesorFicha, 1L);
        stubContactosDeFicha(fichaPerfilId);

        // Act
        modificarRevisionItemUseCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(revisionesDelItemFinder, fichaPerfilDelItemFinder, fichaPerfilFinder,
                modificarRevisionItemValidator, revisionItemOutputPort);
        inOrder.verify(revisionesDelItemFinder).obtener(entrada.getItem());
        inOrder.verify(fichaPerfilDelItemFinder).obtener(entrada.getItem());
        inOrder.verify(fichaPerfilFinder).obtener(fichaPerfilId);
        inOrder.verify(modificarRevisionItemValidator).validar(entrada, 1L, fichaPerfilId, asesorFicha);
        inOrder.verify(revisionItemOutputPort).actualizarEstado(entrada.getItem(), entrada.getEstadoRevision().getId());
    }

    @Test
    void debePublicarElEventoConLosDatosDeLaModificacion_cuandoSeActualizaElEstado() {
        // Arrange
        var entrada = modificacionValida();
        stubConsultas(entrada, Optional.of(fichaPerfilId), asesorFicha, 1L);
        stubContactosDeFicha(fichaPerfilId);

        // Act
        modificarRevisionItemUseCase.ejecutar(entrada);

        // Assert
        ArgumentCaptor<RevisionItemModificadoEvent> captor =
                ArgumentCaptor.forClass(RevisionItemModificadoEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThatContieneLosDatos(captor.getValue(), entrada);
    }

    @Test
    void debeUsarUUIDPorDefecto_cuandoLaFichaPerfilDelItemNoExiste() {
        // Arrange — cuando el ítem no tiene ficha asociada, el use case cae al UUID por
        // defecto; la ficha resuelta con ese id no tiene como asesor al solicitante, así que
        // el validator real rechazaría la operación. El mock lo replica para no seguir de largo.
        var entrada = modificacionValida();
        UUID otroAsesor = UUID.randomUUID();
        stubConsultas(entrada, Optional.empty(), otroAsesor, 1L);
        doThrow(new FichaNoPerteneceAsesorException(UtilUUID.obtenerUUIDPorDefecto(), asesorFicha))
                .when(modificarRevisionItemValidator)
                .validar(entrada, 1L, UtilUUID.obtenerUUIDPorDefecto(), otroAsesor);

        // Act & Assert
        assertThatThrownBy(() -> modificarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);

        verify(modificarRevisionItemValidator).validar(entrada, 1L, UtilUUID.obtenerUUIDPorDefecto(), otroAsesor);
    }

    @Test
    void debeLanzarExcepcion_cuandoLaRevisionNoExiste() {
        // Arrange
        var entrada = modificacionValida();
        stubConsultas(entrada, Optional.of(fichaPerfilId), asesorFicha, 0L);
        doThrow(new RevisionItemNoEncontradaException(entrada.getItem()))
                .when(modificarRevisionItemValidator).validar(entrada, 0L, fichaPerfilId, asesorFicha);

        // Act & Assert
        assertThatThrownBy(() -> modificarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemNoEncontradaException.class);

        verify(revisionItemOutputPort, never()).actualizarEstado(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorNoEsPropietario() {
        // Arrange
        var entrada = modificacionValida();
        UUID otroAsesor = UUID.randomUUID();
        stubConsultas(entrada, Optional.of(fichaPerfilId), otroAsesor, 1L);
        doThrow(new FichaNoPerteneceAsesorException(fichaPerfilId, asesorFicha))
                .when(modificarRevisionItemValidator).validar(entrada, 1L, fichaPerfilId, otroAsesor);

        // Act & Assert
        assertThatThrownBy(() -> modificarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);

        verify(revisionItemOutputPort, never()).actualizarEstado(any(), any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoElRepositorioFalla() {
        // Arrange
        var entrada = modificacionValida();
        stubConsultas(entrada, Optional.of(fichaPerfilId), asesorFicha, 1L);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(revisionItemOutputPort)
                .actualizarEstado(entrada.getItem(), entrada.getEstadoRevision().getId());

        // Act & Assert
        assertThatThrownBy(() -> modificarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);

        verify(eventPublisher, never()).publish(any());
    }

    private void stubConsultas(ModificacionRevisionItemDomain entrada, Optional<UUID> fichaPerfilDelItem,
                                UUID asesorDeLaFicha, long revisiones) {
        when(revisionesDelItemFinder.obtener(entrada.getItem())).thenReturn(revisiones);
        when(fichaPerfilDelItemFinder.obtener(entrada.getItem())).thenReturn(fichaPerfilDelItem);
        UUID fichaResuelta = fichaPerfilDelItem.orElse(UtilUUID.obtenerUUIDPorDefecto());
        var ficha = FichaPerfilDomain.crear("Título de prueba", asesorDeLaFicha);
        when(fichaPerfilFinder.obtener(fichaResuelta)).thenReturn(Optional.of(ficha));
    }

    private void stubContactosDeFicha(UUID fichaPerfil) {
        when(contactosDeFichaFinder.obtener(fichaPerfil)).thenReturn(ESTUDIANTES);
    }

    private static void assertThatContieneLosDatos(RevisionItemModificadoEvent evento,
                                                     ModificacionRevisionItemDomain entrada) {
        assertThat(evento.getItemId()).isEqualTo(entrada.getItem());
        assertThat(evento.getEstadoRevisionId()).isEqualTo(entrada.getEstadoRevision().getId());
        assertThat(evento.getEstadoRevisionNombre()).isEqualTo(entrada.getEstadoRevision().getNombre());
        assertThat(evento.getTituloProyecto()).isEqualTo("Título de prueba");
        assertThat(evento.getEstudiantes()).isEqualTo(ESTUDIANTES);
    }

    private ModificacionRevisionItemDomain modificacionValida() {
        return ModificacionRevisionItemDomain.crear(UUID.randomUUID(), "VISUALIZADA", asesorFicha);
    }
}
