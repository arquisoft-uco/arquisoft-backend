package com.arquisoft.usuarios.application.usuario.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.UsuariosRealmRoles;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.administrador.command.finder.AdministradorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesor.command.finder.AsesorPorUsuarioFinder;
import com.arquisoft.usuarios.application.asesorficha.command.finder.AsesorFichaPorUsuarioFinder;
import com.arquisoft.usuarios.application.coordinador.command.finder.CoordinadorPorUsuarioFinder;
import com.arquisoft.usuarios.application.estudiante.command.finder.EstudiantePorUsuarioFinder;
import com.arquisoft.usuarios.application.representantecomite.command.finder.RepresentanteComitePorUsuarioFinder;
import com.arquisoft.usuarios.application.usuario.command.finder.UsuarioPorIdFinder;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import com.arquisoft.usuarios.application.usuario.command.usecase.CambiarEstadoUsuarioUseCase;
import com.arquisoft.usuarios.application.usuario.command.validator.EliminarUsuarioValidator;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.EliminacionUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioRolesVigentesException;
import com.arquisoft.usuarios.domain.usuario.model.RolesUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
class EliminarUsuarioUseCaseImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;
    @Mock
    private UsuarioPorIdFinder usuarioPorIdFinder;
    @Mock
    private EstudiantePorUsuarioFinder estudiantePorUsuarioFinder;
    @Mock
    private AsesorPorUsuarioFinder asesorPorUsuarioFinder;
    @Mock
    private AsesorFichaPorUsuarioFinder asesorFichaPorUsuarioFinder;
    @Mock
    private CoordinadorPorUsuarioFinder coordinadorPorUsuarioFinder;
    @Mock
    private RepresentanteComitePorUsuarioFinder representanteComitePorUsuarioFinder;
    @Mock
    private AdministradorPorUsuarioFinder administradorPorUsuarioFinder;
    @Mock
    private EliminarUsuarioValidator eliminarUsuarioValidator;
    @Mock
    private CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
    @Mock
    private AppLogger logger;

    private EliminarUsuarioUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new EliminarUsuarioUseCaseImpl(usuarioOutputPort, usuarioPorIdFinder, estudiantePorUsuarioFinder,
                asesorPorUsuarioFinder, asesorFichaPorUsuarioFinder, coordinadorPorUsuarioFinder, representanteComitePorUsuarioFinder,
                administradorPorUsuarioFinder, eliminarUsuarioValidator, cambiarEstadoUsuarioUseCase, logger);
    }

    private static UsuarioDomain usuario(UUID id, EstadoUsuario estado) {
        return UsuarioDomain.reconstruir(id, "usr001", "Ana Perez", "ana@uco.edu.co", "573001112233",
                estado, UtilFecha.VACIO);
    }

    private void stubRolesAusentes(UUID id) {
        when(estudiantePorUsuarioFinder.obtener(id)).thenReturn(EstudianteDomain.VACIO);
        when(asesorPorUsuarioFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);
        when(asesorFichaPorUsuarioFinder.obtener(id)).thenReturn(AsesorFichaDomain.VACIO);
        when(coordinadorPorUsuarioFinder.obtener(id)).thenReturn(CoordinadorDomain.VACIO);
        when(representanteComitePorUsuarioFinder.obtener(id)).thenReturn(RepresentanteComiteDomain.VACIO);
        when(administradorPorUsuarioFinder.obtener(id)).thenReturn(AdministradorDomain.VACIO);
    }

    @Test
    void debeEliminarEInactivar_cuandoElUsuarioEstaActivo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = usuario(id, EstadoUsuario.ACTIVO);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(encontrado);
        stubRolesAusentes(id);

        // Act
        useCase.ejecutar(EliminacionUsuarioDomain.crear(id));

        // Assert
        verify(usuarioPorIdFinder, times(1)).obtener(id);
        verify(estudiantePorUsuarioFinder, times(1)).obtener(id);
        verify(asesorPorUsuarioFinder, times(1)).obtener(id);
        verify(asesorFichaPorUsuarioFinder, times(1)).obtener(id);
        verify(coordinadorPorUsuarioFinder, times(1)).obtener(id);
        verify(representanteComitePorUsuarioFinder, times(1)).obtener(id);
        verify(administradorPorUsuarioFinder, times(1)).obtener(id);

        var orden = inOrder(eliminarUsuarioValidator, usuarioOutputPort, cambiarEstadoUsuarioUseCase);
        var captorInstante = ArgumentCaptor.forClass(Instant.class);
        var captorCambio = ArgumentCaptor.forClass(CambioEstadoUsuarioDomain.class);
        orden.verify(eliminarUsuarioValidator).validar(id, encontrado, new RolesUsuario(id, EstudianteDomain.VACIO,
                AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO, RepresentanteComiteDomain.VACIO,
                AdministradorDomain.VACIO));
        orden.verify(usuarioOutputPort).eliminarLogica(eq(id), captorInstante.capture());
        orden.verify(cambiarEstadoUsuarioUseCase).ejecutar(captorCambio.capture());

        assertThat(captorInstante.getValue()).isNotEqualTo(UtilFecha.VACIO);
        assertThat(captorCambio.getValue().getUsuario()).isEqualTo(id);
        assertThat(captorCambio.getValue().getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
        verify(usuarioOutputPort, never()).actualizar(any());
    }

    @Test
    void debeSoloEliminar_cuandoElUsuarioYaEstaInactivo() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(usuarioPorIdFinder.obtener(id)).thenReturn(usuario(id, EstadoUsuario.INACTIVO));
        stubRolesAusentes(id);

        // Act
        useCase.ejecutar(EliminacionUsuarioDomain.crear(id));

        // Assert
        verify(usuarioOutputPort, times(1)).eliminarLogica(eq(id), any());
        verify(usuarioOutputPort, never()).cambiarEstado(any(), any(), any());
        verifyNoInteractions(cambiarEstadoUsuarioUseCase);
    }

    @Test
    void noDebePersistirNiInactivar_cuandoElValidatorLanza() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = usuario(id, EstadoUsuario.ACTIVO);
        var estudiante = EstudianteDomain.crear(id);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(encontrado);
        when(estudiantePorUsuarioFinder.obtener(id)).thenReturn(estudiante);
        when(asesorPorUsuarioFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);
        when(asesorFichaPorUsuarioFinder.obtener(id)).thenReturn(AsesorFichaDomain.VACIO);
        when(coordinadorPorUsuarioFinder.obtener(id)).thenReturn(CoordinadorDomain.VACIO);
        when(representanteComitePorUsuarioFinder.obtener(id)).thenReturn(RepresentanteComiteDomain.VACIO);
        when(administradorPorUsuarioFinder.obtener(id)).thenReturn(AdministradorDomain.VACIO);
        var rechazo = new UsuarioRolesVigentesException(id, "estudiante");
        doThrow(rechazo).when(eliminarUsuarioValidator).validar(id, encontrado, new RolesUsuario(id, estudiante,
                AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO, RepresentanteComiteDomain.VACIO,
                AdministradorDomain.VACIO));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(EliminacionUsuarioDomain.crear(id))).isSameAs(rechazo);
        verify(usuarioOutputPort, never()).eliminarLogica(any(), any());
        verifyNoInteractions(cambiarEstadoUsuarioUseCase);
    }

    @Test
    void debeEntregarAdministradorAlValidatorYNoEliminar_cuandoElAdministradorSigueVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = usuario(id, EstadoUsuario.ACTIVO);
        var administrador = AdministradorDomain.crear(id);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(encontrado);
        when(estudiantePorUsuarioFinder.obtener(id)).thenReturn(EstudianteDomain.VACIO);
        when(asesorPorUsuarioFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);
        when(asesorFichaPorUsuarioFinder.obtener(id)).thenReturn(AsesorFichaDomain.VACIO);
        when(coordinadorPorUsuarioFinder.obtener(id)).thenReturn(CoordinadorDomain.VACIO);
        when(representanteComitePorUsuarioFinder.obtener(id)).thenReturn(RepresentanteComiteDomain.VACIO);
        when(administradorPorUsuarioFinder.obtener(id)).thenReturn(administrador);
        var rechazo = new UsuarioRolesVigentesException(id, UsuariosRealmRoles.ADMINISTRADOR);
        doThrow(rechazo).when(eliminarUsuarioValidator).validar(id, encontrado, new RolesUsuario(id,
                EstudianteDomain.VACIO, AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO,
                RepresentanteComiteDomain.VACIO, administrador));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(EliminacionUsuarioDomain.crear(id))).isSameAs(rechazo);
        verify(usuarioOutputPort, never()).eliminarLogica(any(), any());
        verifyNoInteractions(cambiarEstadoUsuarioUseCase);
    }

    @Test
    void debeEntregarRepresentanteComiteAlValidatorYNoEliminar_cuandoElRepresentanteSigueVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var encontrado = usuario(id, EstadoUsuario.ACTIVO);
        var representanteComite = RepresentanteComiteDomain.crear(id);
        when(usuarioPorIdFinder.obtener(id)).thenReturn(encontrado);
        when(estudiantePorUsuarioFinder.obtener(id)).thenReturn(EstudianteDomain.VACIO);
        when(asesorPorUsuarioFinder.obtener(id)).thenReturn(AsesorDomain.VACIO);
        when(asesorFichaPorUsuarioFinder.obtener(id)).thenReturn(AsesorFichaDomain.VACIO);
        when(coordinadorPorUsuarioFinder.obtener(id)).thenReturn(CoordinadorDomain.VACIO);
        when(representanteComitePorUsuarioFinder.obtener(id)).thenReturn(representanteComite);
        when(administradorPorUsuarioFinder.obtener(id)).thenReturn(AdministradorDomain.VACIO);
        var rechazo = new UsuarioRolesVigentesException(id, UsuariosRealmRoles.REPRESENTANTE_COMITE);
        doThrow(rechazo).when(eliminarUsuarioValidator).validar(id, encontrado, new RolesUsuario(id,
                EstudianteDomain.VACIO, AsesorDomain.VACIO, AsesorFichaDomain.VACIO, CoordinadorDomain.VACIO,
                representanteComite, AdministradorDomain.VACIO));

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(EliminacionUsuarioDomain.crear(id))).isSameAs(rechazo);
        verify(usuarioOutputPort, never()).eliminarLogica(any(), any());
        verifyNoInteractions(cambiarEstadoUsuarioUseCase);
    }
}
