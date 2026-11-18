package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.ArchivoVersionPurgadoException;
import com.arquisoft.artefactos.domain.versionartefacto.model.DisponibilidadArchivoVersion;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArchivoVersionNoPurgadoRuleImplTest {

    private final ArchivoVersionNoPurgadoRuleImpl regla = new ArchivoVersionNoPurgadoRuleImpl();

    @Test
    void debePasar_cuandoProyectoActivo() {
        // Arrange
        var disponibilidad = new DisponibilidadArchivoVersion(UtilUUID.generarNuevoUUID(), "Proyecto", false);

        // Act & Assert
        assertThatCode(() -> regla.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoProyectoFinalizado() {
        // Arrange
        var disponibilidad = new DisponibilidadArchivoVersion(UtilUUID.generarNuevoUUID(), "Proyecto", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(ArchivoVersionPurgadoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.VersionArtefacto.ARCHIVO_PURGADO);
    }

    @Test
    void debeOcultarIdentificadores_enMensajeDeCliente() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadArchivoVersion(version, "Proyecto Alfa", true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(disponibilidad))
                .isInstanceOf(ArchivoVersionPurgadoException.class)
                .satisfies(ex -> {
                    var excepcion = (ArchivoVersionPurgadoException) ex;
                    assertThat(excepcion.getError().getMessage()).contains("Proyecto Alfa")
                            .doesNotContain(version.toString());
                    assertThat(excepcion.getMessage()).contains(version.toString());
                });
    }
}
