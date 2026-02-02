module org.hibernate.models {
	requires java.sql;
	requires org.jboss.logging;

	exports org.hibernate.models;
	exports org.hibernate.models.rendering;
	exports org.hibernate.models.rendering.spi;
	exports org.hibernate.models.serial.spi;
	exports org.hibernate.models.spi;

	// TODO: have the "shared classes" in the SPI packages, instead of exporting "internal" packages
	//  (even if it's to a limited number of own modules) ?
	exports org.hibernate.models.internal.util to org.hibernate.models.bytebuddy, org.hibernate.models.jandex, org.hibernate.orm.core, org.hibernate.orm.envers;
	exports org.hibernate.models.internal to org.hibernate.models.bytebuddy, org.hibernate.models.jandex, org.hibernate.orm.core, org.hibernate.orm.envers;
	exports org.hibernate.models.internal.jdk to org.hibernate.models.bytebuddy, org.hibernate.models.jandex, org.hibernate.orm.core, org.hibernate.orm.envers;
	exports org.hibernate.models.serial.internal to org.hibernate.models.bytebuddy, org.hibernate.models.jandex, org.hibernate.orm.core, org.hibernate.orm.envers;
	exports org.hibernate.models.internal.dynamic to org.hibernate.orm.core, org.hibernate.orm.envers;
	exports org.hibernate.models.rendering.internal to org.hibernate.orm.core, org.hibernate.orm.envers;

	uses org.hibernate.models.spi.ModelsContextProvider;
}
