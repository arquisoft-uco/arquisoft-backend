package com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.secondaryport.model.EntradaPaqueteArmada;
import com.arquisoft.artefactos.domain.paqueteentregable.model.ArtefactoIncluidoPaquete;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PaqueteEntregableMapperTest {

    private static ArtefactoIncluidoPaquete incluido(UUID artefacto, String nombre, int numeroVersion) {
        return new ArtefactoIncluidoPaquete(artefacto, UtilUUID.generarNuevoUUID(), nombre, numeroVersion,
                numeroVersion, Instant.now(), "uri", UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "Repo " + nombre);
    }

    @Test
    void debeUnirRutaPorArtefacto_cuandoHayVariosIncluidos() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();
        var incluidos = List.of(incluido(primero, "Informe", 3), incluido(segundo, "Anexo", 1));
        var entradas = List.of(
                new EntradaPaqueteArmada(segundo, "02-Anexo/v1-anexo.pdf"),
                new EntradaPaqueteArmada(primero, "01-Informe/v3-informe.pdf"));

        // Act
        var resultado = PaqueteEntregableMapper.toArtefactosGenerados(incluidos, entradas);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).artefacto()).isEqualTo(primero);
        assertThat(resultado.get(0).numeroVersion()).isEqualTo(3);
        assertThat(resultado.get(0).nombreDocumento()).isEqualTo("Informe");
        assertThat(resultado.get(0).nombreRepositorio()).isEqualTo("Repo Informe");
        assertThat(resultado.get(0).versionRepositorioArtefacto())
                .isEqualTo(incluidos.get(0).versionRepositorioArtefacto());
        assertThat(resultado.get(0).repositorio()).isEqualTo(incluidos.get(0).repositorio());
        assertThat(resultado.get(0).rutaEnPaquete()).isEqualTo("01-Informe/v3-informe.pdf");
        assertThat(resultado.get(1).artefacto()).isEqualTo(segundo);
        assertThat(resultado.get(1).rutaEnPaquete()).isEqualTo("02-Anexo/v1-anexo.pdf");
    }

    @Test
    void debeDevolverVacio_cuandoNoHayIncluidos() {
        // Arrange & Act
        var resultado = PaqueteEntregableMapper.toArtefactosGenerados(List.of(), List.of());

        // Assert
        assertThat(resultado).isEmpty();
    }
}
