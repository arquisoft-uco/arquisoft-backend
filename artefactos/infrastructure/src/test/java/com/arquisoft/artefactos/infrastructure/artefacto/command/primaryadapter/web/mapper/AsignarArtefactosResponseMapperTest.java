package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.command.result.ArtefactosAsignadosResult;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AsignarArtefactosResponseMapperTest {

    @Test
    void debeExponerLosArtefactosComoIds_cuandoMapeaElResultado() {
        // Arrange
        var primero = UtilUUID.generarNuevoUUID();
        var segundo = UtilUUID.generarNuevoUUID();

        // Act
        var response = AsignarArtefactosResponseMapper.toResponse(
                new ArtefactosAsignadosResult(List.of(primero, segundo)));

        // Assert
        assertThat(response.ids()).containsExactly(primero, segundo);
    }
}
