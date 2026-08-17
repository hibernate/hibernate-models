module org.hibernate.models.accessor.asm {
	requires org.hibernate.models.accessor;
	requires static org.jboss.logging;
	requires org.objectweb.asm;

	exports org.hibernate.models.accessor.asm;
	exports org.hibernate.models.accessor.asm.spi;
}
