package com.arquisoft.mapas_ruta.infrastructure.tarea.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.dto.PredicadoFiltroDTO;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarTareasMapaRutaEstudianteRequestMapperTest {

    @Test
    void debeArmarQuery_conCuerpoYMapaRuta() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();
        var dto = new QueryCriteriaRequestDTO();
        dto.setPagina(1);
        dto.setTamanio(20);
        dto.setOrdenamiento(List.of("estadoItem:DESC"));
        dto.setFiltros(new PredicadoFiltroDTO("descripcion", "CONTIENE", "cap"));

        // Act
        var query = ConsultarTareasMapaRutaEstudianteRequestMapper.toQuery(dto, mapaRuta);

        // Assert
        assertThat(query.mapaRuta()).isEqualTo(mapaRuta);
        assertThat(query.criterio().pagina()).isEqualTo(1);
        assertThat(query.criterio().tamanio()).isEqualTo(20);
        assertThat(query.criterio().ordenamiento()).hasSize(1);
        assertThat(query.criterio().ordenamiento().get(0).getCampo()).isEqualTo("estadoItem");
        assertThat(query.criterio().ordenamiento().get(0).getDireccion()).isEqualTo(SortDirection.DESC);
        assertThat(query.criterio().raiz())
                .isEqualTo(NodoFiltro.predicado("descripcion", FiltroOperador.CONTIENE, "cap"));
    }

    @Test
    void debeAplicarPorDefecto_cuandoCuerpoNulo() {
        // Arrange
        var mapaRuta = UtilUUID.generarNuevoUUID();

        // Act
        var query = ConsultarTareasMapaRutaEstudianteRequestMapper.toQuery(null, mapaRuta);

        // Assert
        assertThat(query.mapaRuta()).isEqualTo(mapaRuta);
        assertThat(query.criterio().pagina()).isZero();
        assertThat(query.criterio().tamanio()).isEqualTo(10);
        assertThat(query.criterio().ordenamiento()).isEmpty();
        assertThat(query.criterio().raiz()).isNull();
    }
}
