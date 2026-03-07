module org.hibernate.models {
	requires org.jboss.logging;

	exports org.hibernate.models;
	exports org.hibernate.models.dynamic;
	exports org.hibernate.models.jdk;
	exports org.hibernate.models.rendering;
	exports org.hibernate.models.serial;

	// todo : most of this should move to `org.hibernate.models`
	exports org.hibernate.models.spi;

	exports org.hibernate.models.logging
			to org.hibernate.models.bytebuddy, org.hibernate.models.jandex;
	exports org.hibernate.models.support
			to org.hibernate.models.bytebuddy, org.hibernate.models.jandex;
	exports org.hibernate.models.internal
			to org.hibernate.models.bytebuddy, org.hibernate.models.jandex;
	exports org.hibernate.models.rendering.internal
			to org.hibernate.models.bytebuddy, org.hibernate.models.jandex;

	// TODO: have the "shared classes" in the SPI packages, instead of exporting "internal" packages
	//  (even if it's to a limited number of own modules) ?
	exports org.hibernate.models.internal.util
			to org.hibernate.models.bytebuddy, org.hibernate.models.jandex, org.hibernate.orm.core, org.hibernate.orm.envers;
	exports org.hibernate.models.serial.internal
			to org.hibernate.models.bytebuddy, org.hibernate.models.jandex, org.hibernate.orm.core, org.hibernate.orm.envers;
	exports org.hibernate.models.jdk.internal
			to org.hibernate.models.bytebuddy, org.hibernate.models.jandex, org.hibernate.orm.core, org.hibernate.orm.envers;
	exports org.hibernate.models.dynamic.internal
			to org.hibernate.orm.core, org.hibernate.orm.envers;

	uses org.hibernate.models.spi.ModelsContextProvider;
}
