package com.arquisoft.fichas.application.revisionitem.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.ContactosDeFichaFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.ItemFichaPerfilExisteFinder;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.RevisionItemOutputPort;
import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.RevisionItemEntity;
import com.arquisoft.fichas.application.revisionitem.command.validator.AgregarRevisionItemValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.ContactoEstudiante;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.event.RevisionItemAgregadoEvent;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemYaExisteException;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarRevisionItemUseCaseTest {

    @Mock
    private ItemFichaPerfilExisteFinder itemFichaPerfilExisteFinder;

    @Mock
    private FichaPerfilDelItemFinder fichaPerfilDelItemFinder;

    @Mock
    private FichaPerfilFinder fichaPerfilFinder;

    @Mock
    private RevisionesDelItemFinder revisionesDelItemFinder;

    @Mock
    private ContactosDeFichaFinder contactosDeFichaFinder;

    @Mock
    private AgregarRevisionItemValidator agregarRevisionItemValidator;

    @Mock
    private RevisionItemOutputPort revisionItemOutputPort;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private AgregarRevisionItemUseCaseImpl agregarRevisionItemUseCase;

    private final UUID asesorFicha = UUID.randomUUID();
    private final UUID otroAsesor = UUID.randomUUID();
    private final UUID fichaPerfilId = UUID.randomUUID();

    @Test
    void debeAgregarLaRevision_cuandoDatosValidos() {
        // Arrange
        var entrada = agregacionValida();
        stubConsultas(entrada, true, Optional.of(fichaPerfilId), asesorFicha, 0L);
        stubContactosDeFicha(fichaPerfilId);

        // Act
        UUID resultado = agregarRevisionItemUseCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(entrada.getRevisionItem().getId());
        verify(revisionItemOutputPort).registrarRevision(entidadDe(entrada.getRevisionItem()));
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var entrada = agregacionValida();
        stubConsultas(entrada, true, Optional.of(fichaPerfilId), asesorFicha, 0L);
        stubContactosDeFicha(fichaPerfilId);

        // Act
        agregarRevisionItemUseCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(itemFichaPerfilExisteFinder, fichaPerfilDelItemFinder,
                fichaPerfilFinder, revisionesDelItemFinder, agregarRevisionItemValidator,
                revisionItemOutputPort);
        inOrder.verify(itemFichaPerfilExisteFinder).obtener(entrada.getItem());
        inOrder.verify(fichaPerfilDelItemFinder).obtener(entrada.getItem());
        inOrder.verify(fichaPerfilFinder).obtener(fichaPerfilId);
        inOrder.verify(revisionesDelItemFinder).obtener(entrada.getItem());
        inOrder.verify(agregarRevisionItemValidator).validar(entrada, true, fichaPerfilId, asesorFicha, 0L);
        inOrder.verify(revisionItemOutputPort).registrarRevision(entidadDe(entrada.getRevisionItem()));
    }

    @Test
    void debePublicarElEventoConLosDatosDeLaRevision_cuandoSeAgregaLaRevision() {
        // Arrange
        var entrada = agregacionValida();
        stubConsultas(entrada, true, Optional.of(fichaPerfilId), asesorFicha, 0L);
        stubContactosDeFicha(fichaPerfilId);
        var revisionItem = entrada.getRevisionItem();

        // Act
        agregarRevisionItemUseCase.ejecutar(entrada);

        // Assert
        ArgumentCaptor<RevisionItemAgregadoEvent> captor =
                ArgumentCaptor.forClass(RevisionItemAgregadoEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().getRevisionItemId()).isEqualTo(revisionItem.getId());
        assertThat(captor.getValue().getItemId()).isEqualTo(revisionItem.getItem());
        assertThat(captor.getValue().getEstadoRevisionId()).isEqualTo(revisionItem.getEstadoRevision().getId());
        assertThat(captor.getValue().getEstadoRevisionNombre())
                .isEqualTo(revisionItem.getEstadoRevision().getNombre());
        assertThat(captor.getValue().getFechaCreacion()).isEqualTo(revisionItem.getFechaCreacion());
        assertThat(captor.getValue().getTituloProyecto()).isEqualTo("Sistema de gestión");
        assertThat(captor.getValue().getEstudiantes())
                .extracting("nombre", "email")
                .containsExactly(org.assertj.core.groups.Tuple.tuple("Ana Gomez", "ana.gomez@soyuco.edu.co"));
    }

    @Test
    void debeUsarUUIDPorDefecto_cuandoLaFichaPerfilDelItemNoExiste() {
        // Arrange — cuando el ítem no tiene ficha asociada, el use case cae al UUID por
        // defecto; la ficha resuelta con ese id no tiene como asesor al solicitante, así que
        // el validator real rechazaría la operación. El mock lo replica para no seguir de largo
        // hacia el enriquecimiento de notificación (que no aplica a este caso).
        var entrada = agregacionValida();
        stubConsultas(entrada, true, Optional.empty(), otroAsesor, 0L);
        doThrow(new FichaNoPerteneceAsesorException(UtilUUID.obtenerUUIDPorDefecto(), asesorFicha))
                .when(agregarRevisionItemValidator)
                .validar(entrada, true, UtilUUID.obtenerUUIDPorDefecto(), otroAsesor, 0L);

        // Act & Assert
        assertThatThrownBy(() -> agregarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);

        verify(agregarRevisionItemValidator).validar(entrada, true, UtilUUID.obtenerUUIDPorDefecto(), otroAsesor, 0L);
    }

    @Test
    void debeLanzarExcepcion_cuandoElItemNoExiste() {
        // Arrange
        var entrada = agregacionValida();
        stubConsultas(entrada, false, Optional.of(fichaPerfilId), asesorFicha, 0L);
        doThrow(new ItemFichaPerfilNoEncontradoException(entrada.getItem()))
                .when(agregarRevisionItemValidator).validar(entrada, false, fichaPerfilId, asesorFicha, 0L);

        // Act & Assert
        assertThatThrownBy(() -> agregarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);

        verify(revisionItemOutputPort, never()).registrarRevision(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorNoEsPropietario() {
        // Arrange
        var entrada = agregacionValida();
        stubConsultas(entrada, true, Optional.of(fichaPerfilId), otroAsesor, 0L);
        doThrow(new FichaNoPerteneceAsesorException(fichaPerfilId, asesorFicha))
                .when(agregarRevisionItemValidator).validar(entrada, true, fichaPerfilId, otroAsesor, 0L);

        // Act & Assert
        assertThatThrownBy(() -> agregarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(FichaNoPerteneceAsesorException.class);

        verify(revisionItemOutputPort, never()).registrarRevision(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoLaRevisionYaExiste() {
        // Arrange
        var entrada = agregacionValida();
        stubConsultas(entrada, true, Optional.of(fichaPerfilId), asesorFicha, 1L);
        doThrow(new RevisionItemYaExisteException(entrada.getItem()))
                .when(agregarRevisionItemValidator).validar(entrada, true, fichaPerfilId, asesorFicha, 1L);

        // Act & Assert
        assertThatThrownBy(() -> agregarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(RevisionItemYaExisteException.class);

        verify(revisionItemOutputPort, never()).registrarRevision(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoElRepositorioFalla() {
        // Arrange
        var entrada = agregacionValida();
        stubConsultas(entrada, true, Optional.of(fichaPerfilId), asesorFicha, 0L);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(revisionItemOutputPort).registrarRevision(entidadDe(entrada.getRevisionItem()));

        // Act & Assert
        assertThatThrownBy(() -> agregarRevisionItemUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);

        verify(eventPublisher, never()).publish(any());
    }

    private void stubConsultas(AgregacionRevisionItemDomain entrada, boolean itemExiste,
                                Optional<UUID> fichaPerfilDelItem, UUID asesorDeLaFicha, long revisiones) {
        when(itemFichaPerfilExisteFinder.obtener(entrada.getItem())).thenReturn(itemExiste);
        when(fichaPerfilDelItemFinder.obtener(entrada.getItem())).thenReturn(fichaPerfilDelItem);
        UUID fichaResuelta = fichaPerfilDelItem.orElse(UtilUUID.obtenerUUIDPorDefecto());
        var ficha = FichaPerfilDomain.crear("Sistema de gestión", asesorDeLaFicha);
        when(fichaPerfilFinder.obtener(fichaResuelta)).thenReturn(Optional.of(ficha));
        when(revisionesDelItemFinder.obtener(entrada.getItem())).thenReturn(revisiones);
    }

    private void stubContactosDeFicha(UUID fichaPerfil) {
        when(contactosDeFichaFinder.obtener(fichaPerfil))
                .thenReturn(List.of(new ContactoEstudiante("Ana Gomez", "ana.gomez@soyuco.edu.co")));
    }

    private AgregacionRevisionItemDomain agregacionValida() {
        var revisionItem = RevisionItemDomain.crear(UUID.randomUUID());
        return AgregacionRevisionItemDomain.crear(revisionItem, asesorFicha);
    }

    private static RevisionItemEntity entidadDe(RevisionItemDomain revisionItem) {
        return argThat(entity -> entity.id().equals(revisionItem.getId())
                && entity.item().equals(revisionItem.getItem())
                && entity.estadoRevision().equals(revisionItem.getEstadoRevision().getId())
                && entity.fechaCreacion().equals(revisionItem.getFechaCreacion()));
    }
}
