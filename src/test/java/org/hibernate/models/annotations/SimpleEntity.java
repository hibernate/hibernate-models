/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.annotations;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Comment;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

/**
 * @author Steve Ebersole
 */
@Entity(name = "SimpleColumnEntity")
@Table(name = "simple_entities")
@SecondaryTable(name="another_table")
@CustomAnnotation()
@NamedQuery( name = "abc", query = "select me" )
@NamedQuery( name = "xyz", query = "select you" )
@Cacheable
@Cache( usage = CacheConcurrencyStrategy.READ_ONLY, region = "custom-region" )
public class SimpleEntity {
	@Id
	@Column(name = "id")
	@Comment("SimpleColumnEntity PK column")
	private Integer id;

	@Basic
	@Column(name = "description", nullable = false, unique = true, insertable = false)
	@Comment("SimpleColumnEntity#name")
	private String name;

	@Basic
	@Column(table = "another_table", columnDefinition = "special_type")
	private String name2;

	private SimpleEntity() {
		// for use by Hibernate
	}

	public SimpleEntity(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
