package com.arquisoft.artefactos.application.artefacto.query.primaryport.model;

import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.message.constant.ArtefactosFields;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarArtefactosSinAvanceQueryTest {

    private final ConsultaCriteriaQuery criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

    @Test
    void debeCrearQuery_cuandoDatosValidos() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarArtefactosSinAvanceQuery.crear(asesor, 30, criterio);

        // Assert
        assertThat(query.asesor()).isEqualTo(asesor);
        assertThat(query.diasSinAvance()).isEqualTo(30);
        assertThat(query.criterio()).isSameAs(criterio);
    }

    @Test
    void debeCrearQuery_cuandoDiasEsLimiteInferiorYSuperior() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var minimo = ConsultarArtefactosSinAvanceQuery.crear(asesor, 1, criterio);
        var maximo = ConsultarArtefactosSinAvanceQuery.crear(asesor, 365, criterio);

        // Assert
        assertThat(minimo.diasSinAvance()).isEqualTo(1);
        assertThat(maximo.diasSinAvance()).isEqualTo(365);
    }

    @Test
    void debeAcumularErrores_cuandoAsesorYDiasSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> ConsultarArtefactosSinAvanceQuery.crear(null, null, criterio))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                    var errores = ex.getValidationResult().getErrores();
                    assertThat(errores).hasSize(2);
                    assertThat(errores).extracting(e -> e.campo())
                            .containsExactlyInAnyOrder(ArtefactosFields.Artefacto.ASESOR,
                                    ArtefactosFields.Artefacto.DIAS_SIN_AVANCE);
                    assertThat(errores).extracting(e -> e.codigoError())
                            .containsExactlyInAnyOrder(ArtefactosCodes.Artefacto.ASESOR_REQUERIDO,
                                    ArtefactosCodes.Artefacto.DIAS_SIN_AVANCE_REQUERIDO);
                });
    }

    @Test
    void debeLanzarValidacionEntrada_cuandoDiasEstaFueraDeRango() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act & Assert
        for (var dias : List.of(0, -1, 366)) {
            assertThatThrownBy(() -> ConsultarArtefactosSinAvanceQuery.crear(asesor, dias, criterio))
                    .isInstanceOfSatisfying(ApplicationValidationException.class, ex -> {
                        var errores = ex.getValidationResult().getErrores();
                        assertThat(errores).hasSize(1);
                        assertThat(errores.get(0).campo()).isEqualTo(ArtefactosFields.Artefacto.DIAS_SIN_AVANCE);
                        assertThat(errores.get(0).codigoError())
                                .isEqualTo(ArtefactosCodes.Artefacto.DIAS_SIN_AVANCE_FUERA_DE_RANGO);
                    });
        }
    }
}
