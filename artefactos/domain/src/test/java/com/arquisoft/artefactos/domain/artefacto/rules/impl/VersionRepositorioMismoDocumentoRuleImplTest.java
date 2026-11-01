package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioOtroDocumentoException;
import com.arquisoft.artefactos.domain.artefacto.model.CambioVersionRepositorio;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioMismoDocumentoRuleImplTest {

    private final VersionRepositorioMismoDocumentoRuleImpl rule = new VersionRepositorioMismoDocumentoRuleImpl();

    private static VersionRepositorioArtefactoDomain version(UUID repositorio, String documento, int numero) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), documento, numero, repositorio, "Repo",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 3})
    void debePasar_cuandoMismoRepositorioYMismoDocumento(int numeroElegido) {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var cambio = new CambioVersionRepositorio(
                version(repositorio, "Formato A", 2), version(repositorio, "Formato A", numeroElegido));

        // Act & Assert
        assertThatCode(() -> rule.validar(cambio)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinUuidEnMensaje_cuandoOtroDocumentoDelMismoRepositorio() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var actual = version(repositorio, "Formato A", 1);
        var elegida = version(repositorio, "Formato B", 2);

        // Act & Assert
        verificarRechazo(actual, elegida);
    }

    @Test
    void debeLanzarSinUuidEnMensaje_cuandoOtroRepositorio() {
        // Arrange
        var actual = version(UtilUUID.generarNuevoUUID(), "Formato A", 1);
        var elegida = version(UtilUUID.generarNuevoUUID(), "Formato A", 2);

        // Act & Assert
        verificarRechazo(actual, elegida);
    }

    private void verificarRechazo(VersionRepositorioArtefactoDomain actual, VersionRepositorioArtefactoDomain elegida) {
        assertThatThrownBy(() -> rule.validar(new CambioVersionRepositorio(actual, elegida)))
                .isInstanceOf(VersionRepositorioOtroDocumentoException.class)
                .satisfies(ex -> {
                    var excepcion = (VersionRepositorioOtroDocumentoException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_OTRO_DOCUMENTO);
                    assertThat(excepcion.getError().getMessage())
                            .doesNotContain(actual.getId().toString())
                            .doesNotContain(elegida.getId().toString());
                });
    }
}
