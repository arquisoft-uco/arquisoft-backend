package com.arquisoft.usuarios.application.usuario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarMiPerfilKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.PerfilUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.finder.UsuarioPorIdQueryFinder;
import com.arquisoft.usuarios.application.usuario.query.readmodel.EstadoPerfilReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.PerfilUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.readmodel.RolPerfilReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.PerfilUsuarioQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.validator.ConsultarMiPerfilValidator;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarMiPerfilUseCaseImplTest {

    @Mock
    private UsuarioPorIdQueryFinder usuarioPorIdQueryFinder;

    @Mock
    private ConsultarMiPerfilValidator consultarMiPerfilValidator;

    @Mock
    private PerfilUsuarioQueryOutputPort perfilUsuarioQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarMiPerfilUseCaseImpl useCase;

    @Test
    void debeConsultarEnOrden_cuandoElUsuarioEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var criteria = new PerfilUsuarioCriteria(usuario);
        var encontrado = UsuarioDomain.reconstruir(usuario, "1001", "Ana Ramirez", "ana@uco.edu.co",
                "573001112233", EstadoUsuario.ACTIVO, UtilFecha.VACIO);
        var esperado = new PerfilUsuarioReadModel(usuario, "1001", "Ana Ramirez", "ana@uco.edu.co",
                "573001112233", new EstadoPerfilReadModel("ACTIVO", "Activo"),
                List.of(new RolPerfilReadModel("estudiante", "Estudiante"),
                        new RolPerfilReadModel("asesor", "Asesor")));
        when(usuarioPorIdQueryFinder.obtener(usuario)).thenReturn(encontrado);
        when(perfilUsuarioQueryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(logger, usuarioPorIdQueryFinder, consultarMiPerfilValidator,
                perfilUsuarioQueryOutputPort);
        orden.verify(logger).debug(ConsultarMiPerfilKey.LOG_CONSULTANDO, usuario);
        orden.verify(usuarioPorIdQueryFinder).obtener(usuario);
        orden.verify(consultarMiPerfilValidator).validar(usuario, encontrado);
        orden.verify(perfilUsuarioQueryOutputPort).consultar(criteria);
        orden.verify(logger).debug(ConsultarMiPerfilKey.LOG_CONSULTA_COMPLETADA, usuario, 2);
        verify(usuarioPorIdQueryFinder, times(1)).obtener(any());
        verify(perfilUsuarioQueryOutputPort, times(1)).consultar(any());
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void noDebeLeerElPerfil_cuandoElValidatorRechaza() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var criteria = new PerfilUsuarioCriteria(usuario);
        when(usuarioPorIdQueryFinder.obtener(usuario)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new UsuarioNoEncontradoException(usuario))
                .when(consultarMiPerfilValidator).validar(usuario, UsuarioDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(UsuarioNoEncontradoException.class);
        verify(perfilUsuarioQueryOutputPort, never()).consultar(any());
        verify(logger, never()).debug(eq(ConsultarMiPerfilKey.LOG_CONSULTA_COMPLETADA), any(), any());
    }
}
