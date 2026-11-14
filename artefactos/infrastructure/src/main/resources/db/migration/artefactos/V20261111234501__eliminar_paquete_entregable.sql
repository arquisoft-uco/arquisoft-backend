-- HT-023 — El paquete .zip del entregable lo arma y registra el contexto entregables con su propia réplica de los artefactos cerrados.
-- paquete_entregable deja de existir en artefactos; sin backfill: el proyecto no se ha desplegado, los datos locales se ajustan a mano.
DROP TABLE paquete_entregable;
