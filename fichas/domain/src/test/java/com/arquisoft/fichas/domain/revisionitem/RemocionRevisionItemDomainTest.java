package com.arquisoft.fichas.domain.revisionitem;

import com.arquisoft.shared.message.constant.FichasCodes;
import com.arquisoft.shared.message.constant.FichasFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemocionRevisionItemDomainTest {

    @Test
    void debeAsignarAmbosCampos_cuandoDatosValidos() {
        // Arrange
        var revisionItem = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();

        // Act
        var remocion = RemocionRevisionItemDomain.crear(revisionItem, asesorFicha);

        // Assert
        assertThat(remocion.getRevisionItem()).isEqualTo(revisionItem);
        assertThat(remocion.getAsesorFicha()).isEqualTo(asesorFicha);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoRevisionItemYAsesorFichaNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RemocionRevisionItemDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting("campo")
                            .containsExactlyInAnyOrder(
                                    FichasFields.RevisionItem.REVISION_ITEM,
                                    FichasFields.RevisionItem.ASESOR_FICHA);
                    assertThat(errores).extracting("codigoError")
                            .containsExactlyInAnyOrder(
                                    FichasCodes.RevisionItem.REVISION_ITEM_REQUERIDO,
                                    FichasCodes.RevisionItem.ASESOR_FICHA_REQUERIDO);
                });
    }
}
