package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoExisteRuleImplTest {

    private final ArtefactoExisteRuleImpl rule = new ArtefactoExisteRuleImpl();

    @Test
    void debePasar_cuandoElArtefactoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = ArtefactoDomain.reconstruir(
                id, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), null);

        // Act & Assert
        assertThatCode(() -> rule.validar(new ExistenciaArtefacto(id, encontrado))).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarArtefactoNoEncontrado_cuandoElResultadoEsVacio() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ExistenciaArtefacto(id, ArtefactoDomain.VACIO)))
                .isInstanceOf(ArtefactoNoEncontradoException.class)
                .satisfies(ex -> assertThat(((ArtefactoNoEncontradoException) ex).getCodigoError())
                        .isEqualTo(ArtefactosCodes.Artefacto.NO_ENCONTRADO));
    }
}
