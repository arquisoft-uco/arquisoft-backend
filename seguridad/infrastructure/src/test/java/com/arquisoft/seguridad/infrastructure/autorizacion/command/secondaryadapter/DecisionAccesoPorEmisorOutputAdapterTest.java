package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.TablaEquivalenciasDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.none.SinDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepDecisionAccesoOutputAdapter;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    private static AutenticacionProperties.Emisor emisor(String id, ModoAutorizacion modo) {
        return new AutenticacionProperties.Emisor(id, true, "https://" + id, "", List.of(), modo, false,
                new ClaimsIdentidad("sub", "email", "preferred_username", "name", "", ""));
    }

    private static SolicitudAcceso solicitud(String emisor) {
        return new SolicitudAcceso("GET", "/api/x", "tok", emisor, "c");
    }

    @BeforeEach
    void preparar() {
        var propiedades = new AutenticacionProperties(List.of(emisor("a", ModoAutorizacion.PEP),
                emisor("b", ModoAutorizacion.ROLE_TABLE), emisor("c", ModoAutorizacion.NONE)));
        adaptador = new DecisionAccesoPorEmisorOutputAdapter(propiedades, pep, tabla, sin);
    }

    @Test
    void debeDelegarEnElAdaptadorDelModoDelEmisor_cuandoDecide() {
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
}
