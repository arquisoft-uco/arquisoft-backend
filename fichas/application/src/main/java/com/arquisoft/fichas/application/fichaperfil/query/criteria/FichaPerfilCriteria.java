package com.arquisoft.fichas.application.fichaperfil.query.criteria;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.QueryCriteria;
import com.arquisoft.shared.query.SortOrder;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class FichaPerfilCriteria extends QueryCriteria {

    public enum Campo {
        TITULO_PROYECTO("tituloProyecto", true,  true),
        ASESOR_NOMBRE  ("asesorNombre",   true,  true),
        ASESOR_EMAIL   ("asesorEmail",    true,  true),
        ASESOR_ID      ("asesorId",       true,  false);

        private final String  clave;
        private final boolean filtrable;
        private final boolean ordenable;

        Campo(String clave, boolean filtrable, boolean ordenable) {
            this.clave     = clave;
            this.filtrable = filtrable;
            this.ordenable = ordenable;
        }

        public String getClave() { return clave; }

        static final Set<String> CLAVES_FILTRABLES = Arrays.stream(values())
                .filter(c -> c.filtrable)
                .map(Campo::getClave)
                .collect(Collectors.toUnmodifiableSet());

        static final Set<String> CLAVES_ORDENABLES = Arrays.stream(values())
                .filter(c -> c.ordenable)
                .map(Campo::getClave)
                .collect(Collectors.toUnmodifiableSet());

        public static boolean esValidoParaFiltrar(String clave) {
            return CLAVES_FILTRABLES.contains(clave);
        }

        public static boolean esValidoParaOrdenar(String clave) {
            return CLAVES_ORDENABLES.contains(clave);
        }
    }

    private FichaPerfilCriteria(Builder b) {
        super(b);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends QueryCriteria.BaseBuilder<Builder> {

        @Override
        public Builder raiz(NodoFiltro raiz) {
            validarCamposFiltro(raiz);
            return super.raiz(raiz);
        }

        @Override
        public Builder ordenamiento(List<SortOrder> ordenamiento) {
            if (ordenamiento != null) {
                ordenamiento.forEach(o -> {
                    if (!Campo.esValidoParaOrdenar(o.getCampo())) {
                        throw new ApplicationException(
                                "Campo de ordenamiento no permitido: '" + o.getCampo() +
                                "'. Campos disponibles: " + Campo.CLAVES_ORDENABLES,
                                "CAMPO_ORDEN_NO_PERMITIDO");
                    }
                });
            }
            return super.ordenamiento(ordenamiento);
        }

        public FichaPerfilCriteria build() {
            return new FichaPerfilCriteria(this);
        }

        private void validarCamposFiltro(NodoFiltro nodo) {
            if (nodo == null) return;
            switch (nodo) {
                case NodoFiltro.Predicado p -> {
                    if (!Campo.esValidoParaFiltrar(p.campo())) {
                        throw new ApplicationException(
                                "Campo de filtro no permitido: '" + p.campo() +
                                "'. Campos disponibles: " + Campo.CLAVES_FILTRABLES,
                                "CAMPO_FILTRO_NO_PERMITIDO");
                    }
                    if (p.operador().requiereValor() && (p.valor() == null || p.valor().isBlank())) {
                        throw new ApplicationException(
                                "El operador '" + p.operador() + "' requiere un valor no vacío para el campo '"
                                + p.campo() + "'",
                                "VALOR_REQUERIDO");
                    }
                }
                case NodoFiltro.Grupo g -> g.nodos().forEach(this::validarCamposFiltro);
            }
        }
    }
}
