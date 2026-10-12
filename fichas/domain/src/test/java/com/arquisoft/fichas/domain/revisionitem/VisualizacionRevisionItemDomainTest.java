package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VisualizacionRevisionItemDomainTest {

    @Test
    void debeAsignarAmbosCampos_cuandoDatosValidos() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();

        // Act
        var visualizacion = VisualizacionRevisionItemDomain.crear(revisionItem, estudiante);

        // Assert
        assertThat(visualizacion.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(visualizacion.getEstudiante()).isEqualTo(estudiante);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoRevisionItemYEstudianteNulos() {
        // Act & Assert
        assertThatThrownBy(() -> VisualizacionRevisionItemDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo")
                            .containsExactlyInAnyOrder(
                                    FichasFields.RevisionItem.REVISION_ITEM,
                                    FichasFields.RevisionItem.ESTUDIANTE);
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(
                                    FichasCodes.RevisionItem.REVISION_ITEM_REQUERIDO,
                                    FichasCodes.RevisionItem.ESTUDIANTE_REQUERIDO);
                });
    }
}
