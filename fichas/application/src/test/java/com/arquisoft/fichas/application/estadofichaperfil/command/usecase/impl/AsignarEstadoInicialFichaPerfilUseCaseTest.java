package com.arquisoft.fichas.application.estadofichaperfil.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.fichas.application.estadofichaperfil.command.validator.AsignarEstadoInicialFichaPerfilValidator;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.usecase.AsignarEstudiantesFichaPerfilUseCase;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.AgregacionEstudiantesFichaPerfilDomain;
import com.arquisoft.fichas.domain.estudiantefichaperfil.EstudianteFichaPerfilDomain;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.RegistroFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignarEstadoInicialFichaPerfilUseCaseTest {

    @Mock
    private EstadoFichaPerfilOutputPort estadoFichaPerfilOutputPort;

    @Mock
    private FichaPerfilExisteFinder fichaPerfilExisteFinder;

    @Mock
    private AsignarEstadoInicialFichaPerfilValidator asignarEstadoInicialFichaPerfilValidator;

    @Mock
    private AsignarEstudiantesFichaPerfilUseCase asignarEstudiantesFichaPerfilUseCase;

    @Mock
    private AppLogger logger;

    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private AsignarEstadoInicialFichaPerfilUseCaseImpl asignarEstadoInicialFichaPerfilUseCase;

    @Test
    void debeRegistrarElEstadoInicial_cuandoLaFichaExiste() {
        // Arrange
        RegistroFichaPerfilDomain registro = registro();
        stubFichaExiste(registro, true);

        // Act
        asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro);

        // Assert
        verify(estadoFichaPerfilOutputPort, times(1))
                .registrarEstadoInicial(entidadDe(registro.getEstadoInicial()));
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        RegistroFichaPerfilDomain registro = registro();
        var estadoInicial = registro.getEstadoInicial();
        stubFichaExiste(registro, true);

        // Act
        asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteFinder, asignarEstadoInicialFichaPerfilValidator,
                estadoFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilExisteFinder).obtener(estadoInicial.getFichaPerfil());
        inOrder.verify(asignarEstadoInicialFichaPerfilValidator)
                .validar(estadoInicial.getFichaPerfil(), true);
        inOrder.verify(estadoFichaPerfilOutputPort).registrarEstadoInicial(entidadDe(estadoInicial));
    }

    @Test
    void debeAsignarLosEstudiantes_despuesDePersistirElEstadoInicial() {
        // Arrange
        RegistroFichaPerfilDomain registro = registro();
        stubFichaExiste(registro, true);

        // Act
        asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro);

        // Assert
        verify(asignarEstudiantesFichaPerfilUseCase).ejecutar(registro.getEstudiantes());

        InOrder inOrder = inOrder(estadoFichaPerfilOutputPort, asignarEstudiantesFichaPerfilUseCase);
        inOrder.verify(estadoFichaPerfilOutputPort).registrarEstadoInicial(any());
        inOrder.verify(asignarEstudiantesFichaPerfilUseCase).ejecutar(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        RegistroFichaPerfilDomain registro = registro();
        var estadoInicial = registro.getEstadoInicial();
        stubFichaExiste(registro, false);
        doThrow(new FichaPerfilNoEncontradaException(estadoInicial.getFichaPerfil()))
                .when(asignarEstadoInicialFichaPerfilValidator)
                .validar(estadoInicial.getFichaPerfil(), false);

        // Act & Assert
        assertThatThrownBy(() -> asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(estadoFichaPerfilOutputPort, never()).registrarEstadoInicial(any());
        verify(asignarEstudiantesFichaPerfilUseCase, never()).ejecutar(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        RegistroFichaPerfilDomain registro = registro();
        stubFichaExiste(registro, true);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(estadoFichaPerfilOutputPort)
                .registrarEstadoInicial(entidadDe(registro.getEstadoInicial()));

        // Act & Assert
        assertThatThrownBy(() -> asignarEstadoInicialFichaPerfilUseCase.ejecutar(registro))
                .isInstanceOf(InfrastructureException.class);

        verify(asignarEstudiantesFichaPerfilUseCase, never()).ejecutar(any());
    }

    private void stubFichaExiste(RegistroFichaPerfilDomain registro, boolean existe) {
        when(fichaPerfilExisteFinder.obtener(registro.getEstadoInicial().getFichaPerfil()))
                .thenReturn(existe);
    }

    private static RegistroFichaPerfilDomain registro() {
        var ficha = FichaPerfilDomain.crear("Título de prueba", UUID.randomUUID());

        return RegistroFichaPerfilDomain.crear(
                ficha,
                EstadoFichaPerfilDomain.crear(ficha.getId()),
                AgregacionEstudiantesFichaPerfilDomain.crear(
                        EstudianteFichaPerfilDomain.crear(ficha.getId(), List.of(UUID.randomUUID()))));
    }

    // El puerto ya recibe la entidad que construyo el mapper: se verifica por identidad de negocio.
    private static EstadoFichaPerfilEntity entidadDe(EstadoFichaPerfilDomain estado) {
        return argThat(entity -> entity.getId().equals(estado.getId())
                && entity.getFichaPerfilId().equals(estado.getFichaPerfil())
                && entity.getEstadoFicha().getId().equals(estado.getEstadoFicha().getId()));
    }
}
