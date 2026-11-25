package com.arquisoft.fichas.application.evaluacionfichaperfil.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AsignarEstadoInicialEvaluacionUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AsignarEstadoReactivadoEvaluacionUseCase;
import com.arquisoft.fichas.application.estadofichaperfil.command.finder.EstadoActualFichaPerfilFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.finder.EvaluacionExistenteDeRepresentanteFinder;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.result.InicioEvaluacionFichaPerfilResult;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.validator.RegistrarEvaluacionFichaPerfilValidator;
import com.arquisoft.fichas.application.fichaperfil.command.finder.FichaPerfilExisteFinder;
import com.arquisoft.fichas.application.representantecomite.command.finder.RepresentanteComiteVigenteFinder;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.domain.estadofichaperfil.exception.FichaPerfilNoDisponibleParaEvaluacionException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.model.EvaluacionExistente;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
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
    private AsignarEstadoReactivadoEvaluacionUseCase asignarEstadoReactivadoEvaluacionUseCase;

    @Mock
    private FichaPerfilExisteFinder fichaPerfilExisteFinder;

    @Mock
    private RepresentanteComiteVigenteFinder representanteComiteVigenteFinder;

    @Mock
    private EvaluacionExistenteDeRepresentanteFinder evaluacionExistenteDeRepresentanteFinder;

    @Mock
    private EstadoActualFichaPerfilFinder estadoActualFichaPerfilFinder;

    @Mock
    private RegistrarEvaluacionFichaPerfilValidator registrarEvaluacionFichaPerfilValidator;

    @Mock
    private AppLogger logger;
    @InjectMocks
    private RegistrarEvaluacionFichaPerfilUseCaseImpl registrarEvaluacionFichaPerfilUseCase;

    private final UUID representante = UUID.randomUUID();
    private final UUID ficha = UUID.randomUUID();
    private final EstadoFichaPerfilDomain estadoFicha = EstadoFichaPerfilDomain.reconstruir(
            UUID.randomUUID(), ficha, EstadoFicha.DISPONIBLE_PARA_EVALUACION, Instant.now());

    @Test
    void debeRegistrarLaEvaluacionYSuEstadoInicial_cuandoNoHayEvaluacionExistente() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, true, true, EvaluacionExistente.VACIO);

        // Act
        var resultado = registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion);

        // Assert
        assertThat(resultado).isEqualTo(new InicioEvaluacionFichaPerfilResult.Registrada(evaluacion.getId()));
        verify(evaluacionFichaPerfilOutputPort, times(1)).registrarEvaluacion(entidadDe(evaluacion));
        verify(asignarEstadoInicialEvaluacionUseCase, times(1)).ejecutar(evaluacion);
        verify(asignarEstadoReactivadoEvaluacionUseCase, never()).ejecutar(any());
    }

    @Test
    void debeReactivarLaExistente_cuandoEstaDescartada() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        var existente = new EvaluacionExistente(UUID.randomUUID(), EstadoEvaluacion.DESCARTADA);
        stubConsultas(evaluacion, true, true, existente);

        // Act
        var resultado = registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion);

        // Assert
        assertThat(resultado).isEqualTo(new InicioEvaluacionFichaPerfilResult.Reactivada(existente.evaluacion()));
        verify(asignarEstadoReactivadoEvaluacionUseCase).ejecutar(argThat(estado ->
                estado.getEvaluacionFichaPerfilId().equals(existente.evaluacion())
                        && estado.getEstadoEvaluacion() == EstadoEvaluacion.EN_EVALUACION));
        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
        verify(asignarEstadoInicialEvaluacionUseCase, never()).ejecutar(any());
    }

    @Test
    void debeConsultarYValidarAntesDePersistir_cuandoSeEjecuta() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, true, true, EvaluacionExistente.VACIO);

        // Act
        registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion);

        // Assert
        var orden = inOrder(fichaPerfilExisteFinder, representanteComiteVigenteFinder,
                evaluacionExistenteDeRepresentanteFinder, estadoActualFichaPerfilFinder,
                registrarEvaluacionFichaPerfilValidator, evaluacionFichaPerfilOutputPort);
        orden.verify(fichaPerfilExisteFinder, times(1)).obtener(ficha);
        orden.verify(representanteComiteVigenteFinder, times(1)).obtener(representante);
        orden.verify(evaluacionExistenteDeRepresentanteFinder, times(1)).obtener(evaluacion);
        orden.verify(estadoActualFichaPerfilFinder, times(1)).obtener(ficha);
        orden.verify(registrarEvaluacionFichaPerfilValidator)
                .validar(evaluacion, true, true, EvaluacionExistente.VACIO, estadoFicha);
        orden.verify(evaluacionFichaPerfilOutputPort).registrarEvaluacion(entidadDe(evaluacion));
    }

    @Test
    void debeNoPersistirNiReactivar_cuandoValidatorLanza() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        var existente = new EvaluacionExistente(UUID.randomUUID(), EstadoEvaluacion.EN_EVALUACION);
        stubConsultas(evaluacion, true, true, existente);
        doThrow(new EvaluacionFichaPerfilDuplicadaException(representante, ficha))
                .when(registrarEvaluacionFichaPerfilValidator)
                .validar(evaluacion, true, true, existente, estadoFicha);

        // Act & Assert
        assertThatThrownBy(() -> registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion))
                .isInstanceOf(EvaluacionFichaPerfilDuplicadaException.class);

        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
        verify(asignarEstadoInicialEvaluacionUseCase, never()).ejecutar(any());
        verify(asignarEstadoReactivadoEvaluacionUseCase, never()).ejecutar(any());
    }


    @Test
    void debeNoReactivarNiRegistrar_cuandoLaFichaNoEstaDisponibleYLaExistenteEstaDescartada() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        var existente = new EvaluacionExistente(UUID.randomUUID(), EstadoEvaluacion.DESCARTADA);
        stubConsultas(evaluacion, true, true, existente);
        doThrow(new FichaPerfilNoDisponibleParaEvaluacionException(EstadoFicha.DISPONIBLE_PARA_EVALUACION))
                .when(registrarEvaluacionFichaPerfilValidator)
                .validar(evaluacion, true, true, existente, estadoFicha);

        // Act & Assert
        assertThatThrownBy(() -> registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion))
                .isInstanceOf(FichaPerfilNoDisponibleParaEvaluacionException.class);

        verify(asignarEstadoReactivadoEvaluacionUseCase, never()).ejecutar(any());
        verify(evaluacionFichaPerfilOutputPort, never()).registrarEvaluacion(any());
        verify(asignarEstadoInicialEvaluacionUseCase, never()).ejecutar(any());
    }
    @Test
    void debeLanzarExcepcion_cuandoRepositorioFalla() {
        // Arrange
        var evaluacion = EvaluacionFichaPerfilDomain.crear(representante, ficha);
        stubConsultas(evaluacion, true, true, EvaluacionExistente.VACIO);
        doThrow(new InfrastructureException("ERROR_DB", "Error de BD"))
                .when(evaluacionFichaPerfilOutputPort).registrarEvaluacion(entidadDe(evaluacion));

        // Act & Assert
        assertThatThrownBy(() -> registrarEvaluacionFichaPerfilUseCase.ejecutar(evaluacion))
                .isInstanceOf(InfrastructureException.class);

        verify(asignarEstadoInicialEvaluacionUseCase, never()).ejecutar(any());
    }

    private void stubConsultas(EvaluacionFichaPerfilDomain evaluacion, boolean fichaExiste,
                               boolean representanteExiste, EvaluacionExistente existente) {
        when(fichaPerfilExisteFinder.obtener(ficha)).thenReturn(fichaExiste);
        when(representanteComiteVigenteFinder.obtener(representante)).thenReturn(representanteExiste);
        when(evaluacionExistenteDeRepresentanteFinder.obtener(evaluacion)).thenReturn(existente);
        when(estadoActualFichaPerfilFinder.obtener(ficha)).thenReturn(estadoFicha);
    }

    // El puerto ya recibe la entidad que construyo el mapper: se verifica por identidad de negocio.
    private static EvaluacionFichaPerfilEntity entidadDe(EvaluacionFichaPerfilDomain evaluacion) {
        return argThat(entity -> entity.id().equals(evaluacion.getId())
                && entity.representanteComiteId().equals(evaluacion.getRepresentanteComiteId())
                && entity.fichaPerfilId().equals(evaluacion.getFichaPerfilId()));
    }
}
