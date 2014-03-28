/*******************************************************************************
 * Copyright (C) 2014 Bonitasoft S.A.
 * Bonitasoft is a trademark of Bonitasoft SA.
 * This software file is BONITASOFT CONFIDENTIAL. Not For Distribution.
 * For commercial licensing information, contact:
 * Bonitasoft, 32 rue Gustave Eiffel – 38000 Grenoble
 * or Bonitasoft US, 51 Federal Street, Suite 305, San Francisco, CA 94107
 *******************************************************************************/
package com.bonitasoft.engine.bdm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import com.bonitasoft.engine.bdm.validator.BusinessObjectModelValidator;
import com.bonitasoft.engine.bdm.validator.ValidationStatus;
import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JType;

/**
 * @author Romain Bioteau
 */
public class BDMCodeGenerator extends CodeGenerator {

	private static final String IMPL_SUFFIX = "Impl";
	private final BusinessObjectModel bom;

	public BDMCodeGenerator(final BusinessObjectModel bom) {
		super();
		if (bom == null) {
			throw new IllegalArgumentException("bom is null");
		}
		this.bom = bom;
	}

	protected void buildASTFromBom() throws JClassAlreadyExistsException {
		for (final BusinessObject bo : bom.getBusinessObjects()) {
			addEntity(bo);
		}
	}

	protected JDefinedClass addEntity(final BusinessObject bo) throws JClassAlreadyExistsException {
		final String qualifiedName = bo.getQualifiedName();
		validateClassNotExistsInRuntime(qualifiedName);
		validateClassNotExistsInRuntime(toImplClassname(qualifiedName));

		createInterfaceClass(bo, qualifiedName);
		JDefinedClass entityClass = createImplClass(bo, qualifiedName);

		return entityClass;
	}

	private JDefinedClass createInterfaceClass(final BusinessObject bo,
			final String qualifiedName) throws JClassAlreadyExistsException {
		JDefinedClass interfaceClass = addInterface(qualifiedName);
		interfaceClass.javadoc().add(bo.getDescription());
		addInterface(interfaceClass, com.bonitasoft.engine.bdm.Entity.class.getName());
	
		for (final Field field : bo.getFields()) {
			final JFieldVar basicField = addBasicField(interfaceClass, field);
			addAccessorsSignature(interfaceClass, basicField);
			interfaceClass.removeField(basicField);
		}
		return interfaceClass;
	}

	private JDefinedClass createImplClass(final BusinessObject bo,
			final String qualifiedName) throws JClassAlreadyExistsException {
		JDefinedClass entityClass = addClass(toImplClassname(qualifiedName));
		entityClass = addInterface(entityClass, qualifiedName);
	

		final JAnnotationUse entityAnnotation = addAnnotation(entityClass, Entity.class);
		entityAnnotation.param("name", entityClass.name());

		final JAnnotationUse tableAnnotation = addAnnotation(entityClass, Table.class);
		tableAnnotation.param("name", entityClass.name().toUpperCase());
		final List<UniqueConstraint> uniqueConstraints = bo.getUniqueConstraints();
		if (!uniqueConstraints.isEmpty()) {
			final JAnnotationArrayMember uniqueConstraintsArray = tableAnnotation.paramArray("uniqueConstraints");

			for (final UniqueConstraint uniqueConstraint : uniqueConstraints) {
				final JAnnotationUse uniqueConstraintAnnotatation = uniqueConstraintsArray.annotate(javax.persistence.UniqueConstraint.class);
				uniqueConstraintAnnotatation.param("name", uniqueConstraint.getName().toUpperCase());
				final JAnnotationArrayMember columnNamesParamArray = uniqueConstraintAnnotatation.paramArray("columnNames");
				for (final String fieldName : uniqueConstraint.getFieldNames()) {
					columnNamesParamArray.param(fieldName.toUpperCase());
				}
			}
		}

		final List<Query> queries = bo.getQueries();
		if (!queries.isEmpty()) {
			final JAnnotationUse namedQueriesAnnotation = addAnnotation(entityClass, NamedQueries.class);
			final JAnnotationArrayMember valueArray = namedQueriesAnnotation.paramArray("value");
			for (final Query query : queries) {
				final JAnnotationUse nameQueryAnnotation = valueArray.annotate(NamedQuery.class);
				nameQueryAnnotation.param("name", query.getName());
				nameQueryAnnotation.param("query", query.getContent());
			}
		}

		addPersistenceIdFieldAndAccessors(entityClass);
		addPersistenceVersionFieldAndAccessors(entityClass);

		for (final Field field : bo.getFields()) {
			final JFieldVar basicField = addBasicField(entityClass, field);
			addAccessors(entityClass, basicField);
		}

		addEqualsMethod(entityClass);
		addHashCodeMethod(entityClass);
		return entityClass;
	}

	private String toImplClassname(String qualifiedName) {
		return qualifiedName+IMPL_SUFFIX;
	}

	private void validateClassNotExistsInRuntime(final String qualifiedName) {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		boolean alreadyInRuntime = true;
		try {
			contextClassLoader.loadClass(qualifiedName);
		} catch (final ClassNotFoundException e) {
			alreadyInRuntime = false;
		}
		if (alreadyInRuntime) {
			throw new IllegalArgumentException("Class " + qualifiedName + " already exists in target runtime environment.");
		}
	}

	protected void addPersistenceIdFieldAndAccessors(final JDefinedClass entityClass) throws JClassAlreadyExistsException {
		final JFieldVar idFieldVar = addField(entityClass, Field.PERSISTENCE_ID, toJavaType(FieldType.LONG));
		addAnnotation(idFieldVar, Id.class);
		addAnnotation(idFieldVar, GeneratedValue.class);
		addAccessors(entityClass, idFieldVar);
	}

	protected void addPersistenceVersionFieldAndAccessors(final JDefinedClass entityClass) throws JClassAlreadyExistsException {
		final JFieldVar versionField = addField(entityClass, Field.PERSISTENCE_VERSION, toJavaType(FieldType.LONG));
		addAnnotation(versionField, Version.class);
		addAccessors(entityClass, versionField);
	}

	protected JFieldVar addBasicField(final JDefinedClass entityClass, final Field field) throws JClassAlreadyExistsException {
		final JFieldVar fieldVar = addField(entityClass, field.getName(), toJavaType(field.getType()));
		final JAnnotationUse columnAnnotation = addAnnotation(fieldVar, Column.class);
		columnAnnotation.param("name", field.getName().toUpperCase());
		final Boolean nullable = field.isNullable();
		columnAnnotation.param("nullable", nullable == null || nullable);
		if (field.getType() == FieldType.DATE) {
			final JAnnotationUse temporalAnnotation = addAnnotation(fieldVar, Temporal.class);
			temporalAnnotation.param("value", TemporalType.TIMESTAMP);
		} else if (FieldType.TEXT.equals(field.getType())) {
			addAnnotation(fieldVar, Lob.class);
		} else if (FieldType.STRING.equals(field.getType()) && field.getLength() != null && field.getLength() > 0) {
			columnAnnotation.param("length", field.getLength());
		}
		return fieldVar;
	}

	protected void addAccessors(final JDefinedClass entityClass, final JFieldVar fieldVar) throws JClassAlreadyExistsException {
		addSetter(entityClass, fieldVar);
		addGetter(entityClass, fieldVar);
	}
	
	protected void addAccessorsSignature(final JDefinedClass entityClass, final JFieldVar fieldVar) throws JClassAlreadyExistsException {
		addSetterSignature(entityClass, fieldVar);
		addGetterSignature(entityClass, fieldVar);
	}

	protected JType toJavaType(final FieldType type) {
		return getModel().ref(type.getClazz());
	}

	@Override
	public void generate(final File destDir) throws IOException, JClassAlreadyExistsException, BusinessObjectModelValidationException {
		final BusinessObjectModelValidator validator = new BusinessObjectModelValidator();
		final ValidationStatus validationStatus = validator.validate(bom);
		if (!validationStatus.isOk()) {
			throw new BusinessObjectModelValidationException(validationStatus);
		}
		buildASTFromBom();
		super.generate(destDir);
	}

}
