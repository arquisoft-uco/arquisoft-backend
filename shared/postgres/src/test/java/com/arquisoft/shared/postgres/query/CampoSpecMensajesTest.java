package com.arquisoft.shared.postgres.query;

import com.arquisoft.shared.postgres.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.QueryCriteria;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifica que los textos del catálogo se sustituyan con sus argumentos.
 *
 * <p>Ver la nota de {@code FiltroMensajesTest}: la prueba de claves cubre que el texto exista,
 * no que el patrón y la llamada declaren el mismo número de {@code %s}.
 */
class CampoSpecMensajesTest {

    private record Fila(UUID id, String titulo) {}

    private static final class SpecDePrueba extends QueryJpaSpecification<Fila> {

        @Override
        protected Map<String, CampoSpec<Fila>> camposPermitidos() {
            return Map.of("titulo", CampoSpec.texto(root -> root.get("titulo")));
        }
    }

    private static final class CriteriaDePrueba extends QueryCriteria {

        private CriteriaDePrueba(Builder b) {
            super(b);
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder extends QueryCriteria.BaseBuilder<Builder> {
            CriteriaDePrueba build() {
                return new CriteriaDePrueba(this);
            }
        }
    }

    @Test
    void debeNombrarTipoYOperadoresValidos_cuandoElOperadorNoAplicaAlTipoDeCampo() {
        CampoSpec<Fila> campo = CampoSpec.uuid(root -> root.get("id"));

        assertThatThrownBy(() -> campo.construirSpec(FiltroOperador.CONTIENE, "x"))
                .isInstanceOf(FiltroInvalidoException.class)
                .hasMessageContaining("El operador 'CONTIENE' no es aplicable a campos de tipo UUID")
                .hasMessageContaining(FiltroOperador.ES.name());
    }

    @Test
    void debeNombrarLaEtiquetaDelTipo_cuandoElCampoEsDeFecha() {
        CampoSpec<Fila> campo = CampoSpec.fecha(root -> root.get("creado"));

        assertThatThrownBy(() -> campo.construirSpec(FiltroOperador.CONTIENE, "x"))
                .isInstanceOf(FiltroInvalidoException.class)
                .hasMessageContaining("campos de tipo fecha (yyyy-MM-dd)");
    }

    @Test
    void debeNombrarElCampoYLosDisponibles_cuandoElFiltroApuntaAUnCampoNoMapeado() {
        QueryCriteria criteria = CriteriaDePrueba.builder()
                .raiz(NodoFiltro.predicado("inexistente", FiltroOperador.ES, "x"))
                .build();

        assertThatThrownBy(() -> new SpecDePrueba().desdeCriteria(criteria))
                .isInstanceOf(FiltroInvalidoException.class)
                .hasMessageContaining("Campo de filtro desconocido: 'inexistente'")
                .hasMessageContaining("titulo");
    }
}
