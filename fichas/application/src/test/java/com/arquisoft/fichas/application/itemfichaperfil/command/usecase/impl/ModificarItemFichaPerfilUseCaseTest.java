package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.FichaPerfilDelItemFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.ModificacionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarItemFichaPerfilUseCaseTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Mock
    private FichaPerfilDelItemFinder fichaPerfilDelItemFinder;

    @Mock
    private VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;

    @Mock
    private EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;

    @Mock
    private ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;

    @Mock
    private AppLogger logger;
    @InjectMocks
    private ModificarItemFichaPerfilUseCaseImpl modificarItemFichaPerfilUseCase;

    private final UUID item = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();
    private final UUID fichaPerfil = UUID.randomUUID();
    private final EstadoFichaPerfilDomain estadoEnConstruccion =
            EstadoFichaPerfilDomain.crear(fichaPerfil);

    @Test
    void debeActualizarElContenido_cuandoDatosValidos() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(true, estadoEnConstruccion);

        // Act
        modificarItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        verify(itemFichaPerfilOutputPort, times(1)).actualizarContenido(item, entrada.getContenido());
    }

    @Test
    void debeResolverLaFichaDelItemAntesDeValidar_cuandoSeEjecuta() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(true, estadoEnConstruccion);

        // Act
        modificarItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilDelItemFinder, vinculoEstudianteFichaExisteFinder,
                estadoActualFichaPerfilFinder, modificarItemFichaPerfilValidator, itemFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilDelItemFinder).obtener(item);
        inOrder.verify(vinculoEstudianteFichaExisteFinder)
                .obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante));
        inOrder.verify(estadoActualFichaPerfilFinder).obtener(fichaPerfil);
        inOrder.verify(modificarItemFichaPerfilValidator).validar(
                item, estudiante, fichaPerfil, true, true,
                estadoEnConstruccion);
        inOrder.verify(itemFichaPerfilOutputPort).actualizarContenido(item, entrada.getContenido());
    }

    @Test
    void noDebeConsultarPropiedadNiEstado_cuandoElItemNoExiste() {
        // Arrange
        var entrada = entrada();
        when(fichaPerfilDelItemFinder.obtener(item)).thenReturn(Optional.empty());
        doThrow(new ItemFichaPerfilNoEncontradoException(item))
                .when(modificarItemFichaPerfilValidator)
                .validar(item, estudiante, UtilUUID.obtenerUUIDPorDefecto(), false, false,
                        EstadoFichaPerfilDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> modificarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);

        verify(vinculoEstudianteFichaExisteFinder, never()).obtener(any());
        verify(estadoActualFichaPerfilFinder, never()).obtener(any());
        verify(itemFichaPerfilOutputPort, never()).actualizarContenido(any(), anyString());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoEsDelEstudiante() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(false, estadoEnConstruccion);
        doThrow(new ItemFichaNoPropiaException(fichaPerfil))
                .when(modificarItemFichaPerfilValidator).validar(
                        item, estudiante, fichaPerfil, true, false,
                        estadoEnConstruccion);

        // Act & Assert
        assertThatThrownBy(() -> modificarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemFichaNoPropiaException.class);

        verify(itemFichaPerfilOutputPort, never()).actualizarContenido(any(), anyString());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var entrada = entrada();
        stubConsultasConFicha(true, estadoEnConstruccion);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(itemFichaPerfilOutputPort).actualizarContenido(item, entrada.getContenido());

        // Act & Assert
        assertThatThrownBy(() -> modificarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubConsultasConFicha(boolean esPropietario, EstadoFichaPerfilDomain estadoActual) {
        when(fichaPerfilDelItemFinder.obtener(item)).thenReturn(Optional.of(fichaPerfil));
        when(vinculoEstudianteFichaExisteFinder.obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante)))
                .thenReturn(esPropietario);
        when(estadoActualFichaPerfilFinder.obtener(fichaPerfil)).thenReturn(Optional.of(estadoActual));
    }

    private ModificacionItemFichaPerfilDomain entrada() {
        return ModificacionItemFichaPerfilDomain.crear(item, "Contenido modificado", estudiante);
    }
}
