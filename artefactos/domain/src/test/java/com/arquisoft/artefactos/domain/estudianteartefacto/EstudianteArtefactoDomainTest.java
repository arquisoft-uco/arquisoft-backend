package com.arquisoft.artefactos.domain.estudianteartefacto;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import com.arquisoft.shared.validation.ValidationResult.ValidationError;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

class EstudianteArtefactoDomainTest {

    @Test
    void debeCrearProductoCartesiano_cuandoHayVariosArtefactosYEstudiantes() {
        // Arrange
        var artefactos = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        var estudiantes = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var relaciones = EstudianteArtefactoDomain.crear(artefactos, estudiantes);

        // Assert
        assertThat(relaciones).hasSize(6);
        assertThat(relaciones).extracting(EstudianteArtefactoDomain::getId).doesNotHaveDuplicates().doesNotContainNull();
        assertThat(relaciones.stream().map(EstudianteArtefactoDomain::getArtefacto).distinct().toList())
                .containsExactlyElementsOf(artefactos);
        assertThat(relaciones.stream().map(EstudianteArtefactoDomain::getEstudiante).distinct().toList())
                .containsExactlyElementsOf(estudiantes);
    }

    @Test
    void debeAcumularErrores_cuandoUnaListaEstaVaciaYLaOtraDuplicada() {
        // Arrange
        var repetido = UtilUUID.generarNuevoUUID();
        var artefactosDuplicados = List.of(repetido, repetido);
        var estudiantesVacios = List.<UUID>of();

        // Act & Assert
        assertThatThrownBy(() -> EstudianteArtefactoDomain.crear(artefactosDuplicados, estudiantesVacios))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .extracting(ValidationError::campo, ValidationError::codigoError)
                        .containsExactlyInAnyOrder(
                                tuple(ArtefactosFields.EstudianteArtefacto.ARTEFACTOS,
                                        ArtefactosCodes.EstudianteArtefacto.ARTEFACTOS_DUPLICADOS),
                                tuple(ArtefactosFields.EstudianteArtefacto.ESTUDIANTES,
                                        ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_REQUERIDOS)));
    }

    @Test
    void debeRechazarEstudiantesDuplicados_cuandoSeRepiteUnId() {
        // Arrange
        var repetido = UtilUUID.generarNuevoUUID();
        var artefactos = List.of(UtilUUID.generarNuevoUUID());
        var estudiantes = List.of(repetido, repetido);

        // Act & Assert
        assertThatThrownBy(() -> EstudianteArtefactoDomain.crear(artefactos, estudiantes))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> {
                            assertThat(error.campo()).isEqualTo(ArtefactosFields.EstudianteArtefacto.ESTUDIANTES);
                            assertThat(error.codigoError())
                                    .isEqualTo(ArtefactosCodes.EstudianteArtefacto.ESTUDIANTES_DUPLICADOS);
                        }));
    }
}
