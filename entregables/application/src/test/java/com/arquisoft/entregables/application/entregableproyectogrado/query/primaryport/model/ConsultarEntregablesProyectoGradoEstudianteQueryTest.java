package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEntregablesProyectoGradoEstudianteQueryTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var estudiante = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarEntregablesProyectoGradoEstudianteQuery.crear(estudiante, proyecto, criterio);

        // Assert
        assertThat(query.estudiante()).isEqualTo(estudiante);
        assertThat(query.proyectoGrado()).isEqualTo(proyecto);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoFaltanEstudianteYProyectoGrado() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEntregablesProyectoGradoEstudianteQuery.crear(null, null, criterio))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.EntregableProyectoGrado.ESTUDIANTE)).isTrue();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.EntregableProyectoGrado.PROYECTO_GRADO)).isTrue();
                });
    }
}
