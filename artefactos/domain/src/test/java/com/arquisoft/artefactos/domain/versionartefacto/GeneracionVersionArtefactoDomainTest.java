package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeneracionVersionArtefactoDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var generacion = GeneracionVersionArtefactoDomain.crear(carga, artefacto, solicitante);

        // Assert
        assertThat(generacion.getCarga()).isEqualTo(carga);
        assertThat(generacion.getArtefacto()).isEqualTo(artefacto);
        assertThat(generacion.getSolicitante()).isEqualTo(solicitante);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanDatos() {
        // Act & Assert
        assertThatThrownBy(() -> GeneracionVersionArtefactoDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("campo").containsExactlyInAnyOrder(
                                ArtefactosFields.VersionArtefacto.CARGA,
                                ArtefactosFields.VersionArtefacto.ARTEFACTO,
                                ArtefactosFields.VersionArtefacto.SOLICITANTE));
    }
}
