# Bitácora de asistencia con IA

## Herramientas usadas

1. Codex

   - Marca: Codex / OpenAI
   - Modelo exacto:  GPT-6.1 Sol

## Resumen de uso

Usé IA para revisar el enunciado y la rúbrica de POO-06, comparar los requisitos
con la estructura que ya tenía mi proyecto de Minecraft y detectar qué faltaba.

Con esa ayuda reorganicé el dominio en `py.edu.uc.lp3.domain`, los controllers
en `py.edu.uc.lp3.rest.controller` y Application en `py.edu.uc.lp3`, actualizando
los packages e imports sin perder funcionalidad.

También agregué el constructor sobrecargado de Zombie y la sobrecarga de
`Entidad.mover`. Revisé la sobreescritura que ya existía de
`describirComportamiento()` en Zombie y Creeper y cómo el controller las usa
como Entidad. Después actualicé el README y el Mermaid según el código final.

Usé IA para detectar y corregir los problemas de configuración de Java 21 y
del Maven Wrapper. Verifiqué compilación, tests, arranque y endpoints REST,
incluyendo que una velocidad negativa devuelva HTTP 400 y que la validación
siga en el dominio.

## Prompts resumidos

- "Revisar el proyecto contra el enunciado POO-06 y detectar qué faltaba."
- "Reorganizar los paquetes según el template sin perder funcionalidad."
- "Agregar constructores y métodos sobrecargados manteniendo objetos válidos."
- "Verificar sobreescritura y polimorfismo con Entidad, Zombie y Creeper."
- "Corregir la respuesta REST para valores inválidos sin duplicar la validación."
- "Probar Maven Wrapper, Java 21, tests y endpoints REST."
- "Actualizar README y Mermaid según el código final."

Nota: revisé las decisiones y las verificaciones finales antes de versionar
los cambios. La IA fue una ayuda para el trabajo; la revisión final quedó a mi cargo.
