package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.message.constant.RepositorioArtefactosCodes;
import com.arquisoft.shared.message.constant.RepositorioArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SubidaVersionRepositorioArtefactoDomainTest {

    @Test
    void debeCrear_conTextosRecortados() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var subida = SubidaVersionRepositorioArtefactoDomain.crear(
                carga, repositorio, solicitante, "  Formato A  ", true);

        // Assert
        assertThat(subida.getCarga()).isEqualTo(carga);
        assertThat(subida.getRepositorio()).isEqualTo(repositorio);
        assertThat(subida.getSolicitante()).isEqualTo(solicitante);
        assertThat(subida.getNombreDocumento()).isEqualTo("Formato A");
    }

    @Test
    void debeAcumularFieldErrors_cuandoNombreDocumentoVacioYCargaNula() {
        // Act & Assert
        assertThatThrownBy(() -> SubidaVersionRepositorioArtefactoDomain.crear(
                null, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "   ", false))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("campo")
                        .contains(RepositorioArtefactosFields.VersionRepositorioArtefacto.CARGA,
                                RepositorioArtefactosFields.VersionRepositorioArtefacto.NOMBRE_DOCUMENTO));
    }

    @Test
    void debeRechazar_cuandoFaltanRepositorioYSolicitante() {
        // Act & Assert
        assertThatThrownBy(() -> SubidaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), null, null, "Formato", false))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(
                                RepositorioArtefactosCodes.VersionRepositorioArtefacto.REPOSITORIO_REQUERIDO,
                                RepositorioArtefactosCodes.VersionRepositorioArtefacto.SOLICITANTE_REQUERIDO));
    }

    @Test
    void debeAceptar_nombreDocumentoDe100() {
        // Act & Assert
        assertThatCode(() -> SubidaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "n".repeat(100), true)).doesNotThrowAnyException();
    }

    @Test
    void debeRechazar_nombreDocumentoDe101() {
        // Act & Assert
        assertThatThrownBy(() -> SubidaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "n".repeat(101), true))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting("codigoError")
                        .contains(RepositorioArtefactosCodes.VersionRepositorioArtefacto
                                .NOMBRE_DOCUMENTO_DEMASIADO_LARGO));
    }

    @Test
    void debeConservar_nuevoDocumento() {
        // Arrange
        var nuevo = SubidaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "F", true);
        var existente = SubidaVersionRepositorioArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "F", false);

        // Act & Assert
        assertThat(nuevo.isNuevoDocumento()).isTrue();
        assertThat(existente.isNuevoDocumento()).isFalse();
    }
}
