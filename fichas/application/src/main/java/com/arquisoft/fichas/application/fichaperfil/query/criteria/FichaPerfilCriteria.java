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
        TITULO_PROYECTO("tituloProyecto"),
        ASESOR_NOMBRE("asesorNombre"),
        ASESOR_EMAIL("asesorEmail"),
        ASESOR_ID("asesorId");

        private final String clave;

        Campo(String clave) {
            this.clave = clave;
        }

        public String getClave() {
            return clave;
        }

        static final Set<String> CLAVES = Arrays.stream(values())
                .map(Campo::getClave)
                .collect(Collectors.toUnmodifiableSet());

        public static boolean esValido(String clave) {
            return CLAVES.contains(clave);
        }
    }

    public enum CampoOrden {
        TITULO_PROYECTO("tituloProyecto"),
        ASESOR_NOMBRE("asesorNombre"),
        ASESOR_EMAIL("asesorEmail");

        private final String clave;

        CampoOrden(String clave) {
            this.clave = clave;
        }

        public String getClave() {
            return clave;
        }

        static final Set<String> CLAVES = Arrays.stream(values())
                .map(CampoOrden::getClave)
                .collect(Collectors.toUnmodifiableSet());

        public static boolean esValido(String clave) {
            return CLAVES.contains(clave);
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
                    if (!CampoOrden.esValido(o.getCampo())) {
                        throw new ApplicationException(
                                "Campo de ordenamiento no permitido: '" + o.getCampo() +
                                "'. Campos disponibles: " + CampoOrden.CLAVES,
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
            validarCamposFiltro(nodo, 0);
        }

        private void validarCamposFiltro(NodoFiltro nodo, int profundidad) {
            if (nodo == null) {
                return;
            }
            if (profundidad > QueryCriteria.MAX_PROFUNDIDAD_FILTRO) {
                throw new ApplicationException(
                        "El árbol de filtros supera la profundidad máxima permitida de "
                        + QueryCriteria.MAX_PROFUNDIDAD_FILTRO + " niveles",
                        "PROFUNDIDAD_FILTRO_EXCEDIDA");
            }
            switch (nodo) {
                case NodoFiltro.Predicado p -> {
                    if (!Campo.esValido(p.campo())) {
                        throw new ApplicationException(
                                "Campo de filtro no permitido: '" + p.campo() +
                                "'. Campos disponibles: " + Campo.CLAVES,
                                "CAMPO_FILTRO_NO_PERMITIDO");
                    }
                    if (p.operador().requiereValor() && (p.valor() == null || p.valor().isBlank())) {
                        throw new ApplicationException(
                                "El operador '" + p.operador() + "' requiere un valor no vacío para el campo '"
                                + p.campo() + "'",
                                "VALOR_REQUERIDO");
                    }
                }
                case NodoFiltro.Grupo g ->
                    g.nodos().forEach(hijo -> validarCamposFiltro(hijo, profundidad + 1));
            }
        }
    }
}
