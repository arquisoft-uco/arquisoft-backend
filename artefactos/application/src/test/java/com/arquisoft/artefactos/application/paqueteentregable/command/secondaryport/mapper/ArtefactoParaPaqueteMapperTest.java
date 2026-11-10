package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.entity.ArtefactoParaPaqueteEntity;
import com.arquisoft.artefactos.domain.paqueteentregable.model.MotivoExclusionPaquete;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoParaPaqueteMapperTest {

    @Test
    void debeClasificarIncluido_cuandoHayVersionCerrada() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var fecha = Instant.now();
        var versionRepositorio = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var fila = new ArtefactoParaPaqueteEntity(artefacto, "Doc", version, 2, "uri", fecha, 3,
                versionRepositorio, repositorio, "Repo");

        // Act
        var resultado = ArtefactoParaPaqueteMapper.toDomain(List.of(fila));

        // Assert
        assertThat(resultado.excluidos()).isEmpty();
        assertThat(resultado.incluidos()).singleElement().satisfies(incluido -> {
            assertThat(incluido.artefacto()).isEqualTo(artefacto);
            assertThat(incluido.versionArtefacto()).isEqualTo(version);
            assertThat(incluido.nombreDocumento()).isEqualTo("Doc");
            assertThat(incluido.numeroVersion()).isEqualTo(2);
            assertThat(incluido.ultimaVersion()).isEqualTo(3);
            assertThat(incluido.fechaVersion()).isEqualTo(fecha);
            assertThat(incluido.uriArchivo()).isEqualTo("uri");
            assertThat(incluido.versionRepositorioArtefacto()).isEqualTo(versionRepositorio);
            assertThat(incluido.repositorio()).isEqualTo(repositorio);
            assertThat(incluido.nombreRepositorio()).isEqualTo("Repo");
        });
    }

    @Test
    void debeClasificarSinVersiones_cuandoNoHayVersiones() {
        // Arrange
        var fila = new ArtefactoParaPaqueteEntity(UtilUUID.generarNuevoUUID(), "Doc",
                UtilUUID.obtenerUUIDPorDefecto(), 0, "", Instant.now(), 0,
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo");

        // Act
        var resultado = ArtefactoParaPaqueteMapper.toDomain(List.of(fila));

        // Assert
        assertThat(resultado.incluidos()).isEmpty();
        assertThat(resultado.excluidos()).singleElement().satisfies(excluido -> {
            assertThat(excluido.motivo()).isEqualTo(MotivoExclusionPaquete.SIN_VERSIONES);
            assertThat(excluido.ultimaVersion()).isZero();
        });
    }

    @Test
    void debeClasificarSinRevisionCerrada_cuandoHayVersionesSinCerrar() {
        // Arrange
        var fila = new ArtefactoParaPaqueteEntity(UtilUUID.generarNuevoUUID(), "Doc",
                UtilUUID.obtenerUUIDPorDefecto(), 0, "", Instant.now(), 4,
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Repo");

        // Act
        var resultado = ArtefactoParaPaqueteMapper.toDomain(List.of(fila));

        // Assert
        assertThat(resultado.excluidos()).singleElement().satisfies(excluido -> {
            assertThat(excluido.motivo()).isEqualTo(MotivoExclusionPaquete.SIN_REVISION_CERRADA);
            assertThat(excluido.ultimaVersion()).isEqualTo(4);
        });
    }
}
