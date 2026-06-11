module org.hibernate.models.jandex {
	requires org.jboss.jandex;
	requires org.jboss.logging;

	requires transitive org.hibernate.models;

	exports org.hibernate.models.jandex;
	exports org.hibernate.models.jandex.spi;
	// exports org.hibernate.models.jandex.internal;
	// exports org.hibernate.models.jandex.internal.values;

	provides org.hibernate.models.spi.ModelsContextProvider with
		org.hibernate.models.jandex.internal.JandexModelsContextProvider;

}
