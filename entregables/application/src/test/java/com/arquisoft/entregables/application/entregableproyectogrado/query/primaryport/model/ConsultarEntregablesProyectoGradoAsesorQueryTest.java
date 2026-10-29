package com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model;

import com.arquisoft.shared.message.constant.EntregablesFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEntregablesProyectoGradoAsesorQueryTest {

    @Test
    void debeCrear_cuandoLosDatosSonValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarEntregablesProyectoGradoAsesorQuery.crear(asesor, criterio);

        // Assert
        assertThat(query.asesor()).isEqualTo(asesor);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoFaltaElAsesor() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEntregablesProyectoGradoAsesorQuery.crear(null, criterio))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var resultado = ex.getValidationResult();
                    assertThat(resultado.tieneErroresDeCampo(EntregablesFields.EntregableProyectoGrado.ASESOR)).isTrue();
                });
    }
}
