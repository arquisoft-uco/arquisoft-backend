package com.arquisoft.entregables.application.artefacto.command.primaryport.mapper;

import com.arquisoft.entregables.application.artefacto.command.primaryport.model.RegistrarVersionCerradaArtefactoCommand;
import com.arquisoft.shared.message.constant.EntregablesAlmacenamiento;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarVersionCerradaArtefactoMapperTest {

    @Test
    void debeComponerArtefactoConClaveDestinoYClaveOrigen_cuandoSeMapeaElComando() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var command = RegistrarVersionCerradaArtefactoCommand.crear(
                artefacto.toString(), proyecto.toString(), "Documento",
                new RegistrarVersionCerradaArtefactoCommand.DatosVersion(
                        UtilUUID.generarNuevoUUID().toString(), 4, "pendientes/a.pdf", Instant.now()),
                new RegistrarVersionCerradaArtefactoCommand.DatosRepositorio(
                        UtilUUID.generarNuevoUUID().toString(), UtilUUID.generarNuevoUUID().toString(), "Repo"));

        // Act
        var registro = RegistrarVersionCerradaArtefactoMapper.toDomain(command);

        // Assert
        assertThat(registro.getClaveOrigen()).isEqualTo("pendientes/a.pdf");
        assertThat(registro.getArtefacto().getId()).isEqualTo(artefacto);
        assertThat(registro.getArtefacto().getProyectoGrado()).isEqualTo(proyecto);
        assertThat(registro.getArtefacto().getClaveObjeto())
                .isEqualTo(EntregablesAlmacenamiento.PREFIJO_ARTEFACTOS + artefacto + "/v4");
    }
}
