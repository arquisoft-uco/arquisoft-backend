package com.arquisoft.entregables.application.artefactoentregable.command.usecase.impl;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.ArtefactoEntregableOutputPort;
import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity.ArtefactoEntregableEntity;
import com.arquisoft.entregables.domain.artefactoentregable.AgregacionArtefactosEntregableDomain;
import com.arquisoft.entregables.domain.artefactoentregable.ArtefactoEntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AgregarArtefactosEntregableUseCaseImplTest {

    @Mock
    private ArtefactoEntregableOutputPort artefactoEntregableOutputPort;

    @Mock
    private AppLogger logger;

    @Captor
    private ArgumentCaptor<List<ArtefactoEntregableEntity>> captor;

    @InjectMocks
    private AgregarArtefactosEntregableUseCaseImpl useCase;

    private static ArtefactoEntregableDomain snapshot(UUID entregable, String ruta) {
        return ArtefactoEntregableDomain.crear(entregable, ruta, UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "Informe", UtilUUID.generarNuevoUUID(), "Repo");
    }

    @Test
    void debeGuardarTodos_cuandoHayArtefactos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var primero = snapshot(entregable, "01-Informe/v1-a.pdf");
        var segundo = snapshot(entregable, "02-Informe/v1-b.pdf");
        var tercero = snapshot(entregable, "03-Informe/v1-c.pdf");
        var agregacion = AgregacionArtefactosEntregableDomain.crear(entregable, List.of(primero, segundo, tercero));

        // Act
        useCase.ejecutar(agregacion);

        // Assert
        verify(artefactoEntregableOutputPort).guardarTodos(captor.capture());
        assertThat(captor.getValue()).extracting(ArtefactoEntregableEntity::uriArchivo)
                .containsExactly("01-Informe/v1-a.pdf", "02-Informe/v1-b.pdf", "03-Informe/v1-c.pdf");
        assertThat(captor.getValue()).allSatisfy(entity ->
                assertThat(entity.entregableProyectoGrado()).isEqualTo(entregable));
        assertThat(captor.getValue().getFirst().id()).isEqualTo(primero.getId());
        assertThat(captor.getValue().getFirst().versionArtefacto()).isEqualTo(primero.getVersionArtefacto());
        assertThat(captor.getValue().getFirst().nombreDocumento()).isEqualTo("Informe");
        assertThat(captor.getValue().getFirst().nombreRepositorio()).isEqualTo("Repo");
    }

    @Test
    void debeNoLlamarAlPuerto_cuandoListaVacia() {
        // Arrange
        var agregacion = AgregacionArtefactosEntregableDomain.crear(UtilUUID.generarNuevoUUID(), List.of());

        // Act
        useCase.ejecutar(agregacion);

        // Assert
        verify(artefactoEntregableOutputPort, never()).guardarTodos(any());
    }
}
