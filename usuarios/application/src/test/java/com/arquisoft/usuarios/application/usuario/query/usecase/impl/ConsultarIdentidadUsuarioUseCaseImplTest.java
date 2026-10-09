package com.arquisoft.usuarios.application.usuario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.usuarios.ConsultarIdentidadUsuarioKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.finder.UsuarioPorIdQueryFinder;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.IdentidadUsuarioQueryOutputPort;
import com.arquisoft.usuarios.application.usuario.query.validator.ConsultarIdentidadUsuarioValidator;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class ConsultarIdentidadUsuarioUseCaseImplTest {

    @Mock
    private UsuarioPorIdQueryFinder usuarioPorIdQueryFinder;

    @Mock
    private ConsultarIdentidadUsuarioValidator consultarIdentidadUsuarioValidator;

    @Mock
    private IdentidadUsuarioQueryOutputPort identidadUsuarioQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarIdentidadUsuarioUseCaseImpl useCase;

    @Test
    void debeConsultarIdentidad_cuandoElUsuarioExisteYNoEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var criteria = new IdentidadUsuarioCriteria(usuario);
        var encontrado = UsuarioDomain.reconstruir(usuario, "1001", "Ana Ramirez", "ana@uco.edu.co",
                "573001112233", EstadoUsuario.ACTIVO, UtilFecha.VACIO);
        var esperado = new IdentidadUsuarioReadModel("Ana María", "Ramírez Díaz");
        when(usuarioPorIdQueryFinder.obtener(usuario)).thenReturn(encontrado);
        when(identidadUsuarioQueryOutputPort.consultar(criteria)).thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        var orden = inOrder(logger, usuarioPorIdQueryFinder, consultarIdentidadUsuarioValidator,
                identidadUsuarioQueryOutputPort);
        orden.verify(logger).debug(ConsultarIdentidadUsuarioKey.LOG_CONSULTANDO, usuario);
        orden.verify(usuarioPorIdQueryFinder).obtener(usuario);
        orden.verify(consultarIdentidadUsuarioValidator).validar(usuario, encontrado);
        orden.verify(identidadUsuarioQueryOutputPort).consultar(criteria);
        orden.verify(logger).debug(ConsultarIdentidadUsuarioKey.LOG_CONSULTA_COMPLETADA, usuario);
        verify(usuarioPorIdQueryFinder, times(1)).obtener(any());
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }

    @Test
    void noDebeLeerIdentidad_cuandoElValidatorRechaza() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var criteria = new IdentidadUsuarioCriteria(usuario);
        when(usuarioPorIdQueryFinder.obtener(usuario)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new UsuarioNoEncontradoException(usuario))
                .when(consultarIdentidadUsuarioValidator).validar(usuario, UsuarioDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria))
                .isInstanceOf(UsuarioNoEncontradoException.class);
        verify(identidadUsuarioQueryOutputPort, never()).consultar(any());
        verify(logger, never()).debug(eq(ConsultarIdentidadUsuarioKey.LOG_CONSULTA_COMPLETADA), any());
    }
}
