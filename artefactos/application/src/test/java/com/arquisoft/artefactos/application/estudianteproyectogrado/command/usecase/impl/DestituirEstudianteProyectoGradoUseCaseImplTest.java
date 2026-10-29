package com.arquisoft.artefactos.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.DesvincularEstudianteArtefactosUseCase;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudianteProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.model.VinculoEstudianteProyectoGrado;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.DestitucionEstudianteProyectoGradoResult;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.artefactos.domain.estudianteartefacto.DesvinculacionEstudianteArtefactosDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.DestitucionEstudianteProyectoGradoDomain;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestituirEstudianteProyectoGradoUseCaseImplTest {

    private static final Instant ANTES = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant DESPUES = Instant.parse("2026-10-02T10:00:00Z");

    @Mock
    private EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    @Mock
    private EstudianteProyectoGradoPorVinculoFinder estudianteProyectoGradoPorVinculoFinder;
    @Mock
    private DesvincularEstudianteArtefactosUseCase desvincularEstudianteArtefactosUseCase;
    @Mock
    private AppLogger logger;

    private DestituirEstudianteProyectoGradoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new DestituirEstudianteProyectoGradoUseCaseImpl(
                estudianteProyectoGradoOutputPort, estudianteProyectoGradoPorVinculoFinder,
                desvincularEstudianteArtefactosUseCase, logger);
    }

    private DestitucionEstudianteProyectoGradoDomain armarEntrada(UUID estudiante, UUID proyecto, Instant ocurridoEn) {
        return DestitucionEstudianteProyectoGradoDomain.crear(
                EstudianteProyectoGradoDomain.crear(estudiante, proyecto, ocurridoEn),
                DesvinculacionEstudianteArtefactosDomain.crear(proyecto, estudiante));
    }

    @Test
    void debeDestituirYDesvincularEnOrden_cuandoElVinculoExisteYElEventoEsMasNuevo() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = armarEntrada(estudiante, proyecto, DESPUES);
        var vigente = EstudianteProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), estudiante, proyecto, ANTES, UtilFecha.VACIO);
        when(estudianteProyectoGradoPorVinculoFinder.obtener(new VinculoEstudianteProyectoGrado(estudiante, proyecto)))
                .thenReturn(vigente);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new DestitucionEstudianteProyectoGradoResult.Destituida(estudiante, proyecto));
        var captor = ArgumentCaptor.forClass(EstudianteProyectoGradoEntity.class);
        var orden = inOrder(estudianteProyectoGradoOutputPort, desvincularEstudianteArtefactosUseCase);
        orden.verify(estudianteProyectoGradoOutputPort).guardar(captor.capture());
        orden.verify(desvincularEstudianteArtefactosUseCase).ejecutar(entrada.getDesvinculacion());
        verify(estudianteProyectoGradoPorVinculoFinder, times(1)).obtener(any());
        assertThat(captor.getValue().id()).isEqualTo(vigente.getId());
        assertThat(captor.getValue().destituidoEn()).isEqualTo(DESPUES);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(DESPUES);
    }

    @Test
    void debeGuardarLapidaYDesvincular_cuandoElVinculoNuncaSeReplico() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = armarEntrada(estudiante, proyecto, DESPUES);
        when(estudianteProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(EstudianteProyectoGradoDomain.VACIO);

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(new DestitucionEstudianteProyectoGradoResult.Lapida(estudiante, proyecto));
        var captor = ArgumentCaptor.forClass(EstudianteProyectoGradoEntity.class);
        verify(estudianteProyectoGradoOutputPort).guardar(captor.capture());
        verify(desvincularEstudianteArtefactosUseCase).ejecutar(entrada.getDesvinculacion());
        assertThat(captor.getValue().id()).isEqualTo(entrada.getVinculo().getId());
        assertThat(captor.getValue().destituidoEn()).isEqualTo(DESPUES);
    }

    @Test
    void debeDescartarSinGuardarNiDesvincular_cuandoElEventoNoEsMasNuevoQueElGuardado() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entrada = armarEntrada(estudiante, proyecto, ANTES);
        when(estudianteProyectoGradoPorVinculoFinder.obtener(any())).thenReturn(
                EstudianteProyectoGradoDomain.reconstruir(
                        UtilUUID.generarNuevoUUID(), estudiante, proyecto, DESPUES, UtilFecha.VACIO));

        // Act
        var resultado = useCase.ejecutar(entrada);

        // Assert
        assertThat(resultado).isEqualTo(
                new DestitucionEstudianteProyectoGradoResult.Descartada(estudiante, proyecto, DESPUES));
        verify(estudianteProyectoGradoOutputPort, never()).guardar(any());
        verify(desvincularEstudianteArtefactosUseCase, never()).ejecutar(any());
    }
}
