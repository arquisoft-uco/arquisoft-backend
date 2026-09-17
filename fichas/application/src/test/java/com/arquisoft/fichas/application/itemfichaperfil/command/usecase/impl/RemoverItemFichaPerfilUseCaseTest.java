package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.itemfichaperfil.command.finder.PertenenciaItemFichaPerfilFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.RemoverItemFichaPerfilValidator;
import com.arquisoft.fichas.application.revisionitem.command.finder.RevisionesDelItemFinder;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.itemfichaperfil.RemocionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemConRevisionesException;
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
    private PertenenciaItemFichaPerfilFinder pertenenciaItemFichaPerfilFinder;

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
        stubConsultas(pertenencia(true), 0L);

        // Act
        removerItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        verify(itemOutputPort, times(1)).removerItem(item);
    }

    @Test
    void debeConsultarLaPertenenciaUnaSolaVezAntesDeValidar_cuandoSeEjecuta() {
        // Arrange
        var entrada = entrada();
        stubConsultas(pertenencia(true), 0L);

        // Act
        removerItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        var inOrder = inOrder(pertenenciaItemFichaPerfilFinder, revisionesDelItemFinder,
                removerItemFichaPerfilValidator, itemOutputPort);
        inOrder.verify(pertenenciaItemFichaPerfilFinder, times(1)).obtener(new ItemDeEstudiante(item, estudiante));
        inOrder.verify(revisionesDelItemFinder, times(1)).obtener(item);
        inOrder.verify(removerItemFichaPerfilValidator)
                .validar(item, estudiante, fichaPerfil, true, true, 0L);
        inOrder.verify(itemOutputPort).removerItem(item);
    }

    @Test
    void debePropagarLaExcepcion_cuandoElItemNoExiste() {
        // Arrange
        var entrada = entrada();
        stubConsultas(PertenenciaItemFichaPerfil.VACIO, 0L);
        doThrow(new ItemFichaPerfilNoEncontradoException(item))
                .when(removerItemFichaPerfilValidator)
                .validar(item, estudiante, UtilUUID.obtenerUUIDPorDefecto(), false, false, 0L);

        // Act & Assert
        assertThatThrownBy(() -> removerItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemFichaPerfilNoEncontradoException.class);

        verify(itemOutputPort, never()).removerItem(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElEstudianteNoEsPropietario() {
        // Arrange
        var entrada = entrada();
        stubConsultas(pertenencia(false), 0L);
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
        stubConsultas(pertenencia(true), 2L);
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
        stubConsultas(pertenencia(true), 0L);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(itemOutputPort).removerItem(item);

        // Act & Assert
        assertThatThrownBy(() -> removerItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);
    }

    private PertenenciaItemFichaPerfil pertenencia(boolean esPropietario) {
        return new PertenenciaItemFichaPerfil(fichaPerfil, esPropietario, EstadoFichaPerfilDomain.crear(fichaPerfil));
    }

    private void stubConsultas(PertenenciaItemFichaPerfil pertenencia, long totalRevisiones) {
        when(pertenenciaItemFichaPerfilFinder.obtener(new ItemDeEstudiante(item, estudiante)))
                .thenReturn(pertenencia);
        when(revisionesDelItemFinder.obtener(item)).thenReturn(totalRevisiones);
    }

    private RemocionItemFichaPerfilDomain entrada() {
        return RemocionItemFichaPerfilDomain.crear(item, estudiante);
    }
}
