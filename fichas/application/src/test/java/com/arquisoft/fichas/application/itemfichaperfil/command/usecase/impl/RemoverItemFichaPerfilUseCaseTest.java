package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.RemocionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemConRevisionesException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoverItemFichaPerfilUseCaseTest {

    @Mock
    private ItemFichaPerfilOutputPort itemOutputPort;

    @Mock
    private FichaPerfilDelItemFinder fichaPerfilDelItemFinder;

    @Mock
    private VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;

    @Mock
    private RevisionesDelItemFinder revisionesDelItemFinder;

    @Mock
    private RemoverItemFichaPerfilValidator removerItemFichaPerfilValidator;

    @Mock
    private AppLogger logger;
    @InjectMocks
    private RemoverItemFichaPerfilUseCaseImpl removerItemFichaPerfilUseCase;

    private final UUID item = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();
    private final UUID fichaPerfil = UUID.randomUUID();

    @Test
    void debeRemoverElItem_cuandoDatosValidos() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(true, 0L);

        // Act
        removerItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        verify(itemOutputPort, times(1)).removerItem(item);
    }

    @Test
    void debeResolverLaFichaDelItemAntesDeValidar_cuandoSeEjecuta() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(true, 0L);

        // Act
        removerItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilDelItemFinder, vinculoEstudianteFichaExisteFinder,
                revisionesDelItemFinder, removerItemFichaPerfilValidator, itemOutputPort);
        inOrder.verify(fichaPerfilDelItemFinder).obtener(item);
        inOrder.verify(vinculoEstudianteFichaExisteFinder)
                .obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante));
        inOrder.verify(revisionesDelItemFinder).obtener(item);
        inOrder.verify(removerItemFichaPerfilValidator)
                .validar(item, estudiante, fichaPerfil, true, true, 0L);
        inOrder.verify(itemOutputPort).removerItem(item);
    }

    @Test
    void noDebeConsultarPropiedad_cuandoElItemNoExiste() {
        // Arrange
        var entrada = entrada();
        when(fichaPerfilDelItemFinder.obtener(item)).thenReturn(Optional.empty());
        when(revisionesDelItemFinder.obtener(item)).thenReturn(0L);
        doThrow(new ItemFichaPerfilNoEncontradoException(item))
                .when(removerItemFichaPerfilValidator)
                .validar(item, estudiante, UtilUUID.obtenerUUIDPorDefecto(), false, false, 0L);

        // Act & Assert
        assertThatThrownBy(() -> removerItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);

        verify(vinculoEstudianteFichaExisteFinder, never()).obtener(any());
        verify(itemOutputPort, never()).removerItem(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElEstudianteNoEsPropietario() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(false, 0L);
        doThrow(new FichaNoPropietarioException(fichaPerfil, estudiante))
                .when(removerItemFichaPerfilValidator)
                .validar(item, estudiante, fichaPerfil, true, false, 0L);

        // Act & Assert
        assertThatThrownBy(() -> removerItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(FichaNoPropietarioException.class);

        verify(itemOutputPort, never()).removerItem(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElItemTieneRevisiones() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(true, 2L);
        doThrow(new ItemConRevisionesException(item))
                .when(removerItemFichaPerfilValidator)
                .validar(item, estudiante, fichaPerfil, true, true, 2L);

        // Act & Assert
        assertThatThrownBy(() -> removerItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemConRevisionesException.class);

        verify(itemOutputPort, never()).removerItem(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(true, 0L);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(itemOutputPort).removerItem(item);

        // Act & Assert
        assertThatThrownBy(() -> removerItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubConsultasConFicha(boolean esPropietario, long totalRevisiones) {
        when(fichaPerfilDelItemFinder.obtener(item)).thenReturn(Optional.of(fichaPerfil));
        when(vinculoEstudianteFichaExisteFinder.obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante)))
                .thenReturn(esPropietario);
        when(revisionesDelItemFinder.obtener(item)).thenReturn(totalRevisiones);
    }

    private RemocionItemFichaPerfilDomain entrada() {
        return RemocionItemFichaPerfilDomain.crear(item, estudiante);
    }
}
