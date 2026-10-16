package com.arquisoft.shared.message.constant;

public final class RepositorioArtefactosFields {

    private RepositorioArtefactosFields() {}

    public static final class Administrador {

        private Administrador() {}

        public static final String ID = "id";
        public static final String IDENTIFICADOR = "identificador";
        public static final String NOMBRE = "nombre";
        public static final String EMAIL = "email";
        public static final String OCURRIDO_EN = "ocurridoEn";
    }

    public static final class RepositorioArtefacto {

        private RepositorioArtefacto() {}

        public static final String NOMBRE_REPOSITORIO = "nombreRepositorio";
        public static final String DESCRIPCION = "descripcion";
        public static final String ADMINISTRADOR = "administrador";
    }
}
