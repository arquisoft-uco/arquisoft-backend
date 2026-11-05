package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProveedoresAutenticacionTest {

    @Test
    void debeResolverElProveedorYElModo_porEmisor() {
        // Arrange
        var bearer = mock(ProveedorAutenticacion.class);
        when(bearer.habilitado()).thenReturn(true);
        when(bearer.autorizacion("principal")).thenReturn(Optional.of(ModoAutorizacion.ROLE_TABLE));
        when(bearer.autorizacion("sesion-pdp")).thenReturn(Optional.empty());
        var sesion = mock(ProveedorAutenticacion.class);
        when(sesion.habilitado()).thenReturn(true);
        when(sesion.autorizacion("sesion-pdp")).thenReturn(Optional.of(ModoAutorizacion.PEP));
        when(sesion.autorizacion("principal")).thenReturn(Optional.empty());
        var registro = new ProveedoresAutenticacion(List.of(bearer, sesion));

        // Act / Assert
        assertThat(registro.porEmisor("principal")).containsSame(bearer);
        assertThat(registro.porEmisor("sesion-pdp")).containsSame(sesion);
        assertThat(registro.modoDe("principal")).contains(ModoAutorizacion.ROLE_TABLE);
        assertThat(registro.modoDe("sesion-pdp")).contains(ModoAutorizacion.PEP);
        assertThat(registro.modoDe("otro")).isEmpty();
    }

    @Test
    void debeExcluirLosProveedoresQueNoActuanEnRutasNoDeNegocio_alPedirAplicables() {
        // Arrange
        var bearer = mock(ProveedorAutenticacion.class);
        when(bearer.habilitado()).thenReturn(true);
        when(bearer.actuaEnRutasNoDeNegocio()).thenReturn(true);
        var sesion = mock(ProveedorAutenticacion.class);
        when(sesion.habilitado()).thenReturn(true);
        when(sesion.actuaEnRutasNoDeNegocio()).thenReturn(false);
        var registro = new ProveedoresAutenticacion(List.of(bearer, sesion));

        // Act / Assert
        assertThat(registro.aplicables(false)).containsExactly(bearer, sesion);
        assertThat(registro.aplicables(true)).containsExactly(bearer);
    }

    @Test
    void debeDevolverElPresentador_cuandoElEmisorTieneProveedorHabilitado() {
        // Arrange
        var bearer = mock(ProveedorAutenticacion.class);
        when(bearer.habilitado()).thenReturn(true);
        when(bearer.autorizacion("principal")).thenReturn(Optional.of(ModoAutorizacion.ROLE_TABLE));
        var registro = new ProveedoresAutenticacion(List.of(bearer));

        // Act
        var presentador = registro.presentadorDe("principal");

        // Assert
        assertThat(presentador).containsSame(bearer);
    }

    @Test
    void debeDevolverVacio_cuandoNingunProveedorAtiendeAlEmisor() {
        // Arrange
        var bearer = mock(ProveedorAutenticacion.class);
        when(bearer.habilitado()).thenReturn(true);
        when(bearer.autorizacion("otro")).thenReturn(Optional.empty());
        var registro = new ProveedoresAutenticacion(List.of(bearer));

        // Act
        var presentador = registro.presentadorDe("otro");

        // Assert
        assertThat(presentador).isEmpty();
    }
}
