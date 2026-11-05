package com.arquisoft.usuarios.application.usuario.query.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.ConsultarUsuarioPorCorreoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.usuario.query.criteria.UsuarioPorCorreoCriteria;
import com.arquisoft.usuarios.application.usuario.query.finder.UsuarioPorCorreoQueryFinder;
import com.arquisoft.usuarios.application.usuario.query.validator.ConsultarUsuarioPorCorreoValidator;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarUsuarioPorCorreoUseCaseImplTest {

    private static final String CORREO = "ana@uco.edu.co";

    @Mock
    private UsuarioPorCorreoQueryFinder usuarioPorCorreoQueryFinder;

    @Mock
    private ConsultarUsuarioPorCorreoValidator consultarUsuarioPorCorreoValidator;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarUsuarioPorCorreoUseCaseImpl useCase;

    @Test
    void debeDevolverElId_cuandoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var encontrado = UsuarioDomain.reconstruir(usuario, "1001", "Ana Ramirez", CORREO, "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
        when(usuarioPorCorreoQueryFinder.obtener(CORREO)).thenReturn(encontrado);

        // Act
        var resultado = useCase.ejecutar(new UsuarioPorCorreoCriteria(CORREO));

        // Assert
        assertThat(resultado.id()).isEqualTo(usuario);
        var orden = inOrder(logger, usuarioPorCorreoQueryFinder, consultarUsuarioPorCorreoValidator);
        orden.verify(logger).debug(ConsultarUsuarioPorCorreoKey.LOG_CONSULTANDO, "a***@uco.edu.co");
        orden.verify(usuarioPorCorreoQueryFinder).obtener(CORREO);
        orden.verify(consultarUsuarioPorCorreoValidator).validar(encontrado);
        orden.verify(logger).debug(ConsultarUsuarioPorCorreoKey.LOG_CONSULTA_COMPLETADA);
    }

    @Test
    void debeRechazarConValidatorMockeado_cuandoNoExisteOEstaDeBaja() {
        // Arrange
        when(usuarioPorCorreoQueryFinder.obtener(CORREO)).thenReturn(UsuarioDomain.VACIO);
        doThrow(new UsuarioNoEncontradoException(UsuarioDomain.VACIO.getId()))
                .when(consultarUsuarioPorCorreoValidator).validar(UsuarioDomain.VACIO);
        var criteria = new UsuarioPorCorreoCriteria(CORREO);

        // Act / Assert
        assertThatThrownBy(() -> useCase.ejecutar(criteria)).isInstanceOf(UsuarioNoEncontradoException.class);
        verify(logger, never()).debug(ConsultarUsuarioPorCorreoKey.LOG_CONSULTA_COMPLETADA);
        verify(logger).debug(any(ConsultarUsuarioPorCorreoKey.class), any(Object[].class));
    }
}
