package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.usuarios.application.coordinador.command.usecase.AgregarCoordinadorUseCase;
import com.arquisoft.usuarios.application.estudiante.command.usecase.AgregarEstudianteUseCase;
import com.arquisoft.usuarios.application.usuario.command.finder.ContactoUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailIdentidadExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.EmailUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.IdentificadorUsuarioExisteFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;
import com.arquisoft.usuarios.application.usuario.command.validator.RegistrarUsuarioValidator;
import com.arquisoft.usuarios.domain.usuario.RegistroUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;
    @Mock
    private ProveedorIdentidadOutputPort proveedorIdentidadOutputPort;
    @Mock
    private IdentificadorUsuarioExisteFinder identificadorUsuarioExisteFinder;
    @Mock
    private EmailUsuarioExisteFinder emailUsuarioExisteFinder;
    @Mock
    private EmailIdentidadExisteFinder emailIdentidadExisteFinder;
    @Mock
    private ContactoUsuarioExisteFinder contactoUsuarioExisteFinder;
    @Mock
    private RegistrarUsuarioValidator registrarUsuarioValidator;
    @Mock
    private AgregarEstudianteUseCase agregarEstudianteUseCase;
    @Mock
    private AgregarCoordinadorUseCase agregarCoordinadorUseCase;
    @Mock
    private AppLogger logger;

    private RegistrarUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarUsuarioUseCaseImpl(
                usuarioOutputPort, proveedorIdentidadOutputPort, identificadorUsuarioExisteFinder,
                emailUsuarioExisteFinder, emailIdentidadExisteFinder, contactoUsuarioExisteFinder,
                registrarUsuarioValidator, agregarEstudianteUseCase, agregarCoordinadorUseCase, logger);
        when(identificadorUsuarioExisteFinder.obtener(any())).thenReturn(false);
        when(emailUsuarioExisteFinder.obtener(any())).thenReturn(false);
        when(emailIdentidadExisteFinder.obtener(any())).thenReturn(false);
        when(contactoUsuarioExisteFinder.obtener(any())).thenReturn(false);
    }

    private RegistroUsuarioDomain registro(List<String> roles) {
        return RegistroUsuarioDomain.crear(
                "usr001", "Ana Pérez", "ana@uco.edu.co", "573001112233", "Ana", "Pérez", roles);
    }

    @Test
    void debeRegistrarUsuario_cuandoFlujoFelizSinRoles() {
        // Arrange
        var registro = registro(List.of());
        var identidadId = UUID.randomUUID();
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(identidadId);

        // Act
        var resultado = useCase.ejecutar(registro);

        // Assert
        assertThat(resultado).isEqualTo(identidadId);
        verify(usuarioOutputPort, times(1)).guardar(any());
        // Presupuesto de I/O: cada Finder se consulta exactamente una vez
        verify(identificadorUsuarioExisteFinder, times(1)).obtener("usr001");
        verify(emailUsuarioExisteFinder, times(1)).obtener("ana@uco.edu.co");
        verify(emailIdentidadExisteFinder, times(1)).obtener("ana@uco.edu.co");
        verify(contactoUsuarioExisteFinder, times(1)).obtener("573001112233");

        var captor = ArgumentCaptor.forClass(RegistroIdentidadEntity.class);
        verify(proveedorIdentidadOutputPort, times(1)).registrar(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().nombres()).isEqualTo("Ana");
        assertThat(captor.getValue().apellidos()).isEqualTo("Pérez");
        assertThat(captor.getValue().realmRoles()).isEmpty();
    }

    @Test
    void debeEnviarLosRoles_cuandoElRegistroLosTrae() {
        // Arrange
        var registro = registro(List.of("estudiante", "asesor"));
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(UUID.randomUUID());

        // Act
        useCase.ejecutar(registro);

        // Assert
        var captor = ArgumentCaptor.forClass(RegistroIdentidadEntity.class);
        verify(proveedorIdentidadOutputPort).registrar(captor.capture());
        assertThat(captor.getValue().realmRoles()).containsExactly("estudiante", "asesor");
    }

    @Test
    void debeSeguirElOrden_validarLuegoRegistrarIdpLuegoGuardar() {
        // Arrange
        var registro = registro(List.of());
        var identidadId = UUID.randomUUID();
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(identidadId);

        // Act
        useCase.ejecutar(registro);

        // Assert
        InOrder orden = inOrder(registrarUsuarioValidator, proveedorIdentidadOutputPort, usuarioOutputPort);
        orden.verify(registrarUsuarioValidator).validar(registro, false, false, false);
        orden.verify(proveedorIdentidadOutputPort).registrar(any());
        orden.verify(usuarioOutputPort).guardar(any());
    }

    @Test
    void noDebeRegistrarNiGuardar_cuandoElValidatorLanza() {
        // Arrange
        var registro = registro(List.of());
        doThrow(new UsuarioEmailDuplicadoException(registro.getEmail()))
                .when(registrarUsuarioValidator).validar(any(), anyBoolean(), anyBoolean(), anyBoolean());

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(UsuarioEmailDuplicadoException.class);
        verify(proveedorIdentidadOutputPort, never()).registrar(any());
        verify(usuarioOutputPort, never()).guardar(any());
    }

    @Test
    void debeAgregarEstudiante_cuandoRolesContieneEstudiante() {
        // Arrange
        var registro = registro(List.of("estudiante"));
        var identidadId = UUID.randomUUID();
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(identidadId);

        // Act
        useCase.ejecutar(registro);

        // Assert
        InOrder orden = inOrder(usuarioOutputPort, agregarEstudianteUseCase);
        orden.verify(usuarioOutputPort).guardar(any());
        orden.verify(agregarEstudianteUseCase).ejecutar(any());
    }

    @Test
    void noDebeAgregarEstudiante_cuandoRolesNoContieneEstudiante() {
        // Arrange
        var registro = registro(List.of("asesor"));
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(UUID.randomUUID());

        // Act
        useCase.ejecutar(registro);

        // Assert
        verify(agregarEstudianteUseCase, never()).ejecutar(any());
    }

    @Test
    void noDebeAgregarEstudiante_cuandoRolesEstaVacia() {
        // Arrange
        var registro = registro(List.of());
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(UUID.randomUUID());

        // Act
        useCase.ejecutar(registro);

        // Assert
        verify(agregarEstudianteUseCase, never()).ejecutar(any());
    }

    @Test
    void debeAgregarEstudiante_cuandoRolesContieneEstudianteYOtroRol() {
        // Arrange
        var registro = registro(List.of("estudiante", "asesor"));
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(UUID.randomUUID());

        // Act
        useCase.ejecutar(registro);

        // Assert
        verify(agregarEstudianteUseCase, times(1)).ejecutar(any());
    }

    @Test
    void noDebeAgregarEstudiante_cuandoElRegistroDeUsuarioFalla() {
        // Arrange
        var registro = registro(List.of("estudiante"));
        when(proveedorIdentidadOutputPort.registrar(any()))
                .thenThrow(new InfrastructureException("no disponible", "USUARIO_IDP_NO_DISPONIBLE"));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(InfrastructureException.class);
        verify(agregarEstudianteUseCase, never()).ejecutar(any());
    }

    @Test
    void debeAgregarCoordinador_cuandoRolesContieneCoordinador() {
        // Arrange
        var registro = registro(List.of("coordinador"));
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(UUID.randomUUID());

        // Act
        useCase.ejecutar(registro);

        // Assert
        InOrder orden = inOrder(usuarioOutputPort, agregarCoordinadorUseCase);
        orden.verify(usuarioOutputPort).guardar(any());
        orden.verify(agregarCoordinadorUseCase).ejecutar(any());
    }

    @Test
    void noDebeAgregarCoordinador_cuandoRolesNoContieneCoordinador() {
        // Arrange
        var registro = registro(List.of("asesor"));
        when(proveedorIdentidadOutputPort.registrar(any())).thenReturn(UUID.randomUUID());

        // Act
        useCase.ejecutar(registro);

        // Assert
        verify(agregarCoordinadorUseCase, never()).ejecutar(any());
    }

    @Test
    void debePropagarExcepcion_cuandoElProveedorDeIdentidadFalla() {
        // Arrange
        var registro = registro(List.of());
        when(proveedorIdentidadOutputPort.registrar(any()))
                .thenThrow(new InfrastructureException("no disponible", "USUARIO_IDP_NO_DISPONIBLE"));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(registro))
                .isInstanceOf(InfrastructureException.class);
        verify(usuarioOutputPort, never()).guardar(any());
    }
}
