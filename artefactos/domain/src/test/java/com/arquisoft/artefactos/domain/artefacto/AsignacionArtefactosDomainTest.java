package com.arquisoft.artefactos.domain.artefacto;

import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsignacionArtefactosDomainTest {

    @Test
    void debeCrearYDerivarProyectoAsesorYVersiones_cuandoHayArtefactos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var artefactos = List.of(
                ArtefactoDomain.crear(proyecto, asesor, List.of(primera)).getFirst(),
                ArtefactoDomain.crear(proyecto, asesor, List.of(segunda)).getFirst());

        // Act
        var asignacion = AsignacionArtefactosDomain.crear(artefactos, AgregacionEstudiantesArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(),
                EstudianteArtefactoDomain.crear(List.of(UtilUUID.generarNuevoUUID()), List.of(UtilUUID.generarNuevoUUID()))));

        // Assert
        assertThat(asignacion.getProyectoGrado()).isEqualTo(proyecto);
        assertThat(asignacion.getAsesor()).isEqualTo(asesor);
        assertThat(asignacion.getArtefactos()).isEqualTo(artefactos);
        assertThat(asignacion.getVersionesRepositorioArtefacto()).containsExactly(primera, segunda);
    }


    @Test
    void debeLanzar_cuandoLaListaEsNulaOVacia() {
        // Arrange
        var vacia = List.<ArtefactoDomain>of();

        // Act & Assert
        for (var artefactos : Arrays.asList(null, vacia)) {
            assertThatThrownBy(() -> AsignacionArtefactosDomain.crear(artefactos, AgregacionEstudiantesArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(),
                EstudianteArtefactoDomain.crear(List.of(UtilUUID.generarNuevoUUID()), List.of(UtilUUID.generarNuevoUUID())))))
                    .isInstanceOf(DomainValidationException.class)
                    .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                            .singleElement()
                            .satisfies(error -> {
                                assertThat(error.campo()).isEqualTo(ArtefactosFields.Artefacto.ARTEFACTOS);
                                assertThat(error.codigoError())
                                        .isEqualTo(ArtefactosCodes.Artefacto.ARTEFACTOS_REQUERIDOS);
                            }));
        }
    }

    @Test
    void debeLanzar_cuandoLosEstudiantesSonNulos() {
        // Arrange
        var artefactos = ArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), List.of(UtilUUID.generarNuevoUUID()));

        // Act & Assert
        assertThatThrownBy(() -> AsignacionArtefactosDomain.crear(artefactos, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(ArtefactosFields.EstudianteArtefacto.ESTUDIANTES);
                            assertThat(error.codigoError())
                                    .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS);
                        }));
    }
}
