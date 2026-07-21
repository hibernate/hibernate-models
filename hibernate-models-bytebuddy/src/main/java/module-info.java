module org.hibernate.models.bytebuddy {
	requires org.jboss.logging;

	requires transitive org.hibernate.models;
	requires transitive net.bytebuddy;

	exports org.hibernate.models.bytebuddy;
	exports org.hibernate.models.bytebuddy.spi;

	provides org.hibernate.models.spi.ModelsContextProvider with
		org.hibernate.models.bytebuddy.internal.ByteBuddyContextProvider;

}
