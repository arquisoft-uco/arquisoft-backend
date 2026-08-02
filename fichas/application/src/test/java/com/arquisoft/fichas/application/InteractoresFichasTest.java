package com.arquisoft.fichas.application;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.interactor.impl.AgregarEstadoEvaluacionFichaInteractorImpl;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.interactor.impl.AsignarEstudiantesFichaPerfilInteractorImpl;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.interactor.impl.RemoverEstudianteFichaPerfilInteractorImpl;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.interactor.impl.RegistrarEvaluacionFichaPerfilInteractorImpl;
import com.arquisoft.fichas.application.fichaperfil.command.interactor.impl.CambiarAsesorFichaInteractorImpl;
import com.arquisoft.fichas.application.fichaperfil.command.interactor.impl.ModificarFichaPerfilInteractorImpl;
import com.arquisoft.fichas.application.fichaperfil.command.interactor.impl.RegistrarFichaPerfilInteractorImpl;
import com.arquisoft.fichas.application.itemfichaperfil.command.interactor.impl.AgregarItemFichaPerfilInteractorImpl;
import com.arquisoft.fichas.application.itemfichaperfil.command.interactor.impl.ModificarItemFichaPerfilInteractorImpl;
import com.arquisoft.fichas.application.itemfichaperfil.command.interactor.impl.RemoverItemFichaPerfilInteractorImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class InteractoresFichasTest {

    @DisplayName("El interactor declara la transacción con el manager de fichas")
    @ParameterizedTest(name = "{0}")
    @ValueSource(classes = {
            RegistrarFichaPerfilInteractorImpl.class,
            ModificarFichaPerfilInteractorImpl.class,
            CambiarAsesorFichaInteractorImpl.class,
            AgregarItemFichaPerfilInteractorImpl.class,
            ModificarItemFichaPerfilInteractorImpl.class,
            RemoverItemFichaPerfilInteractorImpl.class,
            AsignarEstudiantesFichaPerfilInteractorImpl.class,
            RemoverEstudianteFichaPerfilInteractorImpl.class,
            RegistrarEvaluacionFichaPerfilInteractorImpl.class,
            AgregarEstadoEvaluacionFichaInteractorImpl.class
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
        assertThat(transaccion.transactionManager()).isEqualTo("fichasTransactionManager");
    }
}
