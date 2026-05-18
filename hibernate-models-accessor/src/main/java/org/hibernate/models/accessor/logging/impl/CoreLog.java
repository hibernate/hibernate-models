/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.logging.impl;

import org.hibernate.models.accessor.HibernateAccessorException;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;
import org.jboss.logging.annotations.ValidIdRanges;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Member;
import java.util.Locale;

@MessageLogger(projectCode = "HACCESSOR")
@ValidIdRanges({
		@ValidIdRange(min = 1, max = 10_000),
})
public interface CoreLog {

	String CATEGORY_NAME = "org.hibernate.models.accessor";

	CoreLog INSTANCE = Logger.getMessageLogger(MethodHandles.lookup(), CoreLog.class, CATEGORY_NAME, Locale.ROOT);

	@Message(id = 1,
			value = "Exception while invoking '%1$s' on '%2$s': %3$s.")
	HibernateAccessorException errorInvokingMember(Member member, String componentAsString,
												@Cause Throwable cause, String causeMessage);

	@Message(id = 2,
			value = "Exception while invoking '%1$s' on '%2$s': %3$s.")
	HibernateAccessorException errorInvokingHandle(MethodHandle handle, String componentAsString,
												@Cause Throwable cause, String causeMessage);

	@Message(id = 3,
			value = "Exception while creating '%1$s'': %2$s.")
	HibernateAccessorException errorCreatingHandle(Member handle,
												@Cause Throwable cause, String causeMessage);

}
