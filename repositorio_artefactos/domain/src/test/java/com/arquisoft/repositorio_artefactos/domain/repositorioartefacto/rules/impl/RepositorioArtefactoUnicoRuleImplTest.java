package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoDuplicadoException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.DisponibilidadRepositorioArtefacto;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositorioArtefactoUnicoRuleImplTest {

    private final RepositorioArtefactoUnicoRuleImpl regla = new RepositorioArtefactoUnicoRuleImpl();

    @Test
    void debePasar_cuandoLaCombinacionNoExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadRepositorioArtefacto(
                "Repo", "Descripcion", UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarDuplicado_cuandoLaCombinacionYaExiste() {
        // Arrange
        var disponibilidad = new DisponibilidadRepositorioArtefacto(
                "Repo", "Descripcion", UtilUUID.generarNuevoUUID(), true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(RepositorioArtefactoDuplicadoException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.DUPLICADO);
    }
}
