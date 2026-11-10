package com.arquisoft.entregables.application.artefactoentregable.command.primaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model.ArtefactoPaqueteCommand;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarArtefactosEntregableMapperTest {

    @Test
    void debeCrearUnSnapshotPorItemConLaVersionDelCommand_cuandoHayArtefactos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var versionArtefacto = UtilUUID.generarNuevoUUID();
        var item = new ArtefactoPaqueteCommand(artefacto, versionArtefacto, 3, UtilUUID.generarNuevoUUID(), "Informe",
                UtilUUID.generarNuevoUUID(), "Repo", "01-Informe/v3-informe.pdf");

        // Act
        var agregacion = AgregarArtefactosEntregableMapper.toDomain(entregable, List.of(item, item));

        // Assert
        assertThat(agregacion.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(agregacion.getArtefactos()).hasSize(2);
        assertThat(agregacion.getArtefactos()).allSatisfy(snapshot -> {
            assertThat(snapshot.getEntregableProyectoGrado()).isEqualTo(entregable);
            assertThat(snapshot.getUriArchivo()).isEqualTo("01-Informe/v3-informe.pdf");
            assertThat(snapshot.getArtefacto()).isEqualTo(artefacto);
            assertThat(snapshot.getVersionArtefacto()).isEqualTo(versionArtefacto);
            assertThat(snapshot.getNumeroVersion()).isEqualTo(3);
            assertThat(snapshot.getVersionRepositorioArtefacto()).isEqualTo(item.versionRepositorioArtefacto());
        });
    }
}
