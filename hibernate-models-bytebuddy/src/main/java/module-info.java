module org.hibernate.models.bytebuddy {
	requires org.jboss.logging;
	requires net.bytebuddy;

	requires transitive org.hibernate.models;

	exports org.hibernate.models.bytebuddy;
	exports org.hibernate.models.bytebuddy.spi;
	// exports org.hibernate.models.bytebuddy.internal;
	// exports org.hibernate.models.bytebuddy.internal.values;

	provides org.hibernate.models.spi.ModelsContextProvider with
		org.hibernate.models.bytebuddy.internal.ByteBuddyContextProvider;

}
