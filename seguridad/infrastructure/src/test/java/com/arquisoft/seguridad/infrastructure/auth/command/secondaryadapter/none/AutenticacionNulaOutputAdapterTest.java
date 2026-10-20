package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.none;

import com.arquisoft.seguridad.infrastructure.auth.exception.ProveedorIdentidadNoDisponibleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutenticacionNulaOutputAdapterTest {

    private final AutenticacionNulaOutputAdapter adaptador = new AutenticacionNulaOutputAdapter();

    @Test
    void debeLanzar503_cuandoAutenticaORefresca() {
        // Act / Assert
        assertThatThrownBy(() -> adaptador.autenticar("a@uco.edu.co", "x"))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class);
        assertThatThrownBy(() -> adaptador.refrescar("tok"))
                .isInstanceOf(ProveedorIdentidadNoDisponibleException.class);
    }
}
