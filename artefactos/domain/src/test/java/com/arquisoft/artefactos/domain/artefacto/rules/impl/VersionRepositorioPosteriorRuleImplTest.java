package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioNoPosteriorException;
import com.arquisoft.artefactos.domain.artefacto.model.CambioVersionRepositorio;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioPosteriorRuleImplTest {

    private final VersionRepositorioPosteriorRuleImpl rule = new VersionRepositorioPosteriorRuleImpl();

    private static VersionRepositorioArtefactoDomain version(int numero) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Formato A", numero, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debePasar_cuandoVersionMayor() {
        // Arrange
        var cambio = new CambioVersionRepositorio(version(2), version(3));

        // Act & Assert
        assertThatCode(() -> rule.validar(cambio)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 1})
    void debeLanzarSinUuidEnMensaje_cuandoVersionIgualOMenor(int numeroElegido) {
        // Arrange
        var actual = version(3);
        var elegida = version(numeroElegido);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new CambioVersionRepositorio(actual, elegida)))
                .isInstanceOf(VersionRepositorioNoPosteriorException.class)
                .satisfies(ex -> {
                    var excepcion = (VersionRepositorioNoPosteriorException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_NO_POSTERIOR);
                    assertThat(excepcion.getError().getMessage())
                            .doesNotContain(actual.getId().toString())
                            .doesNotContain(elegida.getId().toString());
                });
    }
}
