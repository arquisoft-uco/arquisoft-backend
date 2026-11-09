package com.arquisoft.entregables.domain.artefactoentregable;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ArtefactoEntregableDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var versionRepositorio = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();

        // Act
        var snapshot = ArtefactoEntregableDomain.crear(entregable, "  01-Informe/v3-informe.pdf ", artefacto,
                versionRepositorio, " Informe ", repositorio, " Repo ");
        var otro = ArtefactoEntregableDomain.crear(entregable, "01-Informe/v3-informe.pdf", artefacto,
                versionRepositorio, "Informe", repositorio, "Repo");

        // Assert
        assertThat(snapshot.getEntregableProyectoGrado()).isEqualTo(entregable);
        assertThat(snapshot.getUriArchivo()).isEqualTo("01-Informe/v3-informe.pdf");
        assertThat(snapshot.getArtefacto()).isEqualTo(artefacto);
        assertThat(snapshot.getVersionRepositorioArtefacto()).isEqualTo(versionRepositorio);
        assertThat(snapshot.getNombreDocumento()).isEqualTo("Informe");
        assertThat(snapshot.getRepositorio()).isEqualTo(repositorio);
        assertThat(snapshot.getNombreRepositorio()).isEqualTo("Repo");
        assertThat(snapshot.getId()).isNotNull().isNotEqualTo(snapshot.getVersionArtefacto());
        assertThat(snapshot.getVersionArtefacto()).isNotNull();
        assertThat(otro.getId()).isNotEqualTo(snapshot.getId());
        assertThat(otro.getVersionArtefacto()).isNotEqualTo(snapshot.getVersionArtefacto());
    }

    @Test
    void debeAcumularErrores_cuandoVariosCamposInvalidos() {
        // Act & Assert
        assertThatThrownBy(() -> ArtefactoEntregableDomain.crear(null, " ", null, null, "", null, null))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.ENTREGABLE_PROYECTO_GRADO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.URI_ARCHIVO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.ARTEFACTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.VERSION_REPOSITORIO_ARTEFACTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.NOMBRE_DOCUMENTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.REPOSITORIO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.NOMBRE_REPOSITORIO)).isTrue();
                });
    }

    @Test
    void debeRechazar_cuandoUnTextoExcedeSuLongitud() {
        // Act & Assert
        assertThatThrownBy(() -> ArtefactoEntregableDomain.crear(UtilUUID.generarNuevoUUID(), "r".repeat(2001),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "d".repeat(101),
                UtilUUID.generarNuevoUUID(), "n".repeat(101)))
                .isInstanceOfSatisfying(DomainValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.ArtefactoEntregable.URI_ARCHIVO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.NOMBRE_DOCUMENTO)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(
                            EntregablesFields.ArtefactoEntregable.NOMBRE_REPOSITORIO)).isTrue();
                });
    }
}
