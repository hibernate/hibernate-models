module org.hibernate.models.accessor.bytebuddy {
	requires org.hibernate.models.accessor;
	requires net.bytebuddy;
	requires static org.jboss.logging;

	exports org.hibernate.models.accessor.bytebuddy;
	exports org.hibernate.models.accessor.bytebuddy.spi;
}
