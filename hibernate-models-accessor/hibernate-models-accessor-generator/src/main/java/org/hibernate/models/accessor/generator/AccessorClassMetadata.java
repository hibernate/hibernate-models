/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.models.accessor.generator.runtime.NamingUtil;

public final class AccessorClassMetadata implements Comparable<AccessorClassMetadata> {

	public static final String BRIDGE_SUFFIX = "$$HibernateAccessorBridge";

	private final TypeMetadata type;
	private final Set<FieldMetadata> fields;
	private final Set<MethodMetadata> getters;
	private final Set<MethodMetadata> setters;
	private final Set<ConstructorMetadata> constructors;

	public AccessorClassMetadata(TypeMetadata type, Set<FieldMetadata> fields,
			Set<MethodMetadata> getters, Set<MethodMetadata> setters, Set<ConstructorMetadata> constructors) {
		this.type = type;
		this.fields = fields == null ? Set.of() : fields;
		this.getters = getters == null ? Set.of() : getters;
		this.setters = setters == null ? Set.of() : setters;
		this.constructors = constructors == null ? Set.of() : constructors;
	}

	public TypeMetadata getType() {
		return type;
	}

	public Set<FieldMetadata> getFields() {
		return fields;
	}

	public Set<MethodMetadata> getGetters() {
		return getters;
	}

	public Set<MethodMetadata> getSetters() {
		return setters;
	}

	public Set<ConstructorMetadata> getConstructors() {
		return constructors;
	}

	@Override
	public int compareTo(AccessorClassMetadata o) {
		return this.type.compareTo( o.type );
	}

	@Override
	public String toString() {
		return "AccessorClassMetadata{type=" + type + '}';
	}

	public static class Builder {
		private final String packageName;
		private final String type;
		private final String host;
		private final boolean hostIsPublic;
		private final boolean hostIsInterface;
		private final boolean record;
		private Set<FieldMetadata> fields;
		private Set<MethodMetadata> getters;
		private Set<MethodMetadata> setters;
		private Set<ConstructorMetadata> constructors;

		private Builder(Class<?> clazz) {
			Package pkg = clazz.getPackage();
			this.packageName = pkg != null ? pkg.getName() : "";
			this.type = clazz.getName();
			this.host = clazz.getName();
			this.hostIsPublic = Modifier.isPublic( clazz.getModifiers() );
			this.hostIsInterface = clazz.isInterface();
			this.record = clazz.isRecord();
		}

		public Builder(String packageName, String type, String host, boolean hostIsPublic, boolean hostIsInterface,
				boolean record) {
			this.packageName = packageName;
			this.type = type;
			this.host = host;
			this.hostIsPublic = hostIsPublic;
			this.hostIsInterface = hostIsInterface;
			this.record = record;
		}

		public static Builder forClass(Class<?> clazz) {
			return new Builder( clazz );
		}

		public Builder addField(Field field) {
			if ( this.fields == null ) {
				this.fields = new HashSet<>();
			}
			Class<?> fieldType = field.getType();
			String descriptor = org.objectweb.asm.Type.getDescriptor( fieldType );
			this.fields.add( new FieldMetadata(
					field.getName(),
					descriptor,
					fieldType.isPrimitive(),
					field.getDeclaringClass().getName(),
					record ) );
			return this;
		}

		public Builder addGetter(Method getter) {
			if ( this.getters == null ) {
				this.getters = new HashSet<>();
			}
			Class<?> returnType = getter.getReturnType();
			String descriptor = org.objectweb.asm.Type.getMethodDescriptor( getter );
			String returnDescriptor = org.objectweb.asm.Type.getDescriptor( returnType );
			this.getters.add( new MethodMetadata(
					getter.getName(),
					descriptor,
					returnType.isPrimitive(),
					getter.getDeclaringClass().getName(),
					getter.getDeclaringClass().isInterface(),
					returnDescriptor ) );
			return this;
		}

		public Builder addSetter(Method setter) {
			if ( this.setters == null ) {
				this.setters = new HashSet<>();
			}
			Class<?> paramType = setter.getParameterTypes()[0];
			String descriptor = org.objectweb.asm.Type.getMethodDescriptor( setter );
			String returnDescriptor = org.objectweb.asm.Type.getDescriptor( setter.getReturnType() );
			this.setters.add( new MethodMetadata(
					setter.getName(),
					descriptor,
					paramType.isPrimitive(),
					setter.getDeclaringClass().getName(),
					setter.getDeclaringClass().isInterface(),
					returnDescriptor ) );
			return this;
		}

		public <T> Builder addConstructor(Constructor<T> constructor) {
			if ( this.constructors == null ) {
				this.constructors = new HashSet<>();
			}
			String descriptor = org.objectweb.asm.Type.getConstructorDescriptor( constructor );
			List<ParameterMetadata> params = new ArrayList<>();
			Class<?>[] paramTypes = constructor.getParameterTypes();
			java.lang.reflect.Parameter[] parameters = constructor.getParameters();
			for ( int i = 0; i < paramTypes.length; i++ ) {
				params.add( new ParameterMetadata(
						parameters[i].getName(),
						org.objectweb.asm.Type.getDescriptor( paramTypes[i] ),
						paramTypes[i].isPrimitive() ) );
			}
			this.constructors.add( new ConstructorMetadata(
					constructor.getDeclaringClass().getName(),
					host,
					descriptor,
					params ) );
			return this;
		}

		public Builder addDefaultConstructor() {
			if ( this.constructors == null ) {
				this.constructors = new HashSet<>();
			}
			this.constructors.add( new ConstructorMetadata( type, host, "()V", List.of() ) );
			return this;
		}

		public Builder all(Class<?> classToAccess) {
			for ( Field field : classToAccess.getDeclaredFields() ) {
				if ( !Modifier.isStatic( field.getModifiers() ) ) {
					addField( field );
				}
			}

			for ( Method method : classToAccess.getDeclaredMethods() ) {
				if ( Modifier.isStatic( method.getModifiers() ) ) {
					continue;
				}
				if ( method.getParameterCount() == 0 && method.getReturnType() != void.class ) {
					addGetter( method );
				}
				if ( method.getParameterCount() == 1 ) {
					addSetter( method );
				}
			}

			if ( !Modifier.isAbstract( classToAccess.getModifiers() ) && !classToAccess.isEnum() ) {
				for ( Constructor<?> constructor : classToAccess.getDeclaredConstructors() ) {
					addConstructor( constructor );
				}
			}

			return this;
		}

		public AccessorClassMetadata build() {
			return new AccessorClassMetadata(
					new TypeMetadata( packageName, type, host, hostIsPublic, hostIsInterface ),
					fields, getters, setters, constructors );
		}
	}

	public interface MemberMetadata extends Comparable<MemberMetadata> {
		Comparator<MemberMetadata> COMPARATOR = Comparator.comparing( MemberMetadata::declaringClass,
						nullsFirst( naturalOrder() ) )
				.thenComparing( MemberMetadata::name, nullsFirst( naturalOrder() ) )
				.thenComparing( MemberMetadata::descriptor, nullsFirst( naturalOrder() ) );

		String name();

		String descriptor();

		boolean isPrimitive();

		String declaringClass();

		@Override
		default int compareTo(MemberMetadata o) {
			return MemberMetadata.COMPARATOR.compare( this, o );
		}
	}

	public record FieldMetadata(String name, String descriptor, boolean isPrimitive,
			String declaringClass, boolean readOnly) implements MemberMetadata {
	}

	public record MethodMetadata(String name, String descriptor, boolean isPrimitive,
			String declaringClass, boolean isInterface,
			String returnDescriptor) implements MemberMetadata {
	}

	public record ConstructorMetadata(String declaringClass, String host, String descriptor,
			List<ParameterMetadata> parameters) implements Comparable<ConstructorMetadata> {

		private static final Comparator<ConstructorMetadata> COMPARATOR = Comparator
				.comparing( ConstructorMetadata::declaringClass, nullsFirst( naturalOrder() ) )
				.thenComparing( ConstructorMetadata::descriptor, nullsFirst( naturalOrder() ) );

		@Override
		public int compareTo(ConstructorMetadata o) {
			return COMPARATOR.compare( this, o );
		}
	}

	public record ParameterMetadata(String name, String descriptor, boolean isPrimitive) {
	}

	public record MultiValueGroupMetadata(
			String targetDeclaringClass,
			List<MemberMetadata> members,
			String descriptor) {

		public static MultiValueGroupMetadata readerGroup(Class<?> targetClass, Member... members) {
			List<MemberMetadata> metadataList = buildMemberMetadataList( members, true );
			return new MultiValueGroupMetadata(
					targetClass.getName(),
					metadataList,
					NamingUtil.multiValueDescriptorFromMetadata( metadataList ) );
		}

		public static MultiValueGroupMetadata writerGroup(Class<?> targetClass, Member... members) {
			List<MemberMetadata> metadataList = buildMemberMetadataList( members, false );
			return new MultiValueGroupMetadata(
					targetClass.getName(),
					metadataList,
					NamingUtil.multiValueDescriptorFromMetadata( metadataList ) );
		}

		private static List<MemberMetadata> buildMemberMetadataList(Member[] members, boolean isReader) {
			List<MemberMetadata> result = new ArrayList<>();
			for ( Member member : members ) {
				if ( member instanceof Field field ) {
					Class<?> fieldType = field.getType();
					result.add( new FieldMetadata(
							field.getName(),
							org.objectweb.asm.Type.getDescriptor( fieldType ),
							fieldType.isPrimitive(),
							field.getDeclaringClass().getName(),
							false ) );
				}
				else if ( member instanceof Method method ) {
					String descriptor = org.objectweb.asm.Type.getMethodDescriptor( method );
					if ( isReader ) {
						result.add( new MethodMetadata(
								method.getName(),
								descriptor,
								method.getReturnType().isPrimitive(),
								method.getDeclaringClass().getName(),
								method.getDeclaringClass().isInterface(),
								org.objectweb.asm.Type.getDescriptor( method.getReturnType() ) ) );
					}
					else {
						result.add( new MethodMetadata(
								method.getName(),
								descriptor,
								method.getParameterTypes()[0].isPrimitive(),
								method.getDeclaringClass().getName(),
								method.getDeclaringClass().isInterface(),
								org.objectweb.asm.Type.getDescriptor( method.getReturnType() ) ) );
					}
				}
				else {
					throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
				}
			}
			return result;
		}
	}

	public record TypeMetadata(String packageName, String name, String host,
			boolean isPublic, boolean isInterface) implements Comparable<TypeMetadata> {

		public TypeMetadata(String packageName, String name, String host, boolean isPublic, boolean isInterface) {
			this.packageName = packageName == null ? "" : packageName;
			this.name = name;
			this.host = host;
			this.isPublic = isPublic;
			this.isInterface = isInterface;
		}

		public String dispatchTarget() {
			if ( !isPublic() && !isInterface() ) {
				return host + BRIDGE_SUFFIX;
			}
			return host;
		}

		private static final Comparator<TypeMetadata> COMPARATOR = Comparator.comparing( TypeMetadata::packageName )
				.thenComparing( TypeMetadata::name );

		@Override
		public int compareTo(TypeMetadata o) {
			return COMPARATOR.compare( this, o );
		}
	}
}
