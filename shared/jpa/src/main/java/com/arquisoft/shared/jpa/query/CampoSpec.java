package com.arquisoft.shared.jpa.query;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.ConsultaKey;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.query.FiltroOperador;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public sealed interface CampoSpec<E>
        permits CampoSpec.Texto, CampoSpec.Uuid,
                CampoSpec.Entero, CampoSpec.Decimal,
                CampoSpec.Fecha, CampoSpec.FechaHora,
                CampoSpec.Booleano {

    Specification<E> construirSpec(FiltroOperador operador, String valor);

    // ── Factories ─────────────────────────────────────────────────────────────

    static <E> Texto<E> texto(Function<Root<E>, Expression<String>> path) {
        return new Texto<>(path);
    }

    static <E> Uuid<E> uuid(Function<Root<E>, Path<UUID>> path) {
        return new Uuid<>(path);
    }

    static <E> Entero<E> entero(Function<Root<E>, Path<Long>> path) {
        return new Entero<>(path);
    }

    static <E> Decimal<E> decimal(Function<Root<E>, Path<BigDecimal>> path) {
        return new Decimal<>(path);
    }

    static <E> Fecha<E> fecha(Function<Root<E>, Path<LocalDate>> path) {
        return new Fecha<>(path);
    }

    static <E> FechaHora<E> fechaHora(Function<Root<E>, Path<LocalDateTime>> path) {
        return new FechaHora<>(path);
    }

    static <E> Booleano<E> booleano(Function<Root<E>, Path<Boolean>> path) {
        return new Booleano<>(path);
    }

    // ── Implementaciones sealed ────────────────────────────────────────────────

    record Texto<E>(Function<Root<E>, Expression<String>> path) implements CampoSpec<E> {

        private static final Set<FiltroOperador> VALIDOS = EnumSet.of(
                FiltroOperador.CONTIENE, FiltroOperador.NO_CONTIENE,
                FiltroOperador.EMPIEZA_CON, FiltroOperador.TERMINA_CON,
                FiltroOperador.ES, FiltroOperador.NO_ES,
                FiltroOperador.ES_NULO, FiltroOperador.NO_ES_NULO
        );

        private static final char ESCAPE_LIKE = '\\';

        @Override
        public Specification<E> construirSpec(FiltroOperador operador, String valor) {
            validar(operador, VALIDOS, ConsultaKey.TIPO_TEXTO);
            String v = escaparComodines(valor);
            return (root, q, cb) -> {
                Expression<String> rawExpr = path.apply(root);
                return switch (operador) {
                    case CONTIENE    -> cb.like(cb.lower(rawExpr), "%" + v + "%", ESCAPE_LIKE);
                    case NO_CONTIENE -> cb.notLike(cb.lower(rawExpr), "%" + v + "%", ESCAPE_LIKE);
                    case EMPIEZA_CON -> cb.like(cb.lower(rawExpr), v + "%", ESCAPE_LIKE);
                    case TERMINA_CON -> cb.like(cb.lower(rawExpr), "%" + v, ESCAPE_LIKE);
                    case ES         -> cb.equal(rawExpr, valor);
                    case NO_ES      -> cb.notEqual(rawExpr, valor);
                    case ES_NULO    -> cb.isNull(rawExpr);
                    case NO_ES_NULO -> cb.isNotNull(rawExpr);
                    default         -> throw new IllegalStateException();
                };
            };
        }

        // Sin esto, un '%' o '_' en el texto buscado se interpreta como comodín de LIKE:
        // buscar "50%" devolvería toda la tabla en lugar de las filas que contienen "50%".
        private static String escaparComodines(String valor) {
            if (valor == null) {
                return "";
            }
            return valor.toLowerCase()
                    .replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
        }
    }

    record Uuid<E>(Function<Root<E>, Path<UUID>> path) implements CampoSpec<E> {

        private static final Set<FiltroOperador> VALIDOS = EnumSet.of(
                FiltroOperador.ES, FiltroOperador.NO_ES,
                FiltroOperador.ES_NULO, FiltroOperador.NO_ES_NULO
        );

        @Override
        public Specification<E> construirSpec(FiltroOperador operador, String valor) {
            validar(operador, VALIDOS, ConsultaKey.TIPO_UUID);
            return (root, q, cb) -> {
                Path<UUID> p = path.apply(root);
                return switch (operador) {
                    case ES         -> cb.equal(p, parseUuid(valor));
                    case NO_ES      -> cb.notEqual(p, parseUuid(valor));
                    case ES_NULO    -> cb.isNull(p);
                    case NO_ES_NULO -> cb.isNotNull(p);
                    default         -> throw new IllegalStateException();
                };
            };
        }

        private UUID parseUuid(String v) {
            try {
                return UUID.fromString(v);
            } catch (IllegalArgumentException e) {
                throw new FiltroInvalidoException(Mensajes.formatear(ConsultaKey.ERROR_UUID_INVALIDO, v));
            }
        }
    }

    record Entero<E>(Function<Root<E>, Path<Long>> path) implements CampoSpec<E> {

        private static final Set<FiltroOperador> VALIDOS = EnumSet.of(
                FiltroOperador.ES, FiltroOperador.NO_ES,
                FiltroOperador.MAYOR_QUE, FiltroOperador.MENOR_QUE,
                FiltroOperador.MAYOR_IGUAL_QUE, FiltroOperador.MENOR_IGUAL_QUE,
                FiltroOperador.ES_NULO, FiltroOperador.NO_ES_NULO
        );

        @Override
        public Specification<E> construirSpec(FiltroOperador operador, String valor) {
            validar(operador, VALIDOS, ConsultaKey.TIPO_ENTERO);
            return (root, q, cb) -> {
                Path<Long> p = path.apply(root);
                return switch (operador) {
                    case ES              -> cb.equal(p, parseLong(valor));
                    case NO_ES           -> cb.notEqual(p, parseLong(valor));
                    case MAYOR_QUE       -> cb.greaterThan(p, parseLong(valor));
                    case MENOR_QUE       -> cb.lessThan(p, parseLong(valor));
                    case MAYOR_IGUAL_QUE -> cb.greaterThanOrEqualTo(p, parseLong(valor));
                    case MENOR_IGUAL_QUE -> cb.lessThanOrEqualTo(p, parseLong(valor));
                    case ES_NULO         -> cb.isNull(p);
                    case NO_ES_NULO      -> cb.isNotNull(p);
                    default              -> throw new IllegalStateException();
                };
            };
        }

        private Long parseLong(String v) {
            try {
                return Long.parseLong(v);
            } catch (NumberFormatException e) {
                throw new FiltroInvalidoException(Mensajes.formatear(ConsultaKey.ERROR_ENTERO_INVALIDO, v));
            }
        }
    }

    record Decimal<E>(Function<Root<E>, Path<BigDecimal>> path) implements CampoSpec<E> {

        private static final Set<FiltroOperador> VALIDOS = EnumSet.of(
                FiltroOperador.ES, FiltroOperador.NO_ES,
                FiltroOperador.MAYOR_QUE, FiltroOperador.MENOR_QUE,
                FiltroOperador.MAYOR_IGUAL_QUE, FiltroOperador.MENOR_IGUAL_QUE,
                FiltroOperador.ES_NULO, FiltroOperador.NO_ES_NULO
        );

        @Override
        public Specification<E> construirSpec(FiltroOperador operador, String valor) {
            validar(operador, VALIDOS, ConsultaKey.TIPO_DECIMAL);
            return (root, q, cb) -> {
                Path<BigDecimal> p = path.apply(root);
                return switch (operador) {
                    case ES              -> cb.equal(p, parseDecimal(valor));
                    case NO_ES           -> cb.notEqual(p, parseDecimal(valor));
                    case MAYOR_QUE       -> cb.greaterThan(p, parseDecimal(valor));
                    case MENOR_QUE       -> cb.lessThan(p, parseDecimal(valor));
                    case MAYOR_IGUAL_QUE -> cb.greaterThanOrEqualTo(p, parseDecimal(valor));
                    case MENOR_IGUAL_QUE -> cb.lessThanOrEqualTo(p, parseDecimal(valor));
                    case ES_NULO         -> cb.isNull(p);
                    case NO_ES_NULO      -> cb.isNotNull(p);
                    default              -> throw new IllegalStateException();
                };
            };
        }

        private BigDecimal parseDecimal(String v) {
            try {
                return new BigDecimal(v);
            } catch (NumberFormatException e) {
                throw new FiltroInvalidoException(Mensajes.formatear(ConsultaKey.ERROR_DECIMAL_INVALIDO, v));
            }
        }
    }

    record Fecha<E>(Function<Root<E>, Path<LocalDate>> path) implements CampoSpec<E> {

        private static final Set<FiltroOperador> VALIDOS = EnumSet.of(
                FiltroOperador.ES, FiltroOperador.NO_ES,
                FiltroOperador.MAYOR_QUE, FiltroOperador.MENOR_QUE,
                FiltroOperador.MAYOR_IGUAL_QUE, FiltroOperador.MENOR_IGUAL_QUE,
                FiltroOperador.ES_NULO, FiltroOperador.NO_ES_NULO
        );

        @Override
        public Specification<E> construirSpec(FiltroOperador operador, String valor) {
            validar(operador, VALIDOS, ConsultaKey.TIPO_FECHA);
            return (root, q, cb) -> {
                Path<LocalDate> p = path.apply(root);
                return switch (operador) {
                    case ES              -> cb.equal(p, parseFecha(valor));
                    case NO_ES           -> cb.notEqual(p, parseFecha(valor));
                    case MAYOR_QUE       -> cb.greaterThan(p, parseFecha(valor));
                    case MENOR_QUE       -> cb.lessThan(p, parseFecha(valor));
                    case MAYOR_IGUAL_QUE -> cb.greaterThanOrEqualTo(p, parseFecha(valor));
                    case MENOR_IGUAL_QUE -> cb.lessThanOrEqualTo(p, parseFecha(valor));
                    case ES_NULO         -> cb.isNull(p);
                    case NO_ES_NULO      -> cb.isNotNull(p);
                    default              -> throw new IllegalStateException();
                };
            };
        }

        private LocalDate parseFecha(String v) {
            try {
                return LocalDate.parse(v);
            } catch (DateTimeParseException e) {
                throw new FiltroInvalidoException(
                        Mensajes.formatear(ConsultaKey.ERROR_FECHA_INVALIDA, v));
            }
        }
    }

    record FechaHora<E>(Function<Root<E>, Path<LocalDateTime>> path) implements CampoSpec<E> {

        private static final Set<FiltroOperador> VALIDOS = EnumSet.of(
                FiltroOperador.ES, FiltroOperador.NO_ES,
                FiltroOperador.MAYOR_QUE, FiltroOperador.MENOR_QUE,
                FiltroOperador.MAYOR_IGUAL_QUE, FiltroOperador.MENOR_IGUAL_QUE,
                FiltroOperador.ES_NULO, FiltroOperador.NO_ES_NULO
        );

        @Override
        public Specification<E> construirSpec(FiltroOperador operador, String valor) {
            validar(operador, VALIDOS, ConsultaKey.TIPO_FECHA_HORA);
            return (root, q, cb) -> {
                Path<LocalDateTime> p = path.apply(root);
                return switch (operador) {
                    case ES              -> cb.equal(p, parseFechaHora(valor));
                    case NO_ES           -> cb.notEqual(p, parseFechaHora(valor));
                    case MAYOR_QUE       -> cb.greaterThan(p, parseFechaHora(valor));
                    case MENOR_QUE       -> cb.lessThan(p, parseFechaHora(valor));
                    case MAYOR_IGUAL_QUE -> cb.greaterThanOrEqualTo(p, parseFechaHora(valor));
                    case MENOR_IGUAL_QUE -> cb.lessThanOrEqualTo(p, parseFechaHora(valor));
                    case ES_NULO         -> cb.isNull(p);
                    case NO_ES_NULO      -> cb.isNotNull(p);
                    default              -> throw new IllegalStateException();
                };
            };
        }

        private LocalDateTime parseFechaHora(String v) {
            try {
                return LocalDateTime.parse(v);
            } catch (DateTimeParseException e) {
                throw new FiltroInvalidoException(
                        Mensajes.formatear(ConsultaKey.ERROR_FECHA_HORA_INVALIDA, v));
            }
        }
    }

    record Booleano<E>(Function<Root<E>, Path<Boolean>> path) implements CampoSpec<E> {

        private static final Set<FiltroOperador> VALIDOS = EnumSet.of(
                FiltroOperador.ES, FiltroOperador.NO_ES,
                FiltroOperador.ES_NULO, FiltroOperador.NO_ES_NULO
        );

        @Override
        public Specification<E> construirSpec(FiltroOperador operador, String valor) {
            validar(operador, VALIDOS, ConsultaKey.TIPO_BOOLEANO);
            return (root, q, cb) -> {
                Path<Boolean> p = path.apply(root);
                return switch (operador) {
                    case ES         -> cb.equal(p, parseBooleano(valor));
                    case NO_ES      -> cb.notEqual(p, parseBooleano(valor));
                    case ES_NULO    -> cb.isNull(p);
                    case NO_ES_NULO -> cb.isNotNull(p);
                    default         -> throw new IllegalStateException();
                };
            };
        }

        private Boolean parseBooleano(String v) {
            if ("true".equalsIgnoreCase(v) || "false".equalsIgnoreCase(v)) {
                return Boolean.parseBoolean(v);
            }
            throw new FiltroInvalidoException(Mensajes.formatear(ConsultaKey.ERROR_BOOLEANO_INVALIDO, v));
        }
    }

    // ── Utilidad compartida entre implementaciones ─────────────────────────────

    private static void validar(FiltroOperador operador, Set<FiltroOperador> validos, ConsultaKey tipo) {
        if (!validos.contains(operador)) {
            throw new FiltroInvalidoException(
                    Mensajes.formatear(ConsultaKey.ERROR_OPERADOR_NO_APLICABLE,
                            operador, Mensajes.obtener(tipo), validos)
            );
        }
    }
}
