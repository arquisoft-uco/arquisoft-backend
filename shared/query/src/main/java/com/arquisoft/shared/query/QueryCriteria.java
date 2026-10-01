package com.arquisoft.shared.query;

import com.arquisoft.shared.query.exception.FiltroException;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.key.app.ConsultaKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
            if (ordenamiento != null) {
                Set<String> permitidos = camposOrdenables();
                if (permitidos != null) {
                    ordenamiento.forEach(o -> {
                        if (!permitidos.contains(o.getCampo())) {
                            throw new FiltroException(
                                    Mensajes.formatear(ConsultaKey.ERROR_CAMPO_ORDEN_NO_PERMITIDO,
                                            o.getCampo(), permitidos),
                                    AppCodes.Consulta.CAMPO_ORDEN_NO_PERMITIDO);
                        }
                    });
                }
            }
            this.ordenamiento = ordenamiento != null ? ordenamiento : new ArrayList<>();
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B raiz(NodoFiltro raiz) {
            if (raiz != null) {
                validarProfundidad(raiz, 0);
                validarPredicados(raiz);
            }
            this.raiz = raiz;
            return (B) this;
        }

        protected Set<String> camposFiltrables() {
            return null;
        }

        protected Set<String> camposOrdenables() {
            return null;
        }

        private void validarProfundidad(NodoFiltro nodo, int profundidad) {
            if (profundidad > MAX_PROFUNDIDAD_FILTRO) {
                throw new FiltroException(
                        Mensajes.formatear(ConsultaKey.ERROR_PROFUNDIDAD_FILTRO_EXCEDIDA,
                                MAX_PROFUNDIDAD_FILTRO),
                        AppCodes.Consulta.PROFUNDIDAD_FILTRO_EXCEDIDA);
            }
            if (nodo instanceof NodoFiltro.Grupo g) {
                g.nodos().forEach(hijo -> validarProfundidad(hijo, profundidad + 1));
            }
        }

        private void validarPredicados(NodoFiltro nodo) {
            if (nodo == null) {
                return;
            }
            Set<String> permitidos = camposFiltrables();
            switch (nodo) {
                case NodoFiltro.Predicado p -> {
                    validarCampoPermitido(p.campo(), permitidos);
                    if (p.operador().esMultivalor()) {
                        throw new FiltroException(
                                Mensajes.formatear(ConsultaKey.ERROR_OPERADOR_REQUIERE_LISTA,
                                        p.operador(), p.campo()),
                                AppCodes.Consulta.OPERADOR_REQUIERE_LISTA);
                    }
                    if (p.operador().requiereValor() && (p.valor() == null || p.valor().isBlank())) {
                        throw new FiltroException(
                                Mensajes.formatear(ConsultaKey.ERROR_VALOR_REQUERIDO,
                                        p.operador(), p.campo()),
                                AppCodes.Consulta.VALOR_REQUERIDO);
                    }
                }
                case NodoFiltro.PredicadoMultivalor p -> {
                    validarCampoPermitido(p.campo(), permitidos);
                    if (!p.operador().esMultivalor()) {
                        throw new FiltroException(
                                Mensajes.formatear(ConsultaKey.ERROR_OPERADOR_NO_ACEPTA_LISTA,
                                        p.operador(), p.campo()),
                                AppCodes.Consulta.OPERADOR_NO_ACEPTA_LISTA);
                    }
                    if (p.valores().isEmpty()) {
                        throw new FiltroException(
                                Mensajes.formatear(ConsultaKey.ERROR_VALOR_REQUERIDO,
                                        p.operador(), p.campo()),
                                AppCodes.Consulta.VALOR_REQUERIDO);
                    }
                }
                case NodoFiltro.Grupo g -> g.nodos().forEach(this::validarPredicados);
            }
        }

        private void validarCampoPermitido(String campo, Set<String> permitidos) {
            if (permitidos != null && !permitidos.contains(campo)) {
                throw new FiltroException(
                        Mensajes.formatear(ConsultaKey.ERROR_CAMPO_FILTRO_NO_PERMITIDO, campo, permitidos),
                        AppCodes.Consulta.CAMPO_FILTRO_NO_PERMITIDO);
            }
        }
    }
}
