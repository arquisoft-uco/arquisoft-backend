package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.TransicionEstadoAsesorInvalidaException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.TransicionEstadoAsesor;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransicionEstadoAsesorPermitidaRuleImplTest {

    private final TransicionEstadoAsesorPermitidaRuleImpl rule = new TransicionEstadoAsesorPermitidaRuleImpl();

    private static TransicionEstadoAsesor transicion(EstadoAsesor actual, EstadoAsesor nuevo) {
        return new TransicionEstadoAsesor(UtilUUID.generarNuevoUUID(), actual, nuevo);
    }

    @Test
    void debePasar_cuandoAsignadoAFinalizado() {
        // Act & Assert
        assertThatCode(() -> rule.validar(transicion(EstadoAsesor.ASIGNADO, EstadoAsesor.FINALIZADO)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoFinalizadoAFinalizado() {
        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion(EstadoAsesor.FINALIZADO, EstadoAsesor.FINALIZADO)))
                .isInstanceOf(TransicionEstadoAsesorInvalidaException.class)
                .satisfies(ex -> assertThat(((TransicionEstadoAsesorInvalidaException) ex).getCodigoError())
                        .isEqualTo(ProyectosCodes.AsesorProyectoGrado.TRANSICION_ESTADO_ASESOR_INVALIDA));
    }

    @Test
    void debeLanzar_cuandoFinalizadoAAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion(EstadoAsesor.FINALIZADO, EstadoAsesor.ASIGNADO)))
                .isInstanceOf(TransicionEstadoAsesorInvalidaException.class);
    }

    @Test
    void debeLanzar_cuandoAsignadoAAsignado() {
        // Act & Assert
        assertThatThrownBy(() -> rule.validar(transicion(EstadoAsesor.ASIGNADO, EstadoAsesor.ASIGNADO)))
                .isInstanceOf(TransicionEstadoAsesorInvalidaException.class);
    }
}
