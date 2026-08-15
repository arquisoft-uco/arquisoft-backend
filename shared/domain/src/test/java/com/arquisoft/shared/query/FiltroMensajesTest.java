package com.arquisoft.shared.query;

import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.pagination.SortDirection;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Los textos de este subsistema viven en el catálogo y se arman con {@code String.formatted}.
 *
 * <p>{@code CatalogoMensajesClavesTest} garantiza que cada clave tenga texto, pero no que el
 * patrón declare tantos {@code %s} como argumentos pasa quien lo invoca: sobra uno y el texto sale
 * incompleto, falta uno y revienta con {@code MissingFormatArgumentException} en tiempo de
 * ejecución. Estas pruebas ejercitan cada punto de lanzamiento y afirman sobre el texto ya
 * sustituido, que es lo único que cierra ese hueco.
 */
class FiltroMensajesTest {

    private static final class CriteriaDePrueba extends QueryCriteria {

        private CriteriaDePrueba(Builder b) {
            super(b);
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

            @Override
            protected Set<String> camposFiltrables() {
                return Set.of("titulo");
            }

            @Override
            protected Set<String> camposOrdenables() {
                return Set.of("titulo");
            }

            CriteriaDePrueba build() {
                return new CriteriaDePrueba(this);
            }
        }
    }

    @Test
    void debeNombrarElConector_cuandoElConectorEsInvalido() {
        assertThatThrownBy(() -> FiltroConector.parse("XOR"))
                .isInstanceOf(FiltroException.class)
                .hasMessage("Conector de filtro inválido: 'XOR'. Use AND u OR");
    }

    @Test
    void debeListarLosOperadoresDelEnum_cuandoElOperadorEsInvalido() {
        assertThatThrownBy(() -> FiltroOperador.parse("PARECIDO_A"))
                .isInstanceOf(FiltroException.class)
                .hasMessageContaining("Operador de filtro inválido: 'PARECIDO_A'")
                // La lista se deriva del enum, no se escribe en el mensaje: si alguien agrega
                // un operador debe aparecer aqui sin tocar el catalogo.
                .hasMessageContaining(FiltroOperador.CONTIENE.name())
                .hasMessageContaining(FiltroOperador.NO_ES_NULO.name());
    }

    @Test
    void debeNombrarElCampoYLosDisponibles_cuandoElCampoDeOrdenNoEstaPermitido() {
        assertThatThrownBy(() -> CriteriaDePrueba.builder()
                .ordenamiento(List.of(SortOrder.of("otro", SortDirection.ASC))))
                .isInstanceOf(FiltroException.class)
                .hasMessageContaining("Campo de ordenamiento no permitido: 'otro'")
                .hasMessageContaining("titulo");
    }

    @Test
    void debeNombrarElCampoYLosDisponibles_cuandoElCampoDeFiltroNoEstaPermitido() {
        assertThatThrownBy(() -> CriteriaDePrueba.builder()
                .raiz(NodoFiltro.predicado("otro", FiltroOperador.ES, "x")))
                .isInstanceOf(FiltroException.class)
                .hasMessageContaining("Campo de filtro no permitido: 'otro'")
                .hasMessageContaining("titulo");
    }

    @Test
    void debeNombrarOperadorYCampo_cuandoElOperadorRequiereValorYNoLoTrae() {
        assertThatThrownBy(() -> CriteriaDePrueba.builder()
                .raiz(NodoFiltro.predicado("titulo", FiltroOperador.CONTIENE, "  ")))
                .isInstanceOf(FiltroException.class)
                .hasMessage("El operador 'CONTIENE' requiere un valor no vacío para el campo 'titulo'");
    }

    @Test
    void debeIndicarLaProfundidadMaxima_cuandoElArbolDeFiltrosLaSupera() {
        NodoFiltro nodo = NodoFiltro.predicado("titulo", FiltroOperador.ES, "x");
        for (int i = 0; i <= QueryCriteria.MAX_PROFUNDIDAD_FILTRO; i++) {
            nodo = NodoFiltro.grupo(FiltroConector.AND, List.of(nodo));
        }
        NodoFiltro demasiadoProfundo = nodo;

        assertThatThrownBy(() -> CriteriaDePrueba.builder().raiz(demasiadoProfundo))
                .isInstanceOf(FiltroException.class)
                .hasMessage("El árbol de filtros supera la profundidad máxima de "
                        + QueryCriteria.MAX_PROFUNDIDAD_FILTRO + " niveles");
    }

    @Test
    void debeExplicarLasDirecciones_cuandoLaDireccionDeOrdenEsInvalida() {
        assertThatThrownBy(() -> SortOrder.parse("titulo:ARRIBA"))
                .isInstanceOf(FiltroException.class)
                .hasMessage("Dirección de ordenamiento inválida: 'ARRIBA'. Use ASC o DESC");
    }

    @Test
    void debeReportarCampoVacio_cuandoLaExpresionDeOrdenNoTraeCampo() {
        assertThatThrownBy(() -> SortOrder.parse(" :ASC"))
                .isInstanceOf(FiltroException.class)
                .hasMessage("El campo de ordenamiento no puede estar vacío");
    }

    @Test
    void debeExponerElCodigoEstable_cuandoSeLanzaCadaError() {
        assertThatThrownBy(() -> FiltroConector.parse("XOR"))
                .isInstanceOf(FiltroException.class)
                .extracting(e -> ((FiltroException) e).getCodigoError())
                .isEqualTo(AppCodes.Consulta.FILTRO_CONECTOR_INVALIDO);
    }
}
