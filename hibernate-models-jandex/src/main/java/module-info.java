module org.hibernate.models.jandex {
	requires org.jboss.jandex;
	requires org.jboss.logging;

	requires transitive org.hibernate.models;

	exports org.hibernate.models.jandex;
	// todo : this should all move to "api" (org.hibernate.models.jandex)
	exports org.hibernate.models.jandex.spi;

	provides org.hibernate.models.spi.ModelsContextProvider with
		org.hibernate.models.jandex.internal.JandexModelsContextProvider;

}
