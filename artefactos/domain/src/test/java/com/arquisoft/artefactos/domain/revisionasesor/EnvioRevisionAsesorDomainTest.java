package com.arquisoft.artefactos.domain.revisionasesor;

import com.arquisoft.artefactos.domain.observacion.AvanceObservacionesRevisionDomain;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnvioRevisionAsesorDomainTest {

    @Test
    void debeCrear_cuandoDatosValidos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        var avance = AvanceObservacionesRevisionDomain.crear(revision);

        // Act
        var envio = EnvioRevisionAsesorDomain.crear(revision, asesor, avance);

        // Assert
        assertThat(envio.getRevisionAsesor()).isEqualTo(revision);
        assertThat(envio.getAsesor()).isEqualTo(asesor);
        assertThat(envio.getObservaciones()).isSameAs(avance);
    }

    @Test
    void debeAcumularFieldErrors_cuandoFaltanTodosLosComponentes() {
        // Act & Assert
        assertThatThrownBy(() -> EnvioRevisionAsesorDomain.crear(null, null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ID);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.RevisionAsesor.ASESOR);
                        assertThat(error.codigoError()).isEqualTo(ArtefactosCodes.RevisionAsesor.ASESOR_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ArtefactosFields.Observacion.REVISION_ASESOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ArtefactosCodes.Observacion.REVISION_ASESOR_ID_REQUERIDO);
                    });
                });
    }
}
