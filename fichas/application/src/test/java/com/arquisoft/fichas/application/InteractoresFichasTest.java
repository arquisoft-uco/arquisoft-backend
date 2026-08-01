package com.arquisoft.fichas.application;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.AgregarEstadoEvaluacionFichaInteractor;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.AsignarEstudiantesFichaPerfilInteractor;
import com.arquisoft.fichas.application.estudiantefichaperfil.command.RemoverEstudianteFichaPerfilInteractor;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.RegistrarEvaluacionFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.CambiarAsesorFichaInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.ModificarFichaPerfilInteractor;
import com.arquisoft.fichas.application.fichaperfil.command.RegistrarFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.AgregarItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.ModificarItemFichaPerfilInteractor;
import com.arquisoft.fichas.application.itemfichaperfil.command.RemoverItemFichaPerfilInteractor;
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
            RegistrarFichaPerfilInteractor.class,
            ModificarFichaPerfilInteractor.class,
            CambiarAsesorFichaInteractor.class,
            AgregarItemFichaPerfilInteractor.class,
            ModificarItemFichaPerfilInteractor.class,
            RemoverItemFichaPerfilInteractor.class,
            AsignarEstudiantesFichaPerfilInteractor.class,
            RemoverEstudianteFichaPerfilInteractor.class,
            RegistrarEvaluacionFichaPerfilInteractor.class,
            AgregarEstadoEvaluacionFichaInteractor.class
    })
    void debeDeclararTransaccion_cuandoEsInteractorDeComando(Class<?> interactor) {
        // Arrange
        Method ejecutar = Arrays.stream(interactor.getDeclaredMethods())
                .filter(m -> "ejecutar".equals(m.getName()))
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
