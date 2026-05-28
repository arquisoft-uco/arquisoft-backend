package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FichaPerfilAggregateTest {

    @Test
    void debeReconstruirCorrectamente_cuandoRebuildEsInvocado() {
        UUID id = UUID.randomUUID();
        UUID asesorFichaId = UUID.randomUUID();

        FichaPerfilAggregate ficha = FichaPerfilAggregate.rebuild(id, "Proyecto de Grado", asesorFichaId);

        assertThat(ficha.getId()).isEqualTo(id);
        assertThat(ficha.getTituloProyecto()).isEqualTo("Proyecto de Grado");
        assertThat(ficha.getAsesorFichaId()).isEqualTo(asesorFichaId);
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloEsNuloOVacioEnBuild() {
        UUID asesorFichaId = UUID.randomUUID();

        assertThatThrownBy(() -> FichaPerfilAggregate.build(null, asesorFichaId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("nulo ni vacío");

        assertThatThrownBy(() -> FichaPerfilAggregate.build("   ", asesorFichaId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("nulo ni vacío");

        assertThatThrownBy(() -> FichaPerfilAggregate.build("", asesorFichaId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("nulo ni vacío");
    }

    @Test
    void debeLanzarExcepcion_cuandoTituloSuperaLongitudMaxima() {
        UUID asesorFichaId = UUID.randomUUID();
        String tituloLargo = "A".repeat(101);

        assertThatThrownBy(() -> FichaPerfilAggregate.build(tituloLargo, asesorFichaId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("100 caracteres");
    }

    @Test
    void debeGenerarIdNoNulo_cuandoBuildEsInvocado() {
        FichaPerfilAggregate ficha = FichaPerfilAggregate.build("Título válido", UUID.randomUUID());

        assertThat(ficha.getId()).isNotNull();
    }

    @Test
    void debeLanzarExcepcion_cuandoAsesorFichaIdEsNulo() {
        assertThatThrownBy(() -> FichaPerfilAggregate.build("Título válido", null))
                .isInstanceOf(DomainValidationException.class);
    }
}
