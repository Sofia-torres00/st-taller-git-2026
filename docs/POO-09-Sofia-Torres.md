# POO-09 — Sofia Torres

## Integrantes y round robin

| Rol | Alumno | Repositorio | Base URL | OpenAPI |
| --- | --- | --- | --- | --- |
| A1 | Sofi | [Sofi](https://github.com/Sofia-torres00/st-taller-git-2026) | http://100.79.75.27:8081 | [OpenAPI](http://100.79.75.27:8081/v3/api-docs) |
| A2 | Nico | [Nico](https://github.com/NicoRodriguez85677/NRodriguez-taller-git-2026) | http://100.83.179.97:8080 | [OpenAPI](http://100.83.179.97:8080/v3/api-docs) |
| A3 | Amir | [Amir](https://github.com/AmirDujak/adb-taller-git-2026) | http://100.78.121.31:8080 | [OpenAPI](http://100.78.121.31:8080/v3/api-docs) |

Cada alumno es A1 en su propia entrega. El round robin esta completo
documentalmente: Amir documenta el consumo de la validacion de Sofi como su A2.
Al cierre del 23/11/2026 la demostracion coordinada en vivo estaba pendiente.
La sesion del 24/11/2026, registrada mas abajo, cierra la demostracion grupal
con los nueve casos manuales confirmados por los tres integrantes.

## Escenario implementado

`POST /api/demoliciones` consulta primero a Nico para comprobar que el explosivo
de respaldo tenga `puedeExplotar=true`. Luego solicita a Amir la explosion de
un Creeper ejecutor independiente. Solo con ambas aceptaciones y una respuesta
confirmable de Amir, Sofi guarda la demolicion y responde 201.

## APIs consumidas

- Nico: `GET /api/minecraft/explosivos/{id}`, consulta sin detonacion.
- Amir: `POST /api/lp3/explosiones/simular`, aplica efectos reales en su H2.

## API que Sofi ofrece al grupo

`POST /api/explosiones/validar`, consumida por Amir, comprueba tres UUID
distintos de un Creeper, un Jugador y un Cerdo existentes, sus tipos y que el
Creeper este vivo. No aplica dano, no modifica H2 ni consume al Creeper.

## Persistencia

Solo una operacion aceptada se guarda en la H2 de Sofi: UUID propio, IDs remotos,
fecha y resultado de Amir. Los rechazos A2/Nico y A3/Amir se identifican en la
respuesta y no agregan registros locales. A1 garantiza que su propia H2 no cambia
ante un rechazo. Una operacion remota ya ejecutada no puede deshacerse mediante
la transaccion local de A1.

## Pruebas

Resultado automatizado anterior, registrado en el cierre del 23/11/2026;
no se repitieron pruebas para este ajuste documental:

```text
160 tests
0 failures
0 errors
0 skipped
BUILD SUCCESS
```

### Sesion manual del 24/11/2026

**Demostracion grupal cerrada:** los tres integrantes confirmaron la realizacion
exitosa de los nueve casos de integracion manual en vivo, con los tres
servidores conectados mediante Tailscale. Registro documental del 25/11/2026:

- Sofia como A1: caso exitoso, rechazo A2 (Nico) y rechazo A3 (Amir) realizados
  correctamente.
- Nicolas como A1: caso exitoso, rechazo A2 (Amir) y rechazo A3 (Sofia)
  realizados correctamente.
- Amir como A1: caso exitoso, rechazo A2 (Sofia) y rechazo A3 (Nico) realizados
  correctamente.

En las pruebas de Nicolas aparecio inicialmente un HTTP 503. Segun los
participantes, se comprobo mediante curl que ambas APIs remotas respondian
correctamente y la integracion funciono posteriormente. No se atribuye una
causa al 503.

Esta prueba manual es independiente de las 160 pruebas automatizadas y de los
ejemplos offline con servicios simulados. No se aportaron capturas, logs,
respuestas HTTP completas, hashes de los procesos ejecutados ni resultados
SQL de la sesion. El cierre se registra como confirmacion manual de los
participantes, sin atribuir verificaciones adicionales.

## Insomnia

Archivo: [docs/insomnia/sofi-poo09.yaml](insomnia/sofi-poo09.yaml).
Importacion manual verificada correctamente, segun confirmacion de la autora.
Incluye caso feliz, consulta posterior y rechazos A2/A3 con sus consultas posteriores.

## Commits de referencia del grupo

- Sofi, acuerdo original: [06a6fb297a5e9f9186d23b6dee7086fdaba94e30](https://github.com/Sofia-torres00/st-taller-git-2026/commit/06a6fb297a5e9f9186d23b6dee7086fdaba94e30).
- Sofi, commit documental intermedio: [269ff2081b70ed7bd9f02e4514ff61e0e6b04de5](https://github.com/Sofia-torres00/st-taller-git-2026/commit/269ff2081b70ed7bd9f02e4514ff61e0e6b04de5).
- Nico: [e9db16a93c925928734932e2759459a08747a1dd](https://github.com/NicoRodriguez85677/NRodriguez-taller-git-2026/commit/e9db16a93c925928734932e2759459a08747a1dd).
- Amir: [a6e6b14a3626f1cf6cfa48547fb4b1d5596a03fb](https://github.com/AmirDujak/adb-taller-git-2026/commit/a6e6b14a3626f1cf6cfa48547fb4b1d5596a03fb).

Son referencias publicadas/acordadas; no se afirma que estos commits ajenos
fueran clonados y ejecutados durante la revision ni que identifiquen los
procesos de la sesion del 24/11/2026.

## Entrega final

Commit final entregado de Sofia:
[7915cb50709770ef9eb6f01dd8880679e68f7c41](https://github.com/Sofia-torres00/st-taller-git-2026/commit/7915cb50709770ef9eb6f01dd8880679e68f7c41).
Se distingue del acuerdo original y del commit intermedio indicados arriba.
Esta actualizacion documental es posterior a esa entrega.
