package com.arquisoft.artefactos.infrastructure.revisionasesor.command.secondaryadapter.repository;

import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoRevisionAsesorPersistenciaTest {

    @Test
    void debeResolverContraElCatalogoDeDominio_cuandoSeRecorreCadaCodigo() {
        // Assert
        for (var estado : EstadoRevisionAsesorPersistencia.values()) {
            assertThat(EstadoRevisionAsesor.desde(estado.getCodigo()).getId())
                    .isEqualTo(EstadoRevisionAsesor.valueOf(estado.name()).getId());
        }
    }

    @Test
    void debeDeclararLosMismosEstadosQueElDominio_cuandoSeComparanAmbasTablas() {
        // Arrange
        var enInfraestructura = Arrays.stream(EstadoRevisionAsesorPersistencia.values())
                .map(Enum::name)
                .collect(Collectors.toSet());
        var enDominio = Arrays.stream(EstadoRevisionAsesor.values())
                .filter(estado -> estado != EstadoRevisionAsesor.VACIO)
                .map(Enum::name)
                .collect(Collectors.toSet());

        // Assert
        assertThat(enInfraestructura).isEqualTo(enDominio);
    }
}
