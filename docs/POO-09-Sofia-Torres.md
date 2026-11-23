# POO-09 — Sofia Torres

## Integrantes y round robin

| Rol | Alumno | Repositorio | Base URL | OpenAPI |
| --- | --- | --- | --- | --- |
| A1 | Sofi | [Sofi](https://github.com/Sofia-torres00/st-taller-git-2026) | http://100.79.75.27:8081 | [OpenAPI](http://100.79.75.27:8081/v3/api-docs) |
| A2 | Nico | [Nico](https://github.com/NicoRodriguez85677/NRodriguez-taller-git-2026) | http://100.83.179.97:8080 | [OpenAPI](http://100.83.179.97:8080/v3/api-docs) |
| A3 | Amir | [Amir](https://github.com/AmirDujak/adb-taller-git-2026) | http://100.78.121.31:8080 | [OpenAPI](http://100.78.121.31:8080/v3/api-docs) |

Cada alumno es A1 en su propia entrega. El round robin esta completo
documentalmente: Amir documenta el consumo de la validacion de Sofi como su A2.
La demostracion coordinada del trio en vivo no se registra como realizada;
es evidencia adicional pendiente y requiere los servicios Tailscale encendidos.

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

Resultado tecnico confirmado; no se repitieron pruebas para este ajuste documental:

```text
160 tests
0 failures
0 errors
0 skipped
BUILD SUCCESS
```

## Insomnia

Archivo: [docs/insomnia/sofi-poo09.yaml](insomnia/sofi-poo09.yaml).
Importacion manual verificada correctamente, segun confirmacion de la autora.
Incluye caso feliz, consulta posterior y rechazos A2/A3 con sus consultas posteriores.

## Commits de referencia del grupo

- Sofi: [269ff2081b70ed7bd9f02e4514ff61e0e6b04de5](https://github.com/Sofia-torres00/st-taller-git-2026/commit/269ff2081b70ed7bd9f02e4514ff61e0e6b04de5), commit publicado anterior a este ultimo ajuste documental.
- Nico: [e9db16a93c925928734932e2759459a08747a1dd](https://github.com/NicoRodriguez85677/NRodriguez-taller-git-2026/commit/e9db16a93c925928734932e2759459a08747a1dd).
- Amir: [a6e6b14a3626f1cf6cfa48547fb4b1d5596a03fb](https://github.com/AmirDujak/adb-taller-git-2026/commit/a6e6b14a3626f1cf6cfa48547fb4b1d5596a03fb).

Son referencias publicadas/acordadas; no se afirma que estos commits ajenos
fueran clonados y ejecutados durante la revision.

## Entrega final

El hash exacto del commit final se consigna en Classroom junto con este documento.
