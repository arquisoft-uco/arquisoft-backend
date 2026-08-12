package com.arquisoft.fichas.application.itemfichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.finder.TipoItemEnFichaExisteFinder;
import com.arquisoft.fichas.application.itemfichaperfil.command.validator.AgregarItemFichaPerfilValidator;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.domain.itemfichaperfil.AgregacionItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.tipoitem.TipoItem;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarItemFichaPerfilUseCaseTest {

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @Mock
    private FichaPerfilExisteFinder fichaPerfilExisteFinder;

    @Mock
    private VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;

    @Mock
    private TipoItemEnFichaExisteFinder tipoItemEnFichaExisteFinder;

    @Mock
    private AgregarItemFichaPerfilValidator agregarItemFichaPerfilValidator;

    @Mock
    private AppLogger logger;

    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private AgregarItemFichaPerfilUseCaseImpl agregarItemFichaPerfilUseCase;

    private final UUID fichaPerfil = UUID.randomUUID();
    private final UUID estudiante = UUID.randomUUID();

    @Test
    void debeRegistrarElItem_cuandoDatosValidos() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, true, false);

        // Act
        UUID resultado = agregarItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(entrada.getItem().getId());
        verify(itemFichaPerfilOutputPort, times(1)).registrarItem(entrada.getItem());
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, true, false);

        // Act
        agregarItemFichaPerfilUseCase.ejecutar(entrada);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteFinder, vinculoEstudianteFichaExisteFinder,
                tipoItemEnFichaExisteFinder, agregarItemFichaPerfilValidator, itemFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilExisteFinder).obtener(fichaPerfil);
        inOrder.verify(vinculoEstudianteFichaExisteFinder)
                .obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante));
        inOrder.verify(tipoItemEnFichaExisteFinder).obtener(entrada.getItem());
        inOrder.verify(agregarItemFichaPerfilValidator)
                .validar(entrada.getItem(), estudiante, true, true, false);
        inOrder.verify(itemFichaPerfilOutputPort).registrarItem(entrada.getItem());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, false, false, false);
        doThrow(new FichaPerfilNoEncontradaException(fichaPerfil))
                .when(agregarItemFichaPerfilValidator)
                .validar(entrada.getItem(), estudiante, false, false, false);

        // Act & Assert
        assertThatThrownBy(() -> agregarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(itemFichaPerfilOutputPort, never()).registrarItem(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoEsDelEstudiante() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, false, false);
        doThrow(new ItemFichaNoPropiaException(fichaPerfil))
                .when(agregarItemFichaPerfilValidator)
                .validar(entrada.getItem(), estudiante, true, false, false);

        // Act & Assert
        assertThatThrownBy(() -> agregarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemFichaNoPropiaException.class);

        verify(itemFichaPerfilOutputPort, never()).registrarItem(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElTipoDeItemEstaDuplicado() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, true, true);
        doThrow(new ItemTipoDuplicadoException(TipoItem.OBJETIVO_GENERAL.getId()))
                .when(agregarItemFichaPerfilValidator)
                .validar(entrada.getItem(), estudiante, true, true, true);

        // Act & Assert
        assertThatThrownBy(() -> agregarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(ItemTipoDuplicadoException.class);

        verify(itemFichaPerfilOutputPort, never()).registrarItem(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var entrada = entrada();
        stubConsultas(entrada, true, true, false);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(itemFichaPerfilOutputPort).registrarItem(entrada.getItem());

        // Act & Assert
        assertThatThrownBy(() -> agregarItemFichaPerfilUseCase.ejecutar(entrada))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubConsultas(AgregacionItemFichaPerfilDomain entrada, boolean fichaExiste,
                               boolean esPropietario, boolean tipoYaExiste) {
        when(fichaPerfilExisteFinder.obtener(fichaPerfil)).thenReturn(fichaExiste);
        when(vinculoEstudianteFichaExisteFinder.obtener(new VinculoEstudianteFicha(fichaPerfil, estudiante)))
                .thenReturn(esPropietario);
        when(tipoItemEnFichaExisteFinder.obtener(entrada.getItem())).thenReturn(tipoYaExiste);
    }

    private AgregacionItemFichaPerfilDomain entrada() {
        return AgregacionItemFichaPerfilDomain.crear(
                fichaPerfil, TipoItem.OBJETIVO_GENERAL.getId(), "Contenido del item", estudiante);
    }
}
