package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.CambioVersionRepositorioNoPermitidoException;
import com.arquisoft.artefactos.domain.artefacto.model.MomentoCambioVersionRepositorio;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambioVersionRepositorioPermitidoRuleImplTest {

    private final CambioVersionRepositorioPermitidoRuleImpl rule = new CambioVersionRepositorioPermitidoRuleImpl();

    private static RevisionAsesorDomain revisionEn(EstadoRevisionAsesor estado) {
        return RevisionAsesorDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), estado, UtilFecha.generarInstanteActual());
    }

    private static MomentoCambioVersionRepositorio momento(int ultimoNumero, RevisionAsesorDomain revision) {
        return new MomentoCambioVersionRepositorio(
                UtilUUID.generarNuevoUUID(), "Formato A", new RevisionVersionAnterior(ultimoNumero, revision));
    }

    @Test
    void debePasar_cuandoSinVersiones() {
        // Arrange
        var momento = momento(0, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(momento)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"PENDIENTE", "EN_PROGRESO"})
    void debePasar_cuandoRevisionPendienteOEnProgreso(EstadoRevisionAsesor estado) {
        // Arrange
        var momento = momento(2, revisionEn(estado));

        // Act & Assert
        assertThatCode(() -> rule.validar(momento)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoUltimaVersionSinRevision() {
        // Arrange
        var momento = momento(2, RevisionAsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(momento))
                .isInstanceOf(CambioVersionRepositorioNoPermitidoException.class)
                .satisfies(ex -> assertThat(((CambioVersionRepositorioNoPermitidoException) ex).getCodigoError())
                        .isEqualTo(ArtefactosCodes.Artefacto.CAMBIO_VERSION_REPOSITORIO_NO_PERMITIDO));
    }

    @ParameterizedTest
    @EnumSource(value = EstadoRevisionAsesor.class, names = {"RESUELTA", "CERRADA"})
    void debeLanzarSinUuidEnMensaje_cuandoRevisionResueltaOCerrada(EstadoRevisionAsesor estado) {
        // Arrange
        var momento = momento(2, revisionEn(estado));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(momento))
                .isInstanceOf(CambioVersionRepositorioNoPermitidoException.class)
                .satisfies(ex -> {
                    var excepcion = (CambioVersionRepositorioNoPermitidoException) ex;
                    assertThat(excepcion.getCodigoError())
                            .isEqualTo(ArtefactosCodes.Artefacto.CAMBIO_VERSION_REPOSITORIO_NO_PERMITIDO);
                    assertThat(excepcion.getError().getMessage())
                            .contains("Formato A")
                            .doesNotContain(momento.artefacto().toString());
                });
    }
}
