package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioIgualException;
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

class VersionRepositorioDistintaRuleImplTest {

    private final VersionRepositorioDistintaRuleImpl rule = new VersionRepositorioDistintaRuleImpl();

    private static VersionRepositorioArtefactoDomain version(int numero) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Formato A", numero, UtilUUID.generarNuevoUUID(), "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void debePasar_cuandoVersionMenorOMayor(int numeroElegido) {
        // Arrange
        var cambio = new CambioVersionRepositorio(version(2), version(numeroElegido));

        // Act & Assert
        assertThatCode(() -> rule.validar(cambio)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinUuidEnMensaje_cuandoEsLaMismaVersion() {
        // Arrange
        var actual = version(2);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new CambioVersionRepositorio(actual, actual)))
                .isInstanceOf(VersionRepositorioIgualException.class)
                .satisfies(ex -> {
                    var excepcion = (VersionRepositorioIgualException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_IGUAL_A_ACTUAL);
                    assertThat(excepcion.getError().getMessage()).doesNotContain(actual.getId().toString());
                });
    }
}
