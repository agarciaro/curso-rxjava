# Ejercicios Tema 5: Combinando Observables

## Ejercicio 5.1: Merge - Sistema de Notificaciones

**Nivel:** Intermedio  
**Tiempo estimado:** 30 minutos

### Enunciado
Implementa un sistema de notificaciones que combine tres fuentes:
- Notificaciones push (cada 500ms)
- Notificaciones de email (cada 800ms)
- Notificaciones SMS (cada 1200ms)

Usa `merge()` para combinar todas las notificaciones y mostrarlas en tiempo real.

### Requisitos
- Cada tipo de notificación debe tener un identificador
- Mostrar timestamp de cada notificación
- Limitar a las primeras 10 notificaciones totales
- Implementar logging de cada notificación recibida

### Resultado Esperado
```
[00:00.500] PUSH: Nueva actualización disponible
[00:00.800] EMAIL: Tienes 3 mensajes nuevos
[00:01.000] PUSH: Recordatorio de reunión
[00:01.200] SMS: Código de verificación: 123456
[00:01.500] PUSH: Ana te ha mencionado
[00:01.600] EMAIL: Newsletter semanal
...
```

---

## Ejercicio 5.2: Concat - Pipeline de Procesamiento

**Nivel:** Intermedio  
**Tiempo estimado:** 25 minutos

### Enunciado
Crea un pipeline de procesamiento de datos que ejecute secuencialmente:
1. Validación de datos
2. Transformación de formato
3. Enriquecimiento con datos externos
4. Guardado en base de datos

Cada fase debe ser un Observable que complete antes de iniciar la siguiente.

### Requisitos
- Usar `concat()` para garantizar orden secuencial
- Cada fase debe emitir un mensaje de progreso
- Si una fase falla, el pipeline completo debe detenerse
- Implementar logging de tiempo de cada fase

### Resultado Esperado
```
[Fase 1/4] Validando datos... ✓ (120ms)
[Fase 2/4] Transformando formato... ✓ (80ms)
[Fase 3/4] Enriqueciendo datos... ✓ (200ms)
[Fase 4/4] Guardando en BD... ✓ (150ms)

Pipeline completado en 550ms
```

---

## Ejercicio 5.3: Zip - Combinar APIs

**Nivel:** Intermedio  
**Tiempo estimado:** 35 minutos

### Enunciado
Implementa un dashboard que combine información de 3 APIs diferentes:
- API de Usuario (nombre, email, foto)
- API de Estadísticas (visitas, likes, shares)
- API de Preferencias (tema, idioma, notificaciones)

Usa `zip()` para combinar los resultados y crear un objeto Dashboard completo.

### Requisitos
- Simular latencia de red (100-300ms por API)
- Manejar errores de cada API individualmente
- Si una API falla, usar valores por defecto
- Mostrar tiempo total de carga

### Resultado Esperado
```
Cargando Dashboard...
  ↻ Obteniendo datos de usuario...
  ↻ Obteniendo estadísticas...
  ↻ Obteniendo preferencias...

Dashboard cargado en 280ms:
┌─────────────────────────────────┐
│ Usuario: Juan Pérez             │
│ Email: juan@example.com         │
│ Visitas: 1,234                  │
│ Likes: 567                      │
│ Tema: Dark                      │
│ Idioma: Español                 │
└─────────────────────────────────┘
```

---

## Ejercicio 5.4: CombineLatest - Formulario Reactivo

**Nivel:** Avanzado  
**Tiempo estimado:** 40 minutos

### Enunciado
Crea un formulario de registro reactivo que valide en tiempo real:
- Campo nombre (mínimo 3 caracteres)
- Campo email (formato válido)
- Campo contraseña (mínimo 8 caracteres, incluye número y mayúscula)
- Campo confirmar contraseña (debe coincidir)

Usa `combineLatest()` para validar el formulario completo y habilitar/deshabilitar el botón de envío.

### Requisitos
- Cada campo es un Observable que emite en cada cambio
- Validación en tiempo real
- Mostrar mensajes de error específicos
- El formulario solo es válido si todos los campos lo son
- Implementar debounce de 300ms en la validación

### Resultado Esperado
```
=== Formulario de Registro ===

Nombre: J
❌ El nombre debe tener al menos 3 caracteres

Nombre: Juan
✓ Nombre válido

Email: juan@
❌ Email inválido

Email: juan@example.com
✓ Email válido

Contraseña: abc123
❌ Debe incluir al menos una mayúscula

Contraseña: Abc12345
✓ Contraseña válida

Confirmar: Abc123
❌ Las contraseñas no coinciden

Confirmar: Abc12345
✓ Las contraseñas coinciden

[Botón Registrar: HABILITADO]
```

---

## Ejercicio 5.5: WithLatestFrom y Sample

**Nivel:** Avanzado  
**Tiempo estimado:** 35 minutos

### Enunciado
Implementa un sistema de auto-guardado para un editor de texto:
- El usuario escribe continuamente (simula con Observable)
- Solo guarda cuando han pasado 2 segundos sin escribir (debounce)
- Al guardar, combina el texto con metadata actual (timestamp, usuario, versión)
- Muestra indicador de "guardando" y "guardado"

### Requisitos
- Usar `debounce()` para detectar pausa en escritura
- Usar `withLatestFrom()` para obtener metadata en el momento del guardado
- Simular latencia de guardado (100-300ms)
- Mostrar versión incremental con cada guardado

### Resultado Esperado
```
Usuario escribe: "H"
Usuario escribe: "Ho"
Usuario escribe: "Hol"
Usuario escribe: "Hola"
Usuario escribe: "Hola m"
Usuario escribe: "Hola mundo"

[Pausa de 2 segundos detectada]

💾 Guardando...
   Texto: "Hola mundo"
   Usuario: Juan
   Timestamp: 2024-01-15 10:23:45
   Versión: 1

✓ Guardado completado (v1)

Usuario escribe: "Hola mundo!"
Usuario escribe: "Hola mundo! C"
Usuario escribe: "Hola mundo! Cómo"
Usuario escribe: "Hola mundo! Cómo estás"

[Pausa de 2 segundos detectada]

💾 Guardando...
   Texto: "Hola mundo! Cómo estás"
   Usuario: Juan
   Timestamp: 2024-01-15 10:24:12
   Versión: 2

✓ Guardado completado (v2)
```

---

## Soluciones de Referencia

Las soluciones de referencia están disponibles en:
- `src/main/java/com/formadoresit/rxjava/tema5/soluciones/`

## Evaluación

| Ejercicio | Puntos | Criterios |
|-----------|--------|-----------|
| 5.1 | 15 | Uso correcto de merge, timing, logging |
| 5.2 | 20 | Secuencialidad, manejo de errores |
| 5.3 | 25 | Combinación correcta, manejo de fallos |
| 5.4 | 25 | Validación reactiva, UX, debounce |
| 5.5 | 15 | Auto-guardado, metadata, versionado |
| **Total** | **100** | |

## Recursos Adicionales

- RxMarbles: https://rxmarbles.com/
- Documentación de operadores de combinación: http://reactivex.io/documentation/operators.html#combining
- Ejemplos en: `src/main/java/com/formadoresit/rxjava/tema5/`

