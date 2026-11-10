package com.arquisoft.entregables.application.artefactoentregable.command.usecase.impl;

import com.arquisoft.entregables.application.artefactoentregable.command.finder.ArtefactosReplicadosFinder;
import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.ArtefactoEntregableOutputPort;
import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;
import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.entregables.domain.artefactoentregable.ArtefactoEntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoEntregableKey;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgregarArtefactosEntregableUseCaseImplTest {

    @Mock
    private ArtefactoEntregableOutputPort artefactoEntregableOutputPort;

    @Mock
    private ArtefactosReplicadosFinder artefactosReplicadosFinder;

    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<List<ArtefactoEntregableEntity>> captorArtefactos;

    @Captor
    private ArgumentCaptor<List<ArtefactoEntregableEntity>> captorVinculos;

    @InjectMocks
    private AgregarArtefactosEntregableUseCaseImpl useCase;

    private static ArtefactoEntregableDomain snapshot(UUID entregable, UUID version, String ruta) {
        return ArtefactoEntregableDomain.crear(entregable, ruta, UtilUUID.generarNuevoUUID(), version,
                1, "Informe", new ArtefactoEntregableDomain.DatosRepositorioArtefacto(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo"));
    }

    @Test
    void debeGuardarArtefactosYVincularTodos_cuandoNingunaVersionEstaReplicada() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var primero = snapshot(entregable, UtilUUID.generarNuevoUUID(), "01-Informe/v1-a.pdf");
        var segundo = snapshot(entregable, UtilUUID.generarNuevoUUID(), "02-Informe/v1-b.pdf");
        var agregacion = AgregacionArtefactosEntregableDomain.crear(entregable, List.of(primero, segundo));
        when(artefactosReplicadosFinder.obtener(List.of(primero.getVersionArtefacto(), segundo.getVersionArtefacto())))
                .thenReturn(List.of());

        // Act
        useCase.ejecutar(agregacion);

        // Assert
        verify(artefactoEntregableOutputPort).guardarArtefactos(captorArtefactos.capture());
        verify(artefactoEntregableOutputPort).vincular(captorVinculos.capture());
        assertThat(captorArtefactos.getValue()).extracting(ArtefactoEntregableEntity::versionArtefacto)
                .containsExactly(primero.getVersionArtefacto(), segundo.getVersionArtefacto());
        assertThat(captorVinculos.getValue()).extracting(ArtefactoEntregableEntity::uriArchivo)
                .containsExactly("01-Informe/v1-a.pdf", "02-Informe/v1-b.pdf");
        assertThat(captorVinculos.getValue()).allSatisfy(entity ->
                assertThat(entity.entregableProyectoGrado()).isEqualTo(entregable));
        assertThat(captorVinculos.getValue().getFirst().id()).isEqualTo(primero.getId());
        verify(logger).debug(ArtefactoEntregableKey.LOG_VINCULADOS, entregable, 2, 0);
    }

    @Test
    void debeSoloVincular_cuandoTodasLasVersionesYaEstanReplicadas() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var primero = snapshot(entregable, UtilUUID.generarNuevoUUID(), "01-Informe/v1-a.pdf");
        var agregacion = AgregacionArtefactosEntregableDomain.crear(entregable, List.of(primero));
        when(artefactosReplicadosFinder.obtener(List.of(primero.getVersionArtefacto())))
                .thenReturn(List.of(primero.getVersionArtefacto()));

        // Act
        useCase.ejecutar(agregacion);

        // Assert
        verify(artefactoEntregableOutputPort).guardarArtefactos(captorArtefactos.capture());
        verify(artefactoEntregableOutputPort).vincular(captorVinculos.capture());
        assertThat(captorArtefactos.getValue()).isEmpty();
        assertThat(captorVinculos.getValue()).hasSize(1);
    }

    @Test
    void debeGuardarSoloLosFaltantesYVincularTodos_cuandoHayMezcla() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var existente = snapshot(entregable, UtilUUID.generarNuevoUUID(), "01-Informe/v1-a.pdf");
        var nuevo = snapshot(entregable, UtilUUID.generarNuevoUUID(), "02-Informe/v1-b.pdf");
        var otroNuevo = snapshot(entregable, UtilUUID.generarNuevoUUID(), "03-Informe/v1-c.pdf");
        var agregacion = AgregacionArtefactosEntregableDomain.crear(entregable, List.of(existente, nuevo, otroNuevo));
        when(artefactosReplicadosFinder.obtener(List.of(
                existente.getVersionArtefacto(), nuevo.getVersionArtefacto(), otroNuevo.getVersionArtefacto())))
                .thenReturn(List.of(existente.getVersionArtefacto()));

        // Act
        useCase.ejecutar(agregacion);

        // Assert
        var orden = inOrder(artefactosReplicadosFinder, artefactoEntregableOutputPort);
        orden.verify(artefactosReplicadosFinder, times(1)).obtener(any());
        orden.verify(artefactoEntregableOutputPort).guardarArtefactos(captorArtefactos.capture());
        orden.verify(artefactoEntregableOutputPort).vincular(captorVinculos.capture());
        assertThat(captorArtefactos.getValue()).extracting(ArtefactoEntregableEntity::versionArtefacto)
                .containsExactly(nuevo.getVersionArtefacto(), otroNuevo.getVersionArtefacto());
        assertThat(captorVinculos.getValue()).extracting(ArtefactoEntregableEntity::versionArtefacto)
                .containsExactly(existente.getVersionArtefacto(), nuevo.getVersionArtefacto(),
                        otroNuevo.getVersionArtefacto());
        verify(logger).debug(ArtefactoEntregableKey.LOG_VINCULADOS, entregable, 3, 1);
    }

    @Test
    void debeNoLlamarAlPuerto_cuandoListaVacia() {
        // Arrange
        var agregacion = AgregacionArtefactosEntregableDomain.crear(UtilUUID.generarNuevoUUID(), List.of());

        // Act
        useCase.ejecutar(agregacion);

        // Assert
        verify(artefactoEntregableOutputPort, never()).guardarArtefactos(any());
        verify(artefactoEntregableOutputPort, never()).vincular(any());
    }
}
