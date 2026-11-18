package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.proyectos.domain.estadoentregable.EstadoEntregable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoEntregableEventoTest {

    @Test
    void debeCoincidirConElCatalogoDelDominio_cuandoSeComparaCadaCodigo() {
        // Act & Assert
        for (var estado : EstadoEntregableEvento.values()) {
            assertThat(EstadoEntregable.esValido(estado.getCodigo())).isTrue();
            assertThat(EstadoEntregable.desde(estado.getCodigo()).getId()).isEqualTo(estado.getCodigo());
        }
        assertThat(EstadoEntregableEvento.values()).hasSize(
                EstadoEntregable.values().length - 1);
    }
}
