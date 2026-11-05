package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedorAutenticacion;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.TablaEquivalenciasDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.none.SinDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionAccesoPorEmisorOutputAdapterTest {

    @Mock
    private PepDecisionAccesoOutputAdapter pep;

    @Mock
    private TablaEquivalenciasDecisionAccesoOutputAdapter tabla;

    @Mock
    private SinDecisionAccesoOutputAdapter sin;

    private DecisionAccesoPorEmisorOutputAdapter adaptador;

    private static ProveedorAutenticacion proveedor(String emisor, ModoAutorizacion modo) {
        var proveedor = mock(ProveedorAutenticacion.class);
        lenient().when(proveedor.habilitado()).thenReturn(true);
        lenient().when(proveedor.autorizacion(emisor)).thenReturn(Optional.of(modo));
        lenient().when(proveedor.autorizacion(org.mockito.ArgumentMatchers.argThat(id -> !emisor.equals(id))))
                .thenReturn(Optional.empty());
        return proveedor;
    }

    private static SolicitudAcceso solicitud(String emisor) {
        return new SolicitudAcceso("GET", "/api/x", "tok", emisor, "c");
    }

    @BeforeEach
    void preparar() {
        var proveedores = new ProveedoresAutenticacion(List.of(proveedor("a", ModoAutorizacion.PEP),
                proveedor("b", ModoAutorizacion.ROLE_TABLE), proveedor("c", ModoAutorizacion.NONE)));
        adaptador = new DecisionAccesoPorEmisorOutputAdapter(proveedores, pep, tabla, sin);
    }

    @Test
    void debeElegirElAdaptadorSegunElModoDelProveedorDelEmisor() {
        // Arrange
        var denegada = new DecisionAcceso.Denegada();
        var permitida = new DecisionAcceso.Permitida("");
        var noAutenticada = new DecisionAcceso.NoAutenticada();
        when(pep.decidir(solicitud("a"))).thenReturn(noAutenticada);
        when(tabla.decidir(solicitud("b"))).thenReturn(denegada);
        when(sin.decidir(solicitud("c"))).thenReturn(permitida);

        // Act / Assert
        assertThat(adaptador.decidir(solicitud("a"))).isSameAs(noAutenticada);
        assertThat(adaptador.decidir(solicitud("b"))).isSameAs(denegada);
        assertThat(adaptador.decidir(solicitud("c"))).isSameAs(permitida);
        verify(pep).decidir(solicitud("a"));
    }

    @Test
    void debeLanzar503_cuandoElEmisorEsDesconocido() {
        // Act / Assert
        assertThatThrownBy(() -> adaptador.decidir(solicitud("zzz")))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        verifyNoInteractions(pep, tabla, sin);
    }

    @Test
    void debeLanzarNoDisponible_cuandoNingunProveedorAtiendeElEmisor() {
        // Arrange
        var apagado = mock(ProveedorAutenticacion.class);
        when(apagado.habilitado()).thenReturn(false);
        var adaptadorSinProveedor = new DecisionAccesoPorEmisorOutputAdapter(
                new ProveedoresAutenticacion(List.of(apagado)), pep, tabla, sin);

        // Act / Assert
        assertThatThrownBy(() -> adaptadorSinProveedor.decidir(solicitud("a")))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
        verifyNoInteractions(pep, tabla, sin);
    }
}
