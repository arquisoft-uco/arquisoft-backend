package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.PertenenciaItemFichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.ModificarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.ModificacionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemDeEstudiante;
import com.arquisoft.fichas.domain.itemfichaperfil.model.PertenenciaItemFichaPerfil;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private PertenenciaItemFichaPerfilFinder pertenenciaItemFichaPerfilFinder;

    @Mock
    private ModificarItemFichaPerfilValidator modificarItemFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ModificarItemFichaPerfilUseCaseImpl modificarItemFichaPerfilUseCase;

    private final UUID item = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();
    private final UUID fichaPerfil = UUID.randomUUID();
    private final EstadoFichaPerfilDomain estadoEnConstruccion = EstadoFichaPerfilDomain.crear(fichaPerfil);

    @Test
    void debeActualizarElContenido_cuandoDatosValidos() {
        // Arrange
        var entrada = entrada();
        stubPertenencia(new PertenenciaItemFichaPerfil(fichaPerfil, true, estadoEnConstruccion));

        // Act
        modificarItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        verify(itemFichaPerfilOutputPort, times(1)).actualizarContenido(item, entrada.getContenido());
    }

    @Test
    void debeConsultarLaPertenenciaUnaSolaVezAntesDeValidar_cuandoSeEjecuta() {
        // Arrange
        var entrada = entrada();
        stubPertenencia(new PertenenciaItemFichaPerfil(fichaPerfil, true, estadoEnConstruccion));

        // Act
        modificarItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        var inOrder = inOrder(pertenenciaItemFichaPerfilFinder, modificarItemFichaPerfilValidator,
                itemFichaPerfilOutputPort);
        inOrder.verify(pertenenciaItemFichaPerfilFinder, times(1)).obtener(new ItemDeEstudiante(item, estudiante));
        inOrder.verify(modificarItemFichaPerfilValidator).validar(
                item, estudiante, fichaPerfil, true, true, estadoEnConstruccion);
        inOrder.verify(itemFichaPerfilOutputPort).actualizarContenido(item, entrada.getContenido());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElItemNoExiste() {
        // Arrange
        var entrada = entrada();
        stubPertenencia(PertenenciaItemFichaPerfil.VACIO);
        doThrow(new ItemFichaPerfilNoEncontradoException(item))
                .when(modificarItemFichaPerfilValidator)
                .validar(item, estudiante, UtilUUID.obtenerUUIDPorDefecto(), false, false,
                        EstadoFichaPerfilDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> modificarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);

        verify(itemFichaPerfilOutputPort, never()).actualizarContenido(any(), anyString());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoEsDelEstudiante() {
        // Arrange
        var entrada = entrada();
        stubPertenencia(new PertenenciaItemFichaPerfil(fichaPerfil, false, estadoEnConstruccion));
        doThrow(new ItemFichaNoPropiaException(fichaPerfil))
                .when(modificarItemFichaPerfilValidator).validar(
                        item, estudiante, fichaPerfil, true, false, estadoEnConstruccion);

        // Act & Assert
        assertThatThrownBy(() -> modificarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemFichaNoPropiaException.class);

        verify(itemFichaPerfilOutputPort, never()).actualizarContenido(any(), anyString());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var entrada = entrada();
        stubPertenencia(new PertenenciaItemFichaPerfil(fichaPerfil, true, estadoEnConstruccion));
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(itemFichaPerfilOutputPort).actualizarContenido(item, entrada.getContenido());

        // Act & Assert
        assertThatThrownBy(() -> modificarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubPertenencia(PertenenciaItemFichaPerfil pertenencia) {
        when(pertenenciaItemFichaPerfilFinder.obtener(new ItemDeEstudiante(item, estudiante)))
                .thenReturn(pertenencia);
    }

    private ModificacionItemFichaPerfilDomain entrada() {
        return ModificacionItemFichaPerfilDomain.crear(item, "Contenido modificado", estudiante);
    }
}
