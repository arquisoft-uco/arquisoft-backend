package com.arquisoft.usuarios.application;

import com.arquisoft.usuarios.application.usuario.command.interactor.impl.CrearUsuarioInteractorImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class InteractoresUsuariosTest {

    @DisplayName("El interactor declara la transacción con el manager de usuarios")
    @ParameterizedTest(name = "{0}")
    @ValueSource(classes = {
            CrearUsuarioInteractorImpl.class
    })
    void debeDeclararTransaccion_cuandoEsInteractorDeComando(Class<?> interactor) {
        // Arrange
        Method ejecutar = Arrays.stream(interactor.getDeclaredMethods())
                .filter(m -> "ejecutar".equals(m.getName()) && !m.isBridge())
                .findFirst()
                .orElseThrow();

        // Act
        Transactional transaccion = ejecutar.getAnnotation(Transactional.class);

        // Assert
        assertThat(transaccion)
                .as("%s debe delimitar la transacción de la operación", interactor.getSimpleName())
                .isNotNull();
        assertThat(transaccion.transactionManager()).isEqualTo("usuariosTransactionManager");
    }
}
