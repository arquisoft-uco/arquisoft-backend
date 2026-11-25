package com.arquisoft.artefactos.application.estudianteartefacto.query.criteria;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.exception.FiltroException;
import com.arquisoft.shared.query.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParticipacionArtefactoCriteriaTest {

    @Test
    void debeDeclararFiltrableYOrdenable_segunLoConfiguradoPorCampo() {
        // Arrange & Act & Assert
        for (var clave : List.of("nombreProyecto", "nombreDocumento", "versionDocumento", "nombreRepositorio",
                "nombreAsesor", "estadoUltimaRevision", "omitido", "proyectoFinalizado", "proyectoCancelado",
                "estudianteId")) {
            assertThat(ParticipacionArtefactoCriteria.Campo.esValidoParaFiltrar(clave)).isTrue();
        }
        for (var clave : List.of("nombreProyecto", "nombreDocumento", "versionDocumento", "nombreRepositorio",
                "nombreAsesor", "estadoUltimaRevision")) {
            assertThat(ParticipacionArtefactoCriteria.Campo.esValidoParaOrdenar(clave)).isTrue();
        }
        for (var clave : List.of("omitido", "proyectoFinalizado", "proyectoCancelado", "estudianteId")) {
            assertThat(ParticipacionArtefactoCriteria.Campo.esValidoParaOrdenar(clave)).isFalse();
        }
        for (var clave : List.of("proyectoGrado", "asesor", "versionRepositorioArtefacto")) {
            assertThat(ParticipacionArtefactoCriteria.Campo.esValidoParaFiltrar(clave)).isFalse();
        }
    }

    @Test
    void debeLanzarFiltroException_cuandoRaizFiltraPorCampoNoDeclarado() {
        // Arrange
        var raiz = NodoFiltro.predicado("asesor", FiltroOperador.ES, "valor");
        var builder = ParticipacionArtefactoCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.raiz(raiz)).isInstanceOf(FiltroException.class);
    }

    @Test
    void debeLanzarFiltroException_cuandoOrdenaPorCampoNoOrdenable() {
        // Arrange
        var ordenamiento = List.of(SortOrder.of("omitido", SortDirection.ASC));
        var builder = ParticipacionArtefactoCriteria.builder().pagina(0).tamanio(10);

        // Act & Assert
        assertThatThrownBy(() -> builder.ordenamiento(ordenamiento)).isInstanceOf(FiltroException.class);
    }
}
