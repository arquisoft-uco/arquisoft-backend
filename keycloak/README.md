# Aprovisionamiento del realm

`realm-arquisoft.json` es un export del realm `arquisoft`. Vive aquí, y no dentro de un módulo,
porque no pertenece a ningún bounded context: describe la infraestructura de identidad que
`seguridad` y `usuarios` consumen por igual. Mismo criterio que `catalogo/` y `plantillas/`.

## Cómo se aplica

`docker-compose.yml` lo monta en `/opt/keycloak/data/import` y arranca Keycloak con
`--import-realm`. **La importación solo ocurre si el realm no existe todavía** en `KC_DB`: sobre un
Keycloak que ya tiene el realm, el archivo se ignora en silencio. Para reimportar de cero:

```bash
docker-compose down -v          # borra el volumen de postgres, con el realm dentro
docker-compose up keycloak
```

## Cómo se regenera

Consola de Keycloak → Realm settings → Action → **Partial export**, marcando *groups and roles* y
*clients*. El export **no incluye** los secretos de cliente (salen enmascarados) ni los usuarios, y
así debe quedar: el secreto vive en `.env-*.properties` y nunca en el repo.

## Lo que el realm debe garantizar

- Realm roles en **kebab-case** (`estudiante`, `asesor-ficha`, `representante-comite`, …), tal como
  los declara `UsuariosRealmRoles`. Keycloak distingue mayúsculas.
- El cliente `arquisoft-api` con *service accounts* habilitado y, del cliente `realm-management`,
  los roles `manage-users`, `view-realm` y `view-users` — los tres que necesita el registro de
  usuarios (`KeycloakProveedorIdentidadOutputAdapter`).
- SMTP configurado, o `execute-actions-email` falla al final del registro.
