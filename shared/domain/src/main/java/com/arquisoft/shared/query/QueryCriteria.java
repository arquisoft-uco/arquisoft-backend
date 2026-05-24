package com.arquisoft.shared.query;

import java.util.ArrayList;
import java.util.List;

public abstract class QueryCriteria {

    public static final int MAX_TAMANIO          = 100;
    public static final int MAX_PROFUNDIDAD_FILTRO = 10;

    private final int pagina;
    private final int tamanio;
    private final List<SortOrder> ordenamiento;
    private final NodoFiltro raiz;

    protected QueryCriteria(BaseBuilder<?> b) {
        this.pagina       = Math.max(0, b.pagina);
        int tamanioRaw    = b.tamanio > 0 ? b.tamanio : 10;
        this.tamanio      = Math.min(tamanioRaw, MAX_TAMANIO);
        this.ordenamiento = List.copyOf(b.ordenamiento);
        this.raiz         = b.raiz;
    }

    public int getPagina() {
        return pagina;
    }

    public int getTamanio() {
        return tamanio;
    }

    public List<SortOrder> getOrdenamiento() {
        return ordenamiento;
    }

    public NodoFiltro getRaiz() {
        return raiz;
    }

    public boolean tieneOrden() {
        return !ordenamiento.isEmpty();
    }

    public boolean tieneFiltros() {
        return raiz != null;
    }

    protected abstract static class BaseBuilder<B extends BaseBuilder<B>> {

        private int pagina    = 0;
        private int tamanio   = 10;
        private List<SortOrder> ordenamiento = new ArrayList<>();
        private NodoFiltro raiz;

        @SuppressWarnings("unchecked")
        public B pagina(int pagina) {
            this.pagina = pagina;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B tamanio(int tamanio) {
            this.tamanio = tamanio;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B ordenamiento(List<SortOrder> ordenamiento) {
            this.ordenamiento = ordenamiento != null ? ordenamiento : new ArrayList<>();
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B raiz(NodoFiltro raiz) {
            this.raiz = raiz;
            return (B) this;
        }
    }
}
