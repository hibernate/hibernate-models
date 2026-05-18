# Hibernate Models Accessor

## What is it?

A library that provides efficient field/property access and instance creation for Hibernate Models.

It provides a single API (`HibernateAccessorFactory`) with pluggable strategies for reading
and writing object state and for instantiating objects via constructors.

## Getting started

Add the core module to your project:

```xml
<dependency>
    <groupId>org.hibernate.models</groupId>
    <artifactId>hibernate-models-accessor</artifactId>
    <version>${hibernate-models.version}</version>
</dependency>
```

For the ASM-based strategy, add the ASM module instead (it transitively includes the core):

```xml
<dependency>
    <groupId>org.hibernate.models</groupId>
    <artifactId>hibernate-models-accessor-asm</artifactId>
    <version>${hibernate-models.version}</version>
</dependency>
```

## Usage

### Obtaining a factory

Three built-in strategies are available:

```java
// Reflection-based (simplest, no extra setup)
HibernateAccessorFactory factory = HibernateAccessorFactory.reflection();

// Lambda-based (better performance, requires a MethodHandles.Lookup)
HibernateAccessorFactory factory = HibernateAccessorFactory.lambda(MethodHandles.lookup());

// ASM-based (generates one bulk accessor class per entity with switch-based dispatch)
HibernateAccessorFactory factory = HibernateAccessorAsmFactory.factory(MethodHandles.lookup());
```

### Reading and writing fields

```java
Field nameField = Person.class.getDeclaredField("name");

HibernateAccessorValueReader<?> reader = factory.valueReader(nameField);
HibernateAccessorValueWriter writer = factory.valueWriter(nameField);

Person person = new Person();
writer.set(person, "Alice");

Object name = reader.get(person);  // "Alice"
```

### Reading and writing via getter/setter methods

```java
Method getter = Person.class.getDeclaredMethod("getName");
Method setter = Person.class.getDeclaredMethod("setName", String.class);

HibernateAccessorValueReader<?> reader = factory.valueReader(getter);
HibernateAccessorValueWriter writer = factory.valueWriter(setter);

writer.set(person, "Bob");
Object name = reader.get(person);  // "Bob"
```

### Instantiating objects

```java
Constructor<Person> ctor = Person.class.getDeclaredConstructor();
HibernateAccessorInstantiator<Person> instantiator = factory.instantiator(ctor);

Person person = instantiator.create();
```

For constructors that take arguments:

```java
Constructor<Person> ctor = Person.class.getDeclaredConstructor(String.class, int.class);
HibernateAccessorInstantiator<Person> instantiator = factory.instantiator(ctor);

Person person = instantiator.create("Alice", 30);
```

## Strategies

| Strategy | Factory method | Mechanism | Notes |
|---|---|---|---|
| Reflection | `HibernateAccessorFactory.reflection()` | `java.lang.reflect` | Simplest. Shared singleton instance. |
| Lambda | `HibernateAccessorFactory.lambda(lookup)` | `LambdaMetafactory` | Better throughput after warm-up. Requires a `MethodHandles.Lookup` with appropriate access. |
| ASM | `HibernateAccessorAsmFactory.factory(lookup)` | ASM bytecode generation | Generates one class per entity with `TABLESWITCH` dispatch on field/method index. Requires a `MethodHandles.Lookup` and a dependency on `hibernate-models-accessor-asm` (which brings `org.ow2.asm:asm`). |
