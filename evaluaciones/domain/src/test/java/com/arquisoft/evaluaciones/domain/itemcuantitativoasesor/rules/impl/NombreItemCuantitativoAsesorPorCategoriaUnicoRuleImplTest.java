package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.NombreItemCuantitativoAsesorDuplicadoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.DisponibilidadNombreItemCuantitativoAsesor;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NombreItemCuantitativoAsesorPorCategoriaUnicoRuleImplTest {

    private final NombreItemCuantitativoAsesorPorCategoriaUnicoRuleImpl rule =
            new NombreItemCuantitativoAsesorPorCategoriaUnicoRuleImpl();

    @Test
    void debeLanzarConNombreEnElMensaje_cuandoNombreYaExiste() {
        // Arrange
        var categoria = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadNombreItemCuantitativoAsesor("Avance", categoria, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOfSatisfying(NombreItemCuantitativoAsesorDuplicadoException.class, exception -> {
                    assertThat(exception.getCodigoError())
                            .isEqualTo(EvaluacionesCodes.ItemCuantitativoAsesor.NOMBRE_CATEGORIA_DUPLICADO);
                    assertThat(exception.getError().getMessage())
                            .contains("Avance")
                            .doesNotContain(categoria.toString());
                    assertThat(exception.getMessage()).contains(categoria.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoNombreNoExiste() {
        // Arrange
        var disponibilidad =
                new DisponibilidadNombreItemCuantitativoAsesor("Avance", UtilUUID.generarNuevoUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
