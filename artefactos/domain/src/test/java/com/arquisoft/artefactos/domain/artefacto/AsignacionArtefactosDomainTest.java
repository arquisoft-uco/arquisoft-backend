package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignacionArtefactosDomainTest {

    @Test
    void debeCrearYExponerVersionesEnOrden_cuandoHayArtefactos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var artefactos = List.of(
                ArtefactoDomain.crear(proyecto, primera, asesor),
                ArtefactoDomain.crear(proyecto, segunda, asesor));

        // Act
        var asignacion = AsignacionArtefactosDomain.crear(proyecto, asesor, artefactos);

        // Assert
        assertThat(asignacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(asignacion.getAsesor()).isEqualTo(asesor);
        assertThat(asignacion.getArtefactos()).isEqualTo(artefactos);
        assertThat(asignacion.getVersionesRepositorioArtefacto()).containsExactly(primera, segunda);
    }

    @Test
    void debeAcumularErrores_cuandoFaltanProyectoAsesorYArtefactos() {
        // Act & Assert
        assertThatThrownBy(() -> AsignacionArtefactosDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.Artefacto.PROYECTO_GRADO,
                            ArtefactosFields.Artefacto.ASESOR,
                            ArtefactosFields.Artefacto.ARTEFACTOS);
                    assertThat(errores).extracting(error -> error.codigoError())
                            .contains(ArtefactosCodes.Artefacto.ARTEFACTOS_REQUERIDOS);
                });
    }

    @Test
    void debeLanzar_cuandoLaListaDeArtefactosEstaVacia() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var vacia = List.<ArtefactoDomain>of();

        // Act & Assert
        assertThatThrownBy(() -> AsignacionArtefactosDomain.crear(proyecto, asesor, vacia))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Artefacto.ARTEFACTOS_REQUERIDOS)));
    }
}
