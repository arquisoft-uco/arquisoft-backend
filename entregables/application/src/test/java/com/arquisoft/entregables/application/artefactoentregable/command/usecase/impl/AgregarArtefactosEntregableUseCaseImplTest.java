package com.arquisoft.entregables.application.artefactoentregable.command.usecase.impl;

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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AgregarArtefactosEntregableUseCaseImplTest {

    @Mock
    private ArtefactoEntregableOutputPort artefactoEntregableOutputPort;

    @Mock
    private AppLogger logger;

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
    void debeVincularTodos_cuandoHayArtefactos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var primero = snapshot(entregable, UtilUUID.generarNuevoUUID(), "01-Informe/v1-a.pdf");
        var segundo = snapshot(entregable, UtilUUID.generarNuevoUUID(), "02-Informe/v1-b.pdf");
        var agregacion = AgregacionArtefactosEntregableDomain.crear(entregable, List.of(primero, segundo));

        // Act
        useCase.ejecutar(agregacion);

        // Assert
        verify(artefactoEntregableOutputPort).vincular(captorVinculos.capture());
        assertThat(captorVinculos.getValue()).extracting(ArtefactoEntregableEntity::uriArchivo)
                .containsExactly("01-Informe/v1-a.pdf", "02-Informe/v1-b.pdf");
        assertThat(captorVinculos.getValue()).allSatisfy(entity ->
                assertThat(entity.entregableProyectoGrado()).isEqualTo(entregable));
        assertThat(captorVinculos.getValue().getFirst().id()).isEqualTo(primero.getId());
        verify(logger).debug(ArtefactoEntregableKey.LOG_VINCULADOS, entregable, 2);
    }
}
