package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.EvaluacionDeRepresentanteExisteFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComiteExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AsignarEstadoInicialEvaluacionUseCase;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
class RegistrarEvaluacionFichaPerfilUseCaseTest {

    @Mock
    private EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;

    @Mock
    private AsignarEstadoInicialEvaluacionUseCase asignarEstadoInicialEvaluacionUseCase;

    @Mock
    private FichaPerfilExisteFinder fichaPerfilExisteFinder;

    @Mock
    private RepresentanteComiteExisteFinder representanteComiteExisteFinder;

    @Mock
    private EvaluacionDeRepresentanteExisteFinder evaluacionDeRepresentanteExisteFinder;

    @Mock
    private RegistrarEvaluacionFichaPerfilValidator registrarEvaluacionFichaPerfilValidator;

    @Mock
    private AppLogger logger;
    @InjectMocks
    private RegistrarEvaluacionFichaPerfilUseCaseImpl registrarEvaluacionFichaPerfilUseCase;

    private final UUID representante = UUID.randomUUID();
    private final UUID ficha = UUID.randomUUID();

    @Test
    void debeRegistrarLaEvaluacionYSuEstadoInicial_cuandoDatosValidos() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, true, true, false);

        // Act
        UUID resultado = registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion);

        // Assert
        assertThat(resultado).isEqualTo(evaluacion.getId());
        verify(evaluacionFichaPerfilOutputPort, times(1)).registrarEvaluacion(entidadDe(evaluacion));
        verify(asignarEstadoInicialEvaluacionUseCase, times(1)).ejecutar(evaluacion);
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, true, true, false);

        // Act
        registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion);

        // Assert
        InOrder inOrder = inOrder(fichaPerfilExisteFinder, representanteComiteExisteFinder,
                evaluacionDeRepresentanteExisteFinder, registrarEvaluacionFichaPerfilValidator,
                evaluacionFichaPerfilOutputPort);
        inOrder.verify(fichaPerfilExisteFinder).obtener(ficha);
        inOrder.verify(representanteComiteExisteFinder).obtener(representante);
        inOrder.verify(evaluacionDeRepresentanteExisteFinder).obtener(evaluacion);
        inOrder.verify(registrarEvaluacionFichaPerfilValidator).validar(evaluacion, true, true, false);
        inOrder.verify(evaluacionFichaPerfilOutputPort).registrarEvaluacion(entidadDe(evaluacion));
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaFichaNoExiste() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, false, true, false);
        doThrow(new FichaPerfilNoEncontradaException(ficha))
                .when(registrarEvaluacionFichaPerfilValidator).validar(evaluacion, false, true, false);

        // Act & Assert
        assertThatThrownBy(() -> registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion))
                .isInstanceOf(FichaPerfilNoEncontradaException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoElRepresentanteNoExiste() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, true, false, false);
        doThrow(new RepresentanteComiteNoEncontradoException(representante))
                .when(registrarEvaluacionFichaPerfilValidator).validar(evaluacion, true, false, false);

        // Act & Assert
        assertThatThrownBy(() -> registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
    }

    @Test
    void debePropagarLaExcepcion_cuandoLaEvaluacionEstaDuplicada() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, true, true, true);
        doThrow(new EvaluacionFichaPerfilDuplicadaException(representante, ficha))
                .when(registrarEvaluacionFichaPerfilValidator).validar(evaluacion, true, true, true);

        // Act & Assert
        assertThatThrownBy(() -> registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
    }

    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, true, true, false);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(evaluacionFichaPerfilOutputPort).registrarEvaluacion(entidadDe(evaluacion));

        // Act & Assert
        assertThatThrownBy(() -> registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion))
                .isInstanceOf(InfrastructureException.class);

        verify(asignarEstadoInicialEvaluacionUseCase, never()).ejecutar(any());
    }

    private void stubConsultas(EvaluacionFichaPerfilDomain evaluacion, boolean fichaExiste,
                               boolean representanteExiste, boolean evaluacionYaExiste) {
        when(fichaPerfilExisteFinder.obtener(ficha)).thenReturn(fichaExiste);
        when(representanteComiteExisteFinder.obtener(representante)).thenReturn(representanteExiste);
        when(evaluacionDeRepresentanteExisteFinder.obtener(evaluacion)).thenReturn(evaluacionYaExiste);
    }

    // El puerto ya recibe la entidad que construyo el mapper: se verifica por identidad de negocio.
    private static EvaluacionFichaPerfilEntity entidadDe(EvaluacionFichaPerfilDomain evaluacion) {
        return argThat(entity -> entity.id().equals(evaluacion.getId())
                && entity.representanteComiteId().equals(evaluacion.getRepresentanteComiteId())
                && entity.fichaPerfilId().equals(evaluacion.getFichaPerfilId()));
    }
}
