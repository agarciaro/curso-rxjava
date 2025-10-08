# Tema 9: Pruebas y Depuración en RxJava

Este tema cubre las técnicas esenciales para testing y debugging en programación reactiva con RxJava.

## 📚 Ejemplos Incluidos

### 1. Ejemplo01_TestObserver.java
**Conceptos:** TestObserver básico, validaciones, predicados
- Test básico con TestObserver
- Test con transformaciones (map, filter)
- Test con filtrado
- Test con error
- Test con TestScheduler (operaciones temporales)
- Test con predicados personalizados

### 2. Ejemplo02_TestScheduler.java
**Conceptos:** Control de tiempo virtual, operadores temporales
- Test básico con TestScheduler
- Test con delay
- Test con timer
- Test con timeout
- Test con debounce
- Test con throttle
- Test con sample
- Test con retry y delay
- Test con buffer temporal
- Test con window temporal

### 3. Ejemplo03_Debugging.java
**Conceptos:** Operadores de debugging, logging, monitoreo
- Debugging básico con doOnNext, doOnError, doOnComplete
- Debugging con operadores de transformación
- Debugging con operadores temporales
- Debugging con manejo de errores
- Debugging con operadores de combinación
- Debugging con operadores de reducción
- Debugging con operadores de agrupación
- Debugging con operadores de buffer
- Debugging con operadores de window
- Debugging con métricas personalizadas

### 4. Ejemplo04_TestObserverAvanzado.java
**Conceptos:** Técnicas avanzadas de testing
- Test con predicados personalizados
- Test con validaciones de orden
- Test con validaciones de terminación
- Test con validaciones de subscripción
- Test con validaciones de threading
- Test con validaciones de timeout
- Test con validaciones de backpressure
- Test con validaciones de operadores complejos
- Test con validaciones de error handling
- Test con validaciones de métricas
- Test con validaciones de combinación
- Test con validaciones de reducción

### 5. Ejemplo05_TestingIntegracion.java
**Conceptos:** Testing de sistemas completos
- Test de sistema de notificaciones
- Test de sistema de caché con expiración
- Test de sistema de retry con backoff exponencial
- Test de sistema de rate limiting
- Test de sistema de circuit breaker
- Test de sistema de batch processing
- Test de sistema de load balancing
- Test de sistema de monitoreo
- Test de sistema de autenticación
- Test de sistema de logging

### 6. Ejemplo06_TestingPerformance.java
**Conceptos:** Medición de rendimiento
- Test de throughput básico
- Test de performance con operadores
- Test de performance con threading
- Test de performance con backpressure
- Test de performance con cache
- Test de performance con operadores de tiempo
- Test de performance con operadores de combinación
- Test de performance con operadores de reducción
- Test de performance con operadores de agrupación
- Test de performance con operadores de buffer
- Test de performance con operadores de window
- Test de performance con operadores de flatMap
- Test de performance con operadores de merge
- Test de performance con operadores de concat
- Test de performance con operadores de switchMap

### 7. Ejemplo07_TestingErrores.java
**Conceptos:** Manejo de errores en testing
- Test de error básico
- Test de error con onErrorResumeNext
- Test de error con onErrorReturn
- Test de error con retry
- Test de error con retryWhen
- Test de error con timeout
- Test de error con catch
- Test de error con doOnError
- Test de error con finally
- Test de error con múltiples errores
- Test de error con error específico
- Test de error con error personalizado
- Test de error con error en operador
- Test de error con error en flatMap
- Test de error con error en subscribe

### 8. Ejemplo08_TestingOperadores.java
**Conceptos:** Testing de operadores específicos
- Test de operador map
- Test de operador filter
- Test de operador take
- Test de operador skip
- Test de operador distinct
- Test de operador distinctUntilChanged
- Test de operador takeWhile
- Test de operador skipWhile
- Test de operador takeUntil
- Test de operador skipUntil
- Test de operador buffer
- Test de operador window
- Test de operador groupBy
- Test de operador scan
- Test de operador reduce
- Test de operador collect
- Test de operador toList
- Test de operador toMap
- Test de operador toMultimap
- Test de operador toSortedList
- Test de operador delay
- Test de operador debounce
- Test de operador throttleFirst
- Test de operador throttleLast
- Test de operador sample

### 9. Ejemplo09_TestingCombinacion.java
**Conceptos:** Testing de operadores de combinación
- Test de operador merge
- Test de operador mergeWith
- Test de operador zip
- Test de operador zipWith
- Test de operador combineLatest
- Test de operador concat
- Test de operador concatWith
- Test de operador startWith
- Test de operador switchMap
- Test de operador flatMap
- Test de operador concatMap
- Test de operador amb
- Test de operador ambWith
- Test de operador withLatestFrom
- Test de operador join
- Test de operador groupJoin
- Test de operador mergeDelayError
- Test de operador concatDelayError
- Test de operador switchOnNext
- Test de combineLatest con múltiples fuentes

### 10. Ejemplo10_TestingCompleto.java
**Conceptos:** Testing de sistemas completos
- Test de sistema de procesamiento de datos
- Test de sistema con manejo de errores
- Test de sistema con retry
- Test de sistema con timeout
- Test de sistema con cache
- Test de sistema con métricas
- Test de sistema con backpressure
- Test de sistema con operadores complejos
- Test de sistema con combinación
- Test de sistema con reducción

## 🎯 Conceptos Clave

### TestObserver
- **assertValues()**: Verificar valores emitidos
- **assertValueCount()**: Verificar cantidad de valores
- **assertComplete()**: Verificar completado
- **assertError()**: Verificar error
- **assertErrorMessage()**: Verificar mensaje de error
- **assertTerminated()**: Verificar terminación
- **assertSubscribed()**: Verificar suscripción
- **assertValueAt()**: Verificar valor en posición específica
- **assertValueSequence()**: Verificar secuencia de valores

### TestScheduler
- **advanceTimeBy()**: Avanzar tiempo virtual
- **advanceTimeTo()**: Ir a tiempo específico
- **createWorker()**: Crear worker para scheduling
- **triggerActions()**: Ejecutar acciones pendientes

### Operadores de Debugging
- **doOnNext()**: Ejecutar código por cada elemento
- **doOnError()**: Ejecutar código en caso de error
- **doOnComplete()**: Ejecutar código al completar
- **doOnSubscribe()**: Ejecutar código al suscribirse
- **doOnDispose()**: Ejecutar código al limpiar
- **doFinally()**: Ejecutar código al finalizar
- **doOnTerminate()**: Ejecutar código al terminar
- **doOnCancel()**: Ejecutar código al cancelar

### Técnicas de Testing
- **Testing de operadores**: Validar comportamiento de operadores
- **Testing de errores**: Validar manejo de errores
- **Testing de performance**: Medir rendimiento
- **Testing de integración**: Validar sistemas completos
- **Testing de combinación**: Validar operadores de combinación
- **Testing temporal**: Validar operadores temporales
- **Testing de backpressure**: Validar control de flujo
- **Testing de cache**: Validar funcionalidad de cache

## 🚀 Ejecución

Para ejecutar los ejemplos:

```bash
# Compilar
javac -cp "lib/*" Ejemplo01_TestObserver.java

# Ejecutar
java -cp ".:lib/*" Ejemplo01_TestObserver
```

## 📖 Recursos Adicionales

- [RxJava Testing Documentation](https://github.com/ReactiveX/RxJava/wiki/Testing)
- [TestObserver API](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/observers/TestObserver.html)
- [TestScheduler API](http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/schedulers/TestScheduler.html)
- [Testing Best Practices](https://github.com/ReactiveX/RxJava/wiki/Testing-Best-Practices)

## 🎓 Objetivos de Aprendizaje

Al completar este tema, serás capaz de:

1. **Usar TestObserver** para validar comportamientos de Observables
2. **Controlar tiempo virtual** con TestScheduler
3. **Debuggear aplicaciones reactivas** con operadores de debugging
4. **Testear operadores específicos** de RxJava
5. **Validar manejo de errores** en flujos reactivos
6. **Medir performance** de operaciones reactivas
7. **Testear sistemas completos** con múltiples operadores
8. **Validar operadores de combinación** entre múltiples fuentes
9. **Implementar testing de integración** para sistemas reales
10. **Aplicar mejores prácticas** de testing en RxJava

---

**¡Dominar el testing y debugging es esencial para desarrollar aplicaciones reactivas robustas y confiables!** 🚀
