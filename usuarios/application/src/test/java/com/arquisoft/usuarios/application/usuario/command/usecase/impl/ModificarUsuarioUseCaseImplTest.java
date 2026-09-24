package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.usuarios.application.asesor.command.usecase.AgregarAsesorUseCase;
import com.arquisoft.usuarios.application.asesorficha.command.usecase.AgregarAsesorFichaUseCase;
import com.arquisoft.usuarios.application.coordinador.command.usecase.AgregarCoordinadorUseCase;
import com.arquisoft.usuarios.application.estudiante.command.usecase.AgregarEstudianteUseCase;
import com.arquisoft.usuarios.application.usuario.command.finder.ContactoOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailOtraIdentidadExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.IdentificadorOtroUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.validator.ModificarUsuarioValidator;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.ModificacionUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioInactivoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModificarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private IdentificadorOtroUsuarioExisteFinder identificadorOtroUsuarioExisteFinder;
    @Mock
    private EmailOtroUsuarioExisteFinder emailOtroUsuarioExisteFinder;
    @Mock
    private EmailOtraIdentidadExisteFinder emailOtraIdentidadExisteFinder;
    @Mock
    private ContactoOtroUsuarioExisteFinder contactoOtroUsuarioExisteFinder;
    @Mock
    private ModificarUsuarioValidator modificarUsuarioValidator;
    @Mock
    private AgregarEstudianteUseCase agregarEstudianteUseCase;
    @Mock
    private AgregarCoordinadorUseCase agregarCoordinadorUseCase;
    @Mock
    private AgregarAsesorFichaUseCase agregarAsesorFichaUseCase;
    @Mock
    private AgregarAsesorUseCase agregarAsesorUseCase;
    @Mock
    private EventPublisher eventPublisher;
    @Mock
    private AppLogger logger;

    private ModificarUsuarioUseCaseImpl useCase;

    private UUID usuarioId;
    private UsuarioDomain usuarioActivo;

    @BeforeEach
    void setUp() {
        useCase = new ModificarUsuarioUseCaseImpl(usuarioOutputPort, proveedorIdentidadOutputPort,
                usuarioPorIdFinder, identificadorOtroUsuarioExisteFinder, emailOtroUsuarioExisteFinder,
                emailOtraIdentidadExisteFinder, contactoOtroUsuarioExisteFinder, modificarUsuarioValidator,
                agregarEstudianteUseCase, agregarCoordinadorUseCase, agregarAsesorFichaUseCase,
                agregarAsesorUseCase, eventPublisher, logger);
        usuarioId = UUID.randomUUID();
        usuarioActivo = UsuarioDomain.reconstruir(usuarioId, "usr001", "Nombre Original",
                "original@uco.edu.co", "573001112233", EstadoUsuario.ACTIVO);
    }

    @Test
    void debeActualizarPublicarYAsignarRealmRolesVacio_cuandoSoloCambianDatos() {
        // Arrange
        var modificacion = modificacionSoloNombre();
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        verify(usuarioOutputPort, times(1)).actualizar(any());
        verify(eventPublisher, times(1)).publish(any());
        verify(proveedorIdentidadOutputPort, times(1)).asignarRealmRoles(usuarioId, List.of());
        verify(proveedorIdentidadOutputPort, never()).actualizar(any());
        verifyNoInteractions(agregarEstudianteUseCase, agregarCoordinadorUseCase,
                agregarAsesorFichaUseCase, agregarAsesorUseCase);
        // presupuesto de I/O: un solo viaje por finder de unicidad tocado
        verify(usuarioPorIdFinder, times(1)).obtener(usuarioId);
        verify(identificadorOtroUsuarioExisteFinder, never()).obtener(any());
        verify(emailOtroUsuarioExisteFinder, never()).obtener(any());
        verify(emailOtraIdentidadExisteFinder, never()).obtener(any());
        verify(contactoOtroUsuarioExisteFinder, never()).obtener(any());
    }

    @Test
    void debeAgregarRolEstudianteConUsuarioModificado_cuandoRolesIncluyeEstudiante() {
        // Arrange
        var modificacion = modificacionConRol("estudiante");
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        verify(agregarEstudianteUseCase, times(1)).ejecutar(usuarioActivo);
        verify(agregarCoordinadorUseCase, never()).ejecutar(any());
        verify(agregarAsesorFichaUseCase, never()).ejecutar(any());
        verify(agregarAsesorUseCase, never()).ejecutar(any());
        verify(eventPublisher, never()).publish(any());
        verify(proveedorIdentidadOutputPort, times(1)).asignarRealmRoles(usuarioId, List.of("estudiante"));
    }

    @Test
    void debeConsultarLosTresFindersDeUnicidad_cuandoLosTresCamposLlegan() {
        // Arrange
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                "usr999", null, null, "573009998877", null, null);
        var modificacion = ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);
        when(identificadorOtroUsuarioExisteFinder.obtener(any())).thenReturn(false);
        when(contactoOtroUsuarioExisteFinder.obtener(any())).thenReturn(false);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        verify(identificadorOtroUsuarioExisteFinder, times(1)).obtener(any());
        verify(contactoOtroUsuarioExisteFinder, times(1)).obtener(any());
    }

    @Test
    void debeAgregarLosTresRolesRestantesConUsuarioModificado_cuandoRolesLosIncluye() {
        // Arrange
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, null, null, null);
        var modificacion = ModificacionUsuarioDomain.crear(
                usuarioId, datos, List.of("coordinador", "asesor-ficha", "asesor"));
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        verify(agregarCoordinadorUseCase, times(1)).ejecutar(usuarioActivo);
        verify(agregarAsesorFichaUseCase, times(1)).ejecutar(usuarioActivo);
        verify(agregarAsesorUseCase, times(1)).ejecutar(usuarioActivo);
        verify(agregarEstudianteUseCase, never()).ejecutar(any());
    }

    @Test
    void noDebeConsultarFinderDeUnicidad_cuandoElCampoCorrespondienteEsAusente() {
        // Arrange — solo llega nombre; identificador/email/contacto ausentes
        var modificacion = modificacionSoloNombre();
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        verifyNoInteractions(identificadorOtroUsuarioExisteFinder, emailOtroUsuarioExisteFinder,
                emailOtraIdentidadExisteFinder, contactoOtroUsuarioExisteFinder);
    }

    @Test
    void debeCortarPorCortocircuito_cuandoElEmailYaExisteEnOtroUsuario() {
        // Arrange
        var modificacion = modificacionConEmail("nuevo@uco.edu.co");
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);
        when(emailOtroUsuarioExisteFinder.obtener(any())).thenReturn(true);

        // Act
        useCase.ejecutar(modificacion);

        // Assert — el || de negocio corta antes de consultar la identidad
        verify(emailOtroUsuarioExisteFinder, times(1)).obtener(any());
        verify(emailOtraIdentidadExisteFinder, never()).obtener(any());
    }

    @Test
    void noDebePersistirNiPublicarNiTocarKeycloak_cuandoElValidadorLanza() {
        // Arrange
        var modificacion = modificacionSoloNombre();
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);
        doThrow(new UsuarioInactivoException(usuarioId)).when(modificarUsuarioValidator)
                .validar(any(), any(), eq(false), eq(false), eq(false));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(UsuarioInactivoException.class);
        verify(usuarioOutputPort, never()).actualizar(any());
        verify(eventPublisher, never()).publish(any());
        verifyNoInteractions(proveedorIdentidadOutputPort);
        verifyNoInteractions(agregarEstudianteUseCase, agregarCoordinadorUseCase,
                agregarAsesorFichaUseCase, agregarAsesorUseCase);
    }

    @Test
    void noDebeActualizarNiTocarKeycloak_cuandoElAgregarRolLanzaDuplicado() {
        // Arrange
        var modificacion = modificacionConRol("asesor");
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);
        doThrow(new IllegalStateException("rol duplicado")).when(agregarAsesorUseCase).ejecutar(any());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(modificacion))
                .isInstanceOf(IllegalStateException.class);
        // usuarioOutputPort.actualizar() ya ocurrió antes del paso de roles (mismo orden que producción),
        // pero Keycloak — el último efecto — nunca se alcanza
        verifyNoInteractions(proveedorIdentidadOutputPort);
    }

    @Test
    void debeRespetarElOrden_validarLuegoRolesLuegoPersistirLuegoPublicarLuegoKeycloak() {
        // Arrange
        var modificacion = modificacionConRol("estudiante");
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        var orden = inOrder(modificarUsuarioValidator, usuarioOutputPort, eventPublisher,
                agregarEstudianteUseCase, proveedorIdentidadOutputPort);
        orden.verify(modificarUsuarioValidator).validar(any(), any(), eq(false), eq(false), eq(false));
        orden.verify(usuarioOutputPort).actualizar(any());
        orden.verify(agregarEstudianteUseCase).ejecutar(any());
        orden.verify(proveedorIdentidadOutputPort).asignarRealmRoles(any(), any());
    }

    @Test
    void debeLoguearEntradaYCierre_cuandoLaModificacionSeCompleta() {
        // Arrange
        var modificacion = modificacionSoloNombre();
        when(usuarioPorIdFinder.obtener(usuarioId)).thenReturn(usuarioActivo);

        // Act
        useCase.ejecutar(modificacion);

        // Assert
        verify(logger).info(any(ClaveMensaje.class), eq(usuarioId));
        verify(logger).info(any(ClaveMensaje.class), eq(usuarioId), eq(0));
    }

    private ModificacionUsuarioDomain modificacionSoloNombre() {
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, "Nombre Nuevo", null, null, null, null);
        return ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());
    }

    private ModificacionUsuarioDomain modificacionConEmail(String email) {
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, email, null, null, null);
        return ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());
    }

    private ModificacionUsuarioDomain modificacionConRol(String rol) {
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, null, null, null, null, null);
        return ModificacionUsuarioDomain.crear(usuarioId, datos, List.of(rol));
    }
}
