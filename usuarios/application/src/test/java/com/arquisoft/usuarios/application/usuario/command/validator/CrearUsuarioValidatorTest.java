package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.application.usuario.command.validator.impl.CrearUsuarioValidatorImpl;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.model.UsuarioRole;
import com.arquisoft.usuarios.domain.usuario.rules.UsuarioEmailUnicoRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioValidatorTest {

    @Mock
    private UsuarioEmailUnicoRule usuarioEmailUnicoRule;

    @InjectMocks
    private CrearUsuarioValidatorImpl crearUsuarioValidator;

    @Test
    void debeValidarUnicidadDelEmailNormalizado_cuandoUsuarioEsValido() {
        // Arrange
        UsuarioDomain usuario = UsuarioDomain.crear("  Nuevo@Example.COM  ", UsuarioRole.ESTUDIANTE);

        // Act
        crearUsuarioValidator.validar(usuario);

        // Assert — la regla recibe el email ya normalizado por el agregado
        verify(usuarioEmailUnicoRule).validar("nuevo@example.com");
    }

    @Test
    void debePropagarExcepcion_cuandoEmailYaExiste() {
        // Arrange
        UsuarioDomain usuario = UsuarioDomain.crear("repetido@example.com", UsuarioRole.ASESOR);
        doThrow(new UsuarioEmailDuplicadoException("repetido@example.com"))
                .when(usuarioEmailUnicoRule).validar("repetido@example.com");

        // Act / Assert
        assertThatThrownBy(() -> crearUsuarioValidator.validar(usuario))
                .isInstanceOf(UsuarioEmailDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoReglaNoLanzaExcepcion() {
        // Arrange
        UsuarioDomain usuario = UsuarioDomain.crear("ok@example.com", UsuarioRole.COORDINADOR);

        // Act / Assert
        assertThatCode(() -> crearUsuarioValidator.validar(usuario)).doesNotThrowAnyException();
    }
}
