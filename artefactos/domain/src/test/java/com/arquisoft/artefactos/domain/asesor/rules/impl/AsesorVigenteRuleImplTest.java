package com.arquisoft.artefactos.domain.asesor.rules.impl;

import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.artefactos.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorVigenteRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AsesorVigenteRuleImpl regla = new AsesorVigenteRuleImpl();

    private static AsesorDomain asesor(UUID id, Instant eliminadoEn) {
        return AsesorDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn);
    }

    @Test
    void debeLanzarExcepcionConElNombreDelAsesor_cuandoElAsesorFueDadoDeBaja() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesor(id, asesor(id, Instant.parse("2026-09-20T10:00:00Z")));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOfSatisfying(AsesorNoVigenteException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Asesor.NO_VIGENTE);
                    assertThat(ex.getError().getMessage()).contains(NOMBRE).doesNotContain(id.toString());
                });
    }

    @Test
    void debePasar_cuandoElAsesorExisteYEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesor(id, asesor(id, null));

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }
}
