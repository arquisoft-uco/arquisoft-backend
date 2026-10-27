package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefactos;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactosExistenRuleImplTest {

    private final ArtefactosExistenRuleImpl rule = new ArtefactosExistenRuleImpl();

    @Test
    void debePasar_cuandoTodosLosArtefactosExisten() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = ArtefactoDomain.reconstruir(
                id, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatCode(() -> rule.validar(new ExistenciaArtefactos(List.of(id), List.of(encontrado))))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_cuandoFaltaUnArtefacto() {
        // Arrange
        var faltante = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ExistenciaArtefactos(List.of(faltante), List.of())))
                .isInstanceOf(ArtefactoNoEncontradoException.class)
                .satisfies(ex -> assertThat(((ArtefactoNoEncontradoException) ex).getCodigoError())
                        .isEqualTo(ArtefactosCodes.Artefacto.NO_ENCONTRADO));
    }
}
