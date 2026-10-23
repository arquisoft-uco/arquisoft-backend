package com.arquisoft.seguridad.infrastructure.mapeoroles.command.primaryadapter.scheduler;

import com.arquisoft.seguridad.application.mapeoroles.command.primaryport.interactor.ActualizarMapeoRolesInteractor;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.MapeoRolesKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.AlcanceTraza;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.tracing.domain.traza.model.SolicitudTraza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarMapeoRolesSchedulerTest {

    @Mock
    private ActualizarMapeoRolesInteractor actualizarMapeoRolesInteractor;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private AlcanceTraza alcance;

    @Mock
    private AppLogger logger;

    @BeforeEach
    void preparar() {
        when(gestorTraza.abrir(any(SolicitudTraza.class))).thenReturn(alcance);
    }

    private ActualizarMapeoRolesScheduler scheduler(ModoAutorizacion modo) {
        return scheduler(modo, true);
    }

    private ActualizarMapeoRolesScheduler scheduler(ModoAutorizacion modo, boolean habilitado) {
        var emisor = new AutenticacionProperties.Emisor("legado", habilitado, "https://legado", "", List.of(), modo, true,
                new ClaimsIdentidad("sub", "email", "u", "n", "realm_access.roles"));
        return new ActualizarMapeoRolesScheduler(actualizarMapeoRolesInteractor,
                new AutenticacionProperties(List.of(emisor)), gestorTraza, logger);
    }

    @Test
    void debeInvocarElInteractor_cuandoHayEmisorRoleTable() {
        // Act
        scheduler(ModoAutorizacion.ROLE_TABLE).actualizar();

        // Assert
        verify(actualizarMapeoRolesInteractor).ejecutar();
    }

    @Test
    void debeNoHacerNada_cuandoNingunEmisorUsaRoleTable() {
        // Act
        scheduler(ModoAutorizacion.PEP).actualizar();

        // Assert
        verifyNoInteractions(actualizarMapeoRolesInteractor);
    }

    @Test
    void debeNoHacerNada_cuandoElEmisorRoleTableEstaDeshabilitado() {
        // Act
        scheduler(ModoAutorizacion.ROLE_TABLE, false).actualizar();

        // Assert
        verifyNoInteractions(actualizarMapeoRolesInteractor);
    }

    @Test
    void debeCapturarYLoguear_cuandoElInteractorLanza() {
        // Arrange
        var fallo = new IllegalStateException("boom");
        when(actualizarMapeoRolesInteractor.ejecutar()).thenThrow(fallo);

        // Act
        scheduler(ModoAutorizacion.ROLE_TABLE).actualizar();

        // Assert
        verify(logger).error(MapeoRolesKey.LOG_REFRESCO_INESPERADO, fallo, "boom");
    }

    @Test
    void debeNoVolverALoguear_cuandoElAlmacenNoEstaDisponible() {
        // Arrange
        when(actualizarMapeoRolesInteractor.ejecutar())
                .thenThrow(new ProveedorAutorizacionNoDisponibleException("no disponible"));

        // Act
        scheduler(ModoAutorizacion.ROLE_TABLE).actualizar();

        // Assert
        verifyNoInteractions(logger);
    }

    @Test
    void debeAbrirSuPropioAlcanceDeTraza() {
        // Act
        scheduler(ModoAutorizacion.ROLE_TABLE).actualizar();

        // Assert
        var orden = inOrder(gestorTraza, actualizarMapeoRolesInteractor, alcance);
        orden.verify(gestorTraza).abrir(any(SolicitudTraza.class));
        orden.verify(actualizarMapeoRolesInteractor).ejecutar();
        orden.verify(alcance).close();
    }
}
