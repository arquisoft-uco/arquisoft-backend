package com.arquisoft.shared.message.constant;

/**
 * Códigos de error del contexto proyectos. Ver la nota de {@link AppCodes}.
 */
public final class ProyectosCodes {

    private ProyectosCodes() {}

    public static final class Coordinador {

        private Coordinador() {}

        public static final String ID_REQUERIDO = "COORDINADOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "COORDINADOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "COORDINADOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "COORDINADOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "COORDINADOR_OCURRIDO_EN_REQUERIDO";
    }

    public static final class Asesor {

        private Asesor() {}

        public static final String ID_REQUERIDO = "ASESOR_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ASESOR_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ASESOR_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ASESOR_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ASESOR_OCURRIDO_EN_REQUERIDO";
        public static final String NO_ENCONTRADO = "ASESOR_NO_ENCONTRADO";
    }

    public static final class Estudiante {

        private Estudiante() {}

        public static final String ID_REQUERIDO = "ESTUDIANTE_ID_REQUERIDO";
        public static final String IDENTIFICADOR_REQUERIDO = "ESTUDIANTE_IDENTIFICADOR_REQUERIDO";
        public static final String NOMBRE_REQUERIDO = "ESTUDIANTE_NOMBRE_REQUERIDO";
        public static final String EMAIL_REQUERIDO = "ESTUDIANTE_EMAIL_REQUERIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "ESTUDIANTE_OCURRIDO_EN_REQUERIDO";
    }

    public static final class ProyectoGrado {

        private ProyectoGrado() {}

        public static final String FICHA_PERFIL_ID_REQUERIDO = "PROYECTO_GRADO_FICHA_PERFIL_ID_REQUERIDO";
        public static final String TITULO_REQUERIDO = "PROYECTO_GRADO_TITULO_REQUERIDO";
        public static final String TITULO_LONGITUD_MAXIMA = "PROYECTO_GRADO_TITULO_LONGITUD_MAXIMA";
        public static final String COORDINADOR_ID_REQUERIDO = "PROYECTO_GRADO_COORDINADOR_ID_REQUERIDO";
        public static final String PROYECTO_GRADO_REQUERIDO = "PROYECTO_GRADO_REQUERIDO";
        public static final String COORDINADOR_NO_VIGENTE = "PROYECTO_GRADO_COORDINADOR_NO_VIGENTE";
        public static final String NO_ENCONTRADO = "PROYECTO_GRADO_NO_ENCONTRADO";
        public static final String FINALIZADO = "PROYECTO_GRADO_FINALIZADO";
        public static final String NO_PERTENECE_COORDINADOR = "PROYECTO_GRADO_NO_PERTENECE_COORDINADOR";
        public static final String ESTUDIANTE_ID_REQUERIDO = "PROYECTO_GRADO_ESTUDIANTE_ID_REQUERIDO";
        public static final String ASESOR_ID_REQUERIDO = "PROYECTO_GRADO_ASESOR_ID_REQUERIDO";
        public static final String PROYECTO_GRADO_ID_REQUERIDO = "PROYECTO_GRADO_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String ESTADO_PROYECTO_REQUERIDO = "PROYECTO_GRADO_ESTADO_PROYECTO_REQUERIDO";
        public static final String ESTADO_PROYECTO_INVALIDO = "PROYECTO_GRADO_ESTADO_PROYECTO_INVALIDO";
        public static final String TRANSICION_ESTADO_NO_PERMITIDA = "PROYECTO_GRADO_TRANSICION_ESTADO_NO_PERMITIDA";
        public static final String SIN_ENTREGABLE = "PROYECTO_GRADO_SIN_ENTREGABLE";
        public static final String ULTIMO_ENTREGABLE_NO_DEFINITIVO = "PROYECTO_GRADO_ULTIMO_ENTREGABLE_NO_DEFINITIVO";
    }

    public static final class EstudianteProyectoGrado {

        private EstudianteProyectoGrado() {}

        public static final String PROYECTO_GRADO_ID_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String COORDINADOR_ID_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_COORDINADOR_ID_REQUERIDO";
        public static final String ASESOR_ID_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_ASESOR_ID_REQUERIDO";
        public static final String ESTUDIANTE_ID_REQUERIDO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_ID_REQUERIDO";
        public static final String ESTUDIANTE_ID_INVALIDO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_ID_INVALIDO";
        public static final String ESTUDIANTES_REQUERIDOS = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_REQUERIDOS";
        public static final String ESTUDIANTES_MAXIMO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_MAXIMO";
        public static final String ESTUDIANTES_NO_VIGENTES = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTES_NO_VIGENTES";
        public static final String ESTUDIANTE_DUPLICADO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_DUPLICADO";
        public static final String CUPO_EXCEDIDO = "ESTUDIANTE_PROYECTO_GRADO_CUPO_EXCEDIDO";
        public static final String ESTUDIANTE_YA_VINCULADO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_YA_VINCULADO";
        public static final String ESTUDIANTE_NO_VINCULADO = "ESTUDIANTE_PROYECTO_GRADO_ESTUDIANTE_NO_VINCULADO";
    }

    public static final class EstadoProyectoGrado {

        private EstadoProyectoGrado() {}

        public static final String NO_ENCONTRADO = "ESTADO_PROYECTO_GRADO_NO_ENCONTRADO";
        public static final String PROYECTO_GRADO_ID_REQUERIDO = "ESTADO_PROYECTO_GRADO_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String ESTUDIANTE_ID_REQUERIDO = "ESTADO_PROYECTO_GRADO_ESTUDIANTE_ID_REQUERIDO";
        public static final String ESTADO_INICIAL_REQUERIDO = "ESTADO_PROYECTO_GRADO_ESTADO_INICIAL_REQUERIDO";
        public static final String ESTADO_PROYECTO_REQUERIDO = "ESTADO_PROYECTO_GRADO_ESTADO_PROYECTO_REQUERIDO";
    }

    public static final class EstadoProyecto {

        private EstadoProyecto() {}

        public static final String NO_ENCONTRADO = "ESTADO_PROYECTO_NO_ENCONTRADO";
    }

    public static final class EstadoEntregable {

        private EstadoEntregable() {}

        public static final String NO_ENCONTRADO = "PROYECTO_GRADO_ESTADO_ENTREGABLE_NO_ENCONTRADO";
    }

    public static final class EntregableProyectoGrado {

        private EntregableProyectoGrado() {}

        public static final String ID_REQUERIDO = "PROYECTO_GRADO_ENTREGABLE_ID_REQUERIDO";
        public static final String PROYECTO_GRADO_REQUERIDO = "PROYECTO_GRADO_ENTREGABLE_PROYECTO_GRADO_REQUERIDO";
        public static final String VERSION_INVALIDA = "PROYECTO_GRADO_ENTREGABLE_VERSION_INVALIDA";
        public static final String ESTADO_ENTREGABLE_REQUERIDO =
                "PROYECTO_GRADO_ENTREGABLE_ESTADO_ENTREGABLE_REQUERIDO";
        public static final String ESTADO_ENTREGABLE_INVALIDO =
                "PROYECTO_GRADO_ENTREGABLE_ESTADO_ENTREGABLE_INVALIDO";
        public static final String OCURRIDO_EN_REQUERIDO = "PROYECTO_GRADO_ENTREGABLE_OCURRIDO_EN_REQUERIDO";
    }

    public static final class AsesorProyectoGrado {

        private AsesorProyectoGrado() {}

        public static final String PROYECTO_GRADO_ID_REQUERIDO = "ASESOR_PROYECTO_GRADO_PROYECTO_GRADO_ID_REQUERIDO";
        public static final String COORDINADOR_ID_REQUERIDO = "ASESOR_PROYECTO_GRADO_COORDINADOR_ID_REQUERIDO";
        public static final String ESTUDIANTE_ID_REQUERIDO = "ASESOR_PROYECTO_GRADO_ESTUDIANTE_ID_REQUERIDO";
        public static final String ASESOR_ID_REQUERIDO = "ASESOR_PROYECTO_GRADO_ASESOR_ID_REQUERIDO";
        public static final String ASESOR_ID_INVALIDO = "ASESOR_PROYECTO_GRADO_ASESOR_ID_INVALIDO";
        public static final String ASESOR_NO_VIGENTE = "ASESOR_PROYECTO_GRADO_ASESOR_NO_VIGENTE";
        public static final String ASESOR_YA_ASIGNADO = "ASESOR_PROYECTO_GRADO_ASESOR_YA_ASIGNADO";
        public static final String CUPO_ASESORES_EXCEDIDO = "ASESOR_PROYECTO_GRADO_CUPO_ASESORES_EXCEDIDO";
        public static final String ESTADO_ASESOR_REQUERIDO = "ASESOR_PROYECTO_GRADO_ESTADO_ASESOR_REQUERIDO";
        public static final String ESTADO_ASESOR_INVALIDO = "ASESOR_PROYECTO_GRADO_ESTADO_ASESOR_INVALIDO";
        public static final String ASESOR_PROYECTO_GRADO_NO_ENCONTRADO = "ASESOR_PROYECTO_GRADO_NO_ENCONTRADO";
        public static final String TRANSICION_ESTADO_ASESOR_INVALIDA =
                "ASESOR_PROYECTO_GRADO_TRANSICION_ESTADO_ASESOR_INVALIDA";
    }

    public static final class EstadoAsesor {

        private EstadoAsesor() {}

        public static final String NO_ENCONTRADO = "ESTADO_ASESOR_NO_ENCONTRADO";
    }
}
