package com.arquisoft.shared.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base inmutable para criterios de consulta paginada con filtros y ordenamiento.
 *
 * Los contextos de negocio extienden esta clase definiendo únicamente su builder
 * concreto. Toda la lógica de paginación, ordenamiento y árbol de filtros se hereda.
 *
 * Principio de extensión (OCP): agregar un nuevo contexto consulta solo requiere
 * crear una subclase con su builder — sin modificar esta clase.
 */
public abstract class QueryCriteria {

    private final int pagina;
    private final int tamanio;
    private final List<SortOrder> ordenamiento;
    private final NodoFiltro raiz;      // null = sin filtros

    protected QueryCriteria(BaseBuilder<?> b) {
        this.pagina       = Math.max(0, b.pagina);
        this.tamanio      = b.tamanio > 0 ? b.tamanio : 10;
        this.ordenamiento = List.copyOf(b.ordenamiento);
        this.raiz         = b.raiz;
    }

    public int getPagina()                   { return pagina; }
    public int getTamanio()                  { return tamanio; }
    public List<SortOrder> getOrdenamiento() { return ordenamiento; }
    public NodoFiltro getRaiz()              { return raiz; }
    public boolean tieneOrden()              { return !ordenamiento.isEmpty(); }
    public boolean tieneFiltros()            { return raiz != null; }

    /**
     * Builder base con self-type para preservar fluidez en subclases.
     *
     * Subclases deben declarar su propio Builder que extienda BaseBuilder<Builder>
     * e implementen build() devolviendo su tipo concreto.
     */
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
