package com.arquisoft.artefactos.domain.asesorproyectogrado;

import com.arquisoft.artefactos.domain.artefacto.TraspasoArtefactosDomain;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregacionAsesorProyectoGradoDomainTest {

    @Test
    void debeCrear_cuandoVinculoYTraspasoValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var vinculo = AsesorProyectoGradoDomain.crear(asesor, proyecto, Instant.parse("2026-10-02T10:00:00Z"));
        var traspaso = TraspasoArtefactosDomain.crear(proyecto, asesor);

        // Act
        var agregacion = AgregacionAsesorProyectoGradoDomain.crear(vinculo, traspaso);

        // Assert
        assertThat(agregacion.getVinculo()).isSameAs(vinculo);
        assertThat(agregacion.getTraspaso()).isSameAs(traspaso);
    }

    @Test
    void debeSerElMasReciente_soloCuandoSuOcurridoEnEsPosteriorAlUltimo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.parse("2026-10-02T10:00:00Z");
        var agregacion = AgregacionAsesorProyectoGradoDomain.crear(
                AsesorProyectoGradoDomain.crear(asesor, proyecto, ocurridoEn),
                TraspasoArtefactosDomain.crear(proyecto, asesor));

        // Act
        var frenteAAnterior = agregacion.esVinculoMasReciente(ocurridoEn.minusSeconds(1));
        var frenteAEmpate = agregacion.esVinculoMasReciente(ocurridoEn);
        var frenteAPosterior = agregacion.esVinculoMasReciente(ocurridoEn.plusSeconds(1));

        // Assert
        assertThat(frenteAAnterior).isTrue();
        assertThat(frenteAEmpate).isFalse();
        assertThat(frenteAPosterior).isFalse();
    }

    @Test
    void debeLanzar_cuandoVinculoYTraspasoSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> AgregacionAsesorProyectoGradoDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).extracting(error -> error.campo()).containsExactlyInAnyOrder(
                            ArtefactosFields.AsesorProyectoGrado.VINCULO,
                            ArtefactosFields.AsesorProyectoGrado.TRASPASO);
                    assertThat(errores).extracting(error -> error.codigoError()).containsExactlyInAnyOrder(
                            ArtefactosCodes.AsesorProyectoGrado.VINCULO_REQUERIDO,
                            ArtefactosCodes.AsesorProyectoGrado.TRASPASO_REQUERIDO);
                });
    }
}
