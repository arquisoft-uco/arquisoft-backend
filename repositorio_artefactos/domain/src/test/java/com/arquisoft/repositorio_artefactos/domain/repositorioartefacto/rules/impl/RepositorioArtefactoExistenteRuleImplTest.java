package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoNoEncontradoException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.ExistenciaRepositorioArtefacto;
import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepositorioArtefactoExistenteRuleImplTest {

    private final RepositorioArtefactoExistenteRuleImpl regla = new RepositorioArtefactoExistenteRuleImpl();

    @Test
    void debePasar_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = RepositorioArtefactoDomain.reconstruir(id, "Repo", "Descripcion", UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> regla.validar(new ExistenciaRepositorioArtefacto(id, encontrado)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoVacio() {
        // Arrange
        var existencia = new ExistenciaRepositorioArtefacto(UtilUUID.generarNuevoUUID(), RepositorioArtefactoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOf(RepositorioArtefactoNoEncontradoException.class)
                .extracting("codigoError")
                .isEqualTo(RepositorioArtefactosCodes.RepositorioArtefacto.NO_ENCONTRADO);
    }
}
