package com.arquisoft.entregables.application.artefactoentregable.command.primaryport.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.model.EntradaPaqueteArmada;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarArtefactosEntregableMapperTest {

    @Test
    void debeCrearUnaFotoPorArtefactoConLaRutaDelZip_cuandoHayArtefactos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var artefacto = ArtefactoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Informe",
                new ArtefactoDomain.VersionCerrada(UtilUUID.generarNuevoUUID(), 3, Instant.now()),
                new ArtefactoDomain.DatosRepositorio(
                        UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo"));
        var entrada = new EntradaPaqueteArmada(artefacto.getId(), "01-Informe/v3-informe.pdf");

        // Act
        var agregacion = AgregarArtefactosEntregableMapper.toDomain(entregable, List.of(artefacto), List.of(entrada));

        // Assert
        assertThat(agregacion.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(agregacion.getArtefactos()).singleElement().satisfies(foto -> {
            assertThat(foto.getEntregableProyectoGrado()).isEqualTo(entregable);
            assertThat(foto.getUriArchivo()).isEqualTo("01-Informe/v3-informe.pdf");
            assertThat(foto.getArtefacto()).isEqualTo(artefacto.getId());
            assertThat(foto.getVersionArtefacto()).isEqualTo(artefacto.getVersionArtefacto());
            assertThat(foto.getNumeroVersion()).isEqualTo(3);
            assertThat(foto.getVersionRepositorioArtefacto()).isEqualTo(artefacto.getVersionRepositorioArtefacto());
        });
    }
}
