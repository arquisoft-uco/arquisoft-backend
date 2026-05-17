# Despliegue — Grafana Alloy (Coolify)

> Guía paso a paso para desplegar Alloy en **Server 1** (servidor del backend).
> Prerequisito: el stack de observabilidad (Loki, Prometheus, Grafana) debe estar
> activo en Server 2. Ver [OBSERVABILIDAD_COOLIFY.md](OBSERVABILIDAD_COOLIFY.md).

---

## 1. Label en el backend

- En Coolify → app del backend → **Configuration → Labels (Custom Labels)**, agregar:
  ```
  monitoring=arquisoft-backend
  ```
  Si el campo no es editable, asegurarse de que **Readonly labels** esté desmarcado.
- Hacer **Redeploy** del backend para que el label quede aplicado. Sin este paso Alloy no capturará ningún log.

## 2. Copiar `config.alloy` al servidor

Alloy lee la configuración desde una ruta fija del host. Debe copiarse antes del primer deploy y cada vez que el archivo cambie.

```bash
ssh root@<SERVER1_IP> 'mkdir -p /opt/alloy'
scp infra/coolify/config.alloy root@<SERVER1_IP>:/opt/alloy/config.alloy
```

## 3. Crear el recurso en Coolify

- Buscar y seleccionar el recurso **Docker Compose Empty**.
- En la vista **Create a new Service**, copiar y pegar el contenido de [`docker-compose.alloy.yml`](../infra/coolify/docker-compose.alloy.yml). En este paso también se pueden editar las variables de entorno directamente en el compose.
- En **Configuration → General**, usar el nombre personalizado `alloy` y guardar.
- En **Configuration → Environment Variables**, configurar las variables si no se hizo en el paso anterior:

| Variable | Valor |
|---|---|
| `LOKI_URL` | `http://<SERVER2_PRIVATE_IP>:3100/loki/api/v1/push` |
| `PROMETHEUS_URL` | `http://<SERVER2_PRIVATE_IP>:9090/api/v1/write` |

> Reemplazar `<SERVER2_PRIVATE_IP>` con la IP privada de Server 2. El puerto 3100 debe estar
> permitido en el firewall de Server 2 para la IP de Server 1; ídem para el 9090.

## 4. Desplegar

- Clic en **Deploy**, esperar y confirmar que el estado sea `Running (healthy)`.
- Después del despliegue se pueden asignar límites de recursos y configurar tuning personalizado para producción.

## 5. Verificar

Confirmar en Grafana con la query:
```logql
{container="arquisoft-backend", job="backend-java-coolify"}
```
