package com.arquisoft.fichas.application.fichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.finder.VinculoEstudianteFichaExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.finder.TituloEnOtraFichaExisteFinder;
import com.arquisoft.fichas.application.fichaperfil.command.validator.ModificarFichaPerfilValidator;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.EstadoFichaPerfilTerminalException;
import com.arquisoft.fichas.domain.estudiantefichaperfil.model.VinculoEstudianteFicha;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.application.fichaperfil.command.secondaryport.FichaPerfilOutputPort;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
class ModificarFichaPerfilUseCaseTest {

    @Mock
    private FichaPerfilOutputPort fichaPerfilOutputPort;

    @Mock
    private VinculoEstudianteFichaExisteFinder vinculoEstudianteFichaExisteFinder;

    @Mock
    private TituloEnOtraFichaExisteFinder tituloEnOtraFichaExisteFinder;

    @Mock
    private EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;

    @Mock
    private ModificarFichaPerfilValidator modificarFichaPerfilValidator;

    @Mock
    private AppLogger logger;
    @InjectMocks
    private ModificarFichaPerfilUseCaseImpl modificarFichaPerfilUseCase;

    private final EstadoFichaPerfilDomain estadoEnConstruccion = EstadoFichaPerfilDomain.crear(UUID.randomUUID());

    @Test
    void debeActualizarElTitulo_cuandoDatosValidos() {
        // Arrange
        var modificacion = modificacionValida();
        stubConsultas(modificacion, true, false);
        stubEstado(estadoEnConstruccion);

        // Act
        modificarFichaPerfilUseCase.ejecutar(modificacion);

        // Assert
        verify(fichaPerfilOutputPort, times(1))
                .actualizarTitulo(modificacion.getFichaPerfil(), modificacion.getTituloProyecto());
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var modificacion = modificacionValida();
        stubConsultas(modificacion, true, false);
        stubEstado(estadoEnConstruccion);

        // Act
        modificarFichaPerfilUseCase.ejecutar(modificacion);

        // Assert
        InOrder inOrder = inOrder(vinculoEstudianteFichaExisteFinder, estadoActualFichaPerfilFinder,
                tituloEnOtraFichaExisteFinder,
                modificarFichaPerfilValidator, fichaPerfilOutputPort);
        inOrder.verify(vinculoEstudianteFichaExisteFinder).obtener(
                new VinculoEstudianteFicha(modificacion.getFichaPerfil(), modificacion.getEstudiante()));
        inOrder.verify(estadoActualFichaPerfilFinder).obtener(modificacion.getFichaPerfil());
        inOrder.verify(tituloEnOtraFichaExisteFinder).obtener(modificacion);
        inOrder.verify(modificarFichaPerfilValidator).validar(modificacion, true, estadoEnConstruccion, false);
        inOrder.verify(fichaPerfilOutputPort)
                .actualizarTitulo(modificacion.getFichaPerfil(), modificacion.getTituloProyecto());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElEstudianteNoEsPropietario() {
        // Arrange
        var modificacion = modificacionValida();
        stubConsultas(modificacion, false, false);
        doThrow(new FichaNoPropietarioException(modificacion.getFichaPerfil(), modificacion.getEstudiante()))
                .when(modificarFichaPerfilValidator).validar(modificacion, false, EstadoFichaPerfilDomain.VACIO, false);

        // Act & Assert
        assertThatThrownBy(() -> modificarFichaPerfilUseCase.ejecutar(modificacion))
                .isInstanceOf(FichaNoPropietarioException.class);

        verify(fichaPerfilOutputPort, never()).actualizarTitulo(any(), anyString());
    }

    @Test
    void debePropagarLaExcepcionSinActualizar_cuandoLaFichaEstaEnEstadoTerminal() {
        // Arrange
        var modificacion = modificacionValida();
        var aprobada = EstadoFichaPerfilDomain.reconstruir(
                UUID.randomUUID(), modificacion.getFichaPerfil(), EstadoFicha.APROBADA, Instant.now());
        stubConsultas(modificacion, true, false);
        stubEstado(aprobada);
        doThrow(new EstadoFichaPerfilTerminalException(EstadoFicha.APROBADA))
                .when(modificarFichaPerfilValidator).validar(modificacion, true, aprobada, false);

        // Act & Assert
        assertThatThrownBy(() -> modificarFichaPerfilUseCase.ejecutar(modificacion))
                .isInstanceOf(EstadoFichaPerfilTerminalException.class);

        verify(fichaPerfilOutputPort, never()).actualizarTitulo(any(), anyString());
    }

    @Test
    void noDebeConsultarElEstado_cuandoElEstudianteNoEsPropietario() {
        // Arrange
        var modificacion = modificacionValida();
        stubConsultas(modificacion, false, false);

        // Act
        modificarFichaPerfilUseCase.ejecutar(modificacion);

        // Assert
        verify(estadoActualFichaPerfilFinder, never()).obtener(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoOtraFichaTieneEseTitulo() {
        // Arrange
        var modificacion = modificacionValida();
        stubConsultas(modificacion, true, true);
        stubEstado(estadoEnConstruccion);
        doThrow(new FichaTituloDuplicadoException(modificacion.getTituloProyecto()))
                .when(modificarFichaPerfilValidator).validar(modificacion, true, estadoEnConstruccion, true);

        // Act & Assert
        assertThatThrownBy(() -> modificarFichaPerfilUseCase.ejecutar(modificacion))
                .isInstanceOf(FichaTituloDuplicadoException.class);

        verify(fichaPerfilOutputPort, never()).actualizarTitulo(any(), anyString());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var modificacion = modificacionValida();
        stubConsultas(modificacion, true, false);
        stubEstado(estadoEnConstruccion);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(fichaPerfilOutputPort)
                .actualizarTitulo(modificacion.getFichaPerfil(), modificacion.getTituloProyecto());

        // Act & Assert
        assertThatThrownBy(() -> modificarFichaPerfilUseCase.ejecutar(modificacion))
                .isInstanceOf(InfrastructureException.class);
    }

    private void stubEstado(EstadoFichaPerfilDomain estado) {
        when(estadoActualFichaPerfilFinder.obtener(any())).thenReturn(estado);
    }

    private void stubConsultas(ModificacionFichaPerfilDomain modificacion, boolean esPropietario,
                               boolean tituloYaExiste) {
        when(vinculoEstudianteFichaExisteFinder.obtener(
                new VinculoEstudianteFicha(modificacion.getFichaPerfil(), modificacion.getEstudiante())))
                .thenReturn(esPropietario);
        when(tituloEnOtraFichaExisteFinder.obtener(modificacion)).thenReturn(tituloYaExiste);
    }

    private ModificacionFichaPerfilDomain modificacionValida() {
        return ModificacionFichaPerfilDomain.crear(UUID.randomUUID(), "Título modificado", UUID.randomUUID());
    }
}
