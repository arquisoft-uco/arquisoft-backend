package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.validator.CambiarAsesorArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.CambioAsesorArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CambiarAsesorArtefactoUseCaseImplTest {

    @Mock
    private ArtefactoPorIdFinder artefactoPorIdFinder;
    @Mock
    private AsesorPorIdFinder asesorPorIdFinder;
    @Mock
    private VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    @Mock
    private CambiarAsesorArtefactoValidator cambiarAsesorArtefactoValidator;
    @Mock
    private ArtefactoOutputPort artefactoOutputPort;
    @Mock
    private AppLogger logger;

    @InjectMocks
    private CambiarAsesorArtefactoUseCaseImpl useCase;

    private UUID artefactoId;
    private UUID asesorId;
    private UUID proyecto;
    private CambioAsesorArtefactoDomain cambio;
    private ArtefactoDomain artefacto;

    @BeforeEach
    void setUp() {
        artefactoId = UtilUUID.generarNuevoUUID();
        asesorId = UtilUUID.generarNuevoUUID();
        proyecto = UtilUUID.generarNuevoUUID();
        cambio = CambioAsesorArtefactoDomain.crear(artefactoId, asesorId);
        artefacto = ArtefactoDomain.reconstruir(
                artefactoId, proyecto, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
    }

    @Test
    void debeCambiarElAsesor_cuandoTodoEsValido() {
        // Arrange
        var asesor = AsesorDomain.reconstruir(
                asesorId, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now(), null);
        var vinculos = List.of(
                AsesorProyectoGradoDomain.reconstruir(
                        UtilUUID.generarNuevoUUID(), asesorId, proyecto, Instant.now(), null));
        when(artefactoPorIdFinder.obtener(artefactoId)).thenReturn(artefacto);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(asesor);
        when(vinculosMasRecientesProyectoGradoFinder.obtener(proyecto)).thenReturn(vinculos);

        // Act
        useCase.ejecutar(cambio);

        // Assert
        var orden = inOrder(artefactoPorIdFinder, asesorPorIdFinder, vinculosMasRecientesProyectoGradoFinder,
                cambiarAsesorArtefactoValidator, artefactoOutputPort, logger);
        orden.verify(logger).info(any(ClaveMensaje.class), eq(artefactoId), eq(asesorId));
        orden.verify(artefactoPorIdFinder, times(1)).obtener(artefactoId);
        orden.verify(asesorPorIdFinder, times(1)).obtener(asesorId);
        orden.verify(vinculosMasRecientesProyectoGradoFinder, times(1)).obtener(proyecto);
        orden.verify(cambiarAsesorArtefactoValidator).validar(cambio, artefacto, asesor, vinculos);
        orden.verify(artefactoOutputPort).cambiarAsesor(artefactoId, asesorId);
        orden.verify(logger).info(eq(ArtefactoKey.LOG_ASESOR_CAMBIADO), eq(artefactoId), eq(asesorId));
    }

    @Test
    void debeNoPersistirNiCerrar_cuandoElValidatorRechaza() {
        // Arrange
        when(artefactoPorIdFinder.obtener(artefactoId)).thenReturn(ArtefactoDomain.VACIO);
        when(asesorPorIdFinder.obtener(asesorId)).thenReturn(AsesorDomain.VACIO);
        when(vinculosMasRecientesProyectoGradoFinder.obtener(ArtefactoDomain.VACIO.getProyectoGrado()))
                .thenReturn(List.of());
        doThrow(new ArtefactoNoEncontradoException(artefactoId)).when(cambiarAsesorArtefactoValidator)
                .validar(cambio, ArtefactoDomain.VACIO, AsesorDomain.VACIO, List.of());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(cambio)).isInstanceOf(ArtefactoNoEncontradoException.class);
        verify(artefactoOutputPort, never()).cambiarAsesor(any(), any());
        verify(logger, never()).info(eq(ArtefactoKey.LOG_ASESOR_CAMBIADO), eq(artefactoId), eq(asesorId));
    }
}
