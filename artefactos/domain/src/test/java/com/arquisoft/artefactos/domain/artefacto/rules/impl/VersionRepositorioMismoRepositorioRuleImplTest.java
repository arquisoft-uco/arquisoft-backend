package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioOtroRepositorioException;
import com.arquisoft.artefactos.domain.artefacto.model.CambioVersionRepositorio;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain.DatosArchivo;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VersionRepositorioMismoRepositorioRuleImplTest {

    private final VersionRepositorioMismoRepositorioRuleImpl rule = new VersionRepositorioMismoRepositorioRuleImpl();

    private static VersionRepositorioArtefactoDomain version(int numero, UUID repositorio) {
        return VersionRepositorioArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "Formato A", numero, repositorio, "Repositorio Uno",
                new DatosArchivo("a.pdf", "application/pdf", 10L));
    }

    @Test
    void debePasar_cuandoMismoRepositorio() {
        // Arrange
        var repositorio = UtilUUID.generarNuevoUUID();
        var cambio = new CambioVersionRepositorio(version(1, repositorio), version(2, repositorio));

        // Act & Assert
        assertThatCode(() -> rule.validar(cambio)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarSinUuidEnMensaje_cuandoOtroRepositorio() {
        // Arrange
        var actual = version(1, UtilUUID.generarNuevoUUID());
        var elegida = version(2, UtilUUID.generarNuevoUUID());

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new CambioVersionRepositorio(actual, elegida)))
                .isInstanceOf(VersionRepositorioOtroRepositorioException.class)
                .satisfies(ex -> {
                    var excepcion = (VersionRepositorioOtroRepositorioException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.Artefacto.VERSION_REPOSITORIO_OTRO_REPOSITORIO);
                    assertThat(excepcion.getError().getMessage())
                            .contains("Repositorio Uno")
                            .doesNotContain(actual.getId().toString())
                            .doesNotContain(elegida.getId().toString());
                });
    }
}
