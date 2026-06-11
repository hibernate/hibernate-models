module org.hibernate.models.bytebuddy {
	requires org.jboss.logging;
	requires net.bytebuddy;

	requires transitive org.hibernate.models;

	exports org.hibernate.models.bytebuddy;
	// todo : this should all move to "api" (org.hibernate.models.bytebuddy)
	exports org.hibernate.models.bytebuddy.spi;

	provides org.hibernate.models.spi.ModelsContextProvider with
		org.hibernate.models.bytebuddy.internal.ByteBuddyContextProvider;

}
