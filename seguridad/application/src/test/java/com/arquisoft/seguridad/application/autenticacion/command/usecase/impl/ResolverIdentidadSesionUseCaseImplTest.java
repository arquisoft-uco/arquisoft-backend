package com.arquisoft.seguridad.application.autenticacion.command.usecase.impl;

import com.arquisoft.seguridad.application.autenticacion.command.finder.UsuarioIdPorCorreoFinder;
import com.arquisoft.seguridad.application.autenticacion.command.result.ResolucionSesionResult;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.ResolucionSesionOutputPort;
import com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model.ResolucionSesion;
import com.arquisoft.seguridad.domain.autenticacion.CredencialSesionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolverIdentidadSesionUseCaseImplTest {

    private static final String COOKIE = "SECURITY_BASELINE_SESSION=abc";

    @Mock
    private ResolucionSesionOutputPort resolucionSesionOutputPort;

    @Mock
    private UsuarioIdPorCorreoFinder usuarioIdPorCorreoFinder;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ResolverIdentidadSesionUseCaseImpl useCase;

    @Test
    void debeDevolverResueltaConElIdDeUsuario_cuandoElCorreoEstaRegistrado() {
        // Arrange
        var credencial = CredencialSesionDomain.crear(COOKIE);
        var usuario = UtilUUID.generarNuevoUUID();
        when(resolucionSesionOutputPort.resolver(COOKIE))
                .thenReturn(new ResolucionSesion.Resuelta("sujeto-pdp", "Ana", "ana@uco.edu.co", "tenant-1"));
        when(usuarioIdPorCorreoFinder.obtener("ana@uco.edu.co")).thenReturn(usuario);

        // Act
        var resultado = useCase.ejecutar(credencial);

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ResolucionSesionResult.Resuelta.class, resuelta -> {
            assertThat(resuelta.sujeto()).isEqualTo(usuario.toString());
            assertThat(resuelta.nombre()).isEqualTo("Ana");
            assertThat(resuelta.correo()).isEqualTo("ana@uco.edu.co");
            assertThat(resuelta.tenant()).isEqualTo("tenant-1");
        });
        verify(resolucionSesionOutputPort).resolver(COOKIE);
    }

    @Test
    void debeDevolverUsuarioNoRegistrado_cuandoElFinderDevuelveElIdPorDefecto() {
        // Arrange
        var credencial = CredencialSesionDomain.crear(COOKIE);
        when(resolucionSesionOutputPort.resolver(COOKIE))
                .thenReturn(new ResolucionSesion.Resuelta("sujeto-pdp", "Ana", "ana@uco.edu.co", "tenant-1"));
        when(usuarioIdPorCorreoFinder.obtener("ana@uco.edu.co")).thenReturn(UtilUUID.obtenerUUIDPorDefecto());

        // Act
        var resultado = useCase.ejecutar(credencial);

        // Assert
        assertThat(resultado).isInstanceOf(ResolucionSesionResult.UsuarioNoRegistrado.class);
    }

    @Test
    void debeDevolverSinSesion_sinConsultarUsuarios() {
        // Arrange
        var credencial = CredencialSesionDomain.crear(COOKIE);
        when(resolucionSesionOutputPort.resolver(COOKIE)).thenReturn(new ResolucionSesion.SinSesion());

        // Act
        var resultado = useCase.ejecutar(credencial);

        // Assert
        assertThat(resultado).isInstanceOf(ResolucionSesionResult.SinSesion.class);
        verifyNoInteractions(usuarioIdPorCorreoFinder);
    }

    @Test
    void debeNormalizarElCorreoAntesDelFinder() {
        // Arrange
        var credencial = CredencialSesionDomain.crear(COOKIE);
        when(resolucionSesionOutputPort.resolver(COOKIE))
                .thenReturn(new ResolucionSesion.Resuelta("sujeto-pdp", "Ana", "  Ana@UCO.edu.co ", "tenant-1"));
        when(usuarioIdPorCorreoFinder.obtener("ana@uco.edu.co")).thenReturn(UtilUUID.generarNuevoUUID());

        // Act
        useCase.ejecutar(credencial);

        // Assert
        verify(usuarioIdPorCorreoFinder).obtener("ana@uco.edu.co");
    }

    @Test
    void debePropagarExcepcion_cuandoPuertoFalla() {
        // Arrange
        var credencial = CredencialSesionDomain.crear(COOKIE);
        when(resolucionSesionOutputPort.resolver(COOKIE)).thenThrow(new IllegalStateException("pdp caido"));

        // Act / Assert
        assertThatThrownBy(() -> useCase.ejecutar(credencial))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("pdp caido");
    }
}
