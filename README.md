# [JEP 507](https://openjdk.org/jeps/507) — Patrones con tipos primitivos en Java 25 (instanceof & switch)

> **Estado**: _Preview_ en Java 25 · **Objetivo**: Tratar **primitivos** y **referencias** de forma uniforme en **patrones** (`instanceof`) y **switch**, mejorando legibilidad, seguridad de tipos y exhaustividad.

---

## 🧠 Conceptos Clave

- **Patrones de tipos primitivos**: ahora puedes hacer `o instanceof int i` o `case int i` en `switch`, **vinculando** directamente una variable primitiva (`i`, `l`, `d`, etc.).
- **Conversión al coincidir (matching)**: el valor se **desenvuelve** (unboxing) si es wrapper y puede aplicar **ampliación** (widening) primitiva. **Nunca** hay **reducción** (narrowing).
- **Dominancia de patrones**: coloca **patrones más específicos** primero (p. ej., `int` antes de `long`, `long` antes de `double`) para evitar sombras o casos inalcanzables.
- **`null`**: `instanceof` nunca coincide con `null`; en `switch` usa **`case null`** si puede aparecer.
- **Exhaustividad (`switch`)**: el `switch` debe ser **total**. Incluye `default` o un **patrón final sin guards** que cubra el dominio, y considera `case null` en selectores de referencia.
- **Punto flotante**: los patrones `double` y `float` capturan **todos** sus valores (incl. `NaN` y `±0.0`). Usa **guards** para tratarlos de forma especial.
- **Guards (cláusulas `when`)**: condiciones booleanas anexas a un patrón que se **evalúan después** de enlazar el patrón. Si la guards **falla**, el matching **continúa** con el siguiente patrón. Las guards no introducen nuevas variables; lo ideal es que sean **expresiones puras**.

---

## ✅ Permitido vs ❌ Prohibido (conversiones al coincidir)

- ✅ **Unboxing + Widening** (ejemplos típicos):  
  - `Byte/Short/Character → int → long → float → double`  
  - `Integer → long/float/double`  
  - `Long → float/double`  
  - `Float → double`
- ❌ **Narrowing**: `Long → int`, `Double → float`, etc.  
- ❌ **Cruces booleano↔numérico**: `boolean` no se convierte a numérico ni viceversa.

> **Tip**: por este orden natural de ampliación, usa `int` **antes** de `long` y `long` **antes** de `double`. Para `char`, pon `case char c` **antes** de `case int i`.

---

## 🔀 `instanceof` con patrones primitivos

```java
Object o = 42; // Integer
if (o instanceof int i && i > 10) {          // Enlaza y guards
    System.out.println("int grande: " + i);
} else if (o instanceof long l) {            // Integer también podría ampliar a long,
    System.out.println("long: " + l);        // por eso va después de int
} else if (o instanceof double d) {
    System.out.println("double: " + d);
} else if (o == null) {
    System.out.println("null");
} else {
    System.out.println("otro: " + o);
}
```

**Notas**  
- `null` nunca coincide.  
- Flujo de matching: **unboxing** → posible **widening** → evaluar **guard** (si existe).

---

## 🔁 `switch` con patrones primitivos (y `null`)

```java
String describe(Object o) {
    return switch (o) {
        case null                     -> "null";
        case int i   when i % 2 == 0  -> "par";
        case int i                    -> "impar";
        case long l                   -> "long " + l;
        case double d                 -> "double " + d;
        default                       -> "otro";
    };
}
```

**Notas**  
- Ordena por **especificidad** para evitar dominancia accidental.  
- Con `Object` como selector, `case null` es explícito y claro.  
- Guards (`when`) se evalúa **después** del enlace del patrón.

---

## 🎯 Guards (`when`): concepto y buenas prácticas

- **Qué son**: filtros booleanos que **refinan** un patrón ya enlazado.  
- **Evaluación**: _después_ de que el patrón coincida y **antes** de aplicar la rama.  
- **Fallo de guards**: **no** ejecuta la rama; la evaluación continúa con el siguiente caso.  
- **No crean bindings**: solo usan variables ya enlazadas en el patrón.  
- **Preferir pureza**: evita efectos secundarios (por legibilidad y previsibilidad).

```java
String signo(Object o) {
    return switch (o) {
        case int i when i > 0  -> "positivo";
        case int i when i == 0 -> "cero";
        case int i             -> "negativo"; // catch‑all para int
        default                -> "otro";
    };
}
```

---

## 🔬 Casos especiales de punto flotante

```java
String fp(Object o) {
    return switch (o) {
        case double d when Double.isNaN(d)      -> "NaN";
        case double d when isNegZero(d)         -> "−0.0";
        case double d when d == 0.0             -> "+0.0";
        case double d                           -> "double " + d;
        default                                 -> "otro";
    };
}
static boolean isNegZero(double d) {
    return Double.doubleToRawLongBits(d) == Double.doubleToRawLongBits(-0.0d);
}
```

---

## 🧭 Reglas de orden/dominancia (resumen)

- `char` **antes** de `int`; `int` **antes** de `long`; `long` **antes** de `double`.  
- Patrones **con guards** antes que su **variante sin guards** del mismo tipo.  
- Evita patrones inalcanzables (el compilador puede avisar).

---

## 📏 Exhaustividad en `switch`

- Asegura cobertura total: `default` o **patrón final sin guards** que cubra el dominio.  
- Para selectores de referencia: considera `case null`.  
- Para `boolean`: cubre `true` y `false` o usa `default`.

---

## 🧪 Comparativa rápida “Antes vs Ahora”

| Problema | Antes  | Ahora (Java 25, preview) |
|---|---|---|
| Testear y usar un entero desde `Object` | `o instanceof Integer i` + (auto)unboxing | `o instanceof int i` |
| Clasificar varios numéricos | Cadena de `if/else` + casts | `switch (o)` con `case int/long/double` |
| Tratamiento de `null` | Chequeo manual | `case null` en `switch` |
| Ramas por rango/paridad | `if (i > 0)`… | Guards: `case int i when i > 0` |

---

## ⚙️ Compilar y ejecutar (preview)

```bash
# Archivo único
javac --release 25 --enable-preview Jep507Demo.java
java  --enable-preview Jep507Demo
```

---

## 📝 Consejos de estilo

- Prefiere **patrones primitivos** sobre wrappers cuando el dato es numérico.  
- Usa **guards** para expresar intención donde se necesita; evita lógica extensa en la guards.  
- Ordena casos por **especificidad** y añade un **catch‑all** claro.
