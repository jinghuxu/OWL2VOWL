/*
 * VowlData.java
 *
 */

package de.uni_stuttgart.vis.vowl.owl2vowl.model.data;

import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.AbstractEntity;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.nodes.AbstractNode;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.nodes.classes.AbstractClass;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.nodes.datatypes.AbstractDatatype;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.properties.AbstractProperty;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.properties.TypeOfProperty;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.properties.VowlDatatypeProperty;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.entities.properties.VowlObjectProperty;
import de.uni_stuttgart.vis.vowl.owl2vowl.model.individuals.VowlIndividual;
import org.semanticweb.owlapi.model.IRI;

import java.util.*;

/**
 * Contains all data WebVOWL needs.
 */
public class VowlData {
	private Map<AbstractEntity, String> entityToId = new HashMap<>();
	private Map<IRI, AbstractEntity> entityMap = new HashMap<>();
	private Map<IRI, AbstractClass> classMap = new AllEntityMap<>(entityMap);
	private Map<IRI, AbstractDatatype> datatypeMap = new AllEntityMap<>(entityMap);
	private Map<IRI, VowlObjectProperty> objectPropertyMap = new AllEntityMap<>(entityMap);
	private Map<IRI, VowlDatatypeProperty> datatypePropertyMap = new AllEntityMap<>(entityMap);

	// Vielleicht generalisieren
	private Map<IRI, TypeOfProperty> typeOfPropertyMap = new AllEntityMap<IRI, TypeOfProperty>(entityMap);
	// TODO vielleicht zu allen entities hinzunehmen?
	private Map<IRI, VowlIndividual> individualMap = new HashMap<>();
	private Set<String> languages = new HashSet<>();
	private VowlSearcher searcher;
	private VowlIriGenerator iriGenerator = new VowlIriGenerator();
	private VowlGenerator generator;
	private VowlThingProvider thingProvider;

	public VowlData() {
		searcher = new VowlSearcher(this);
		generator = new VowlGenerator(this);
		thingProvider = new VowlThingProvider(this, searcher, generator);
	}

	public Map<IRI, TypeOfProperty> getTypeOfPropertyMap() {
		return typeOfPropertyMap;
	}

	public void addTypeOfProperty(TypeOfProperty property) {
		typeOfPropertyMap.put(property.getIri(), property);
	}

	public Map<IRI, VowlIndividual> getIndividualMap() {
		return individualMap;
	}

	public VowlThingProvider getThingProvider() {
		return thingProvider;
	}

	public VowlGenerator getGenerator() {
		return generator;
	}

	public VowlSearcher getSearcher() {
		return searcher;
	}

	public Map<IRI, AbstractEntity> getEntityMap() {
		return Collections.unmodifiableMap(entityMap);
	}

	public Map<IRI, AbstractDatatype> getDatatypeMap() {
		return Collections.unmodifiableMap(datatypeMap);
	}

	public Map<IRI, VowlObjectProperty> getObjectPropertyMap() {
		return Collections.unmodifiableMap(objectPropertyMap);
	}

	public Map<IRI, VowlDatatypeProperty> getDatatypePropertyMap() {
		return Collections.unmodifiableMap(datatypePropertyMap);
	}

	public Map<IRI, AbstractClass> getClassMap() {
		return Collections.unmodifiableMap(classMap);
	}

	public AbstractClass getClassForIri(IRI iri) {
		if (classMap.containsKey(iri)) {
			return classMap.get(iri);
		} else {
			throw new IllegalStateException("Can't find class for passed iri: " + iri);
		}
	}

	public AbstractDatatype getDatatypeForIri(IRI iri) {
		if (datatypeMap.containsKey(iri)) {
			return datatypeMap.get(iri);
		} else {
			throw new IllegalStateException("Can't find datatype for passed iri: " + iri);
		}
	}

	public VowlObjectProperty getObjectPropertyForIri(IRI iri) {
		if (objectPropertyMap.containsKey(iri)) {
			return objectPropertyMap.get(iri);
		} else {
			throw new IllegalStateException("Can't find object property for passed iri: " + iri);
		}
	}

	public VowlDatatypeProperty getDatatypePropertyForIri(IRI iri) {
		if (datatypePropertyMap.containsKey(iri)) {
			return datatypePropertyMap.get(iri);
		} else {
			throw new IllegalStateException("Can't find datatype property for passed iri: " + iri);
		}
	}

	public AbstractEntity getEntityForIri(IRI iri) {
		if (entityMap.containsKey(iri)) {
			return entityMap.get(iri);
		} else {
			throw new IllegalStateException("Can't find entity for passed iri: " + iri);
		}
	}

	public AbstractProperty getPropertyForIri(IRI iri) {
		AbstractEntity entity = getEntityForIri(iri);

		if (entity instanceof AbstractProperty) {
			return (AbstractProperty) entity;
		}

		throw new IllegalStateException("Can't find property for passed iri: " + iri);
	}

	public AbstractNode getNodeForIri(IRI iri) {
		AbstractEntity entity = getEntityForIri(iri);

		if (entity instanceof AbstractNode) {
			return (AbstractNode) entity;
		}

		throw new IllegalStateException("Can't find node for passed iri: " + iri);
	}

	public String getIdForIri(IRI iri) {
		return getIdForEntity(getEntityForIri(iri));
	}

	public String getIdForEntity(AbstractEntity entity) {
		if (!entityToId.containsKey(entity)) {
			entityToId.put(entity, "" + entityToId.keySet().size());
		}

		return entityToId.get(entity);
	}

	public void addClass(AbstractClass vowlClass) {
		classMap.put(vowlClass.getIri(), vowlClass);
	}

	public void addDatatype(AbstractDatatype datatype) {
		datatypeMap.put(datatype.getIri(), datatype);
	}

	public void addObjectProperty(VowlObjectProperty prop) {
		objectPropertyMap.put(prop.getIri(), prop);
	}

	public void addDatatypeProperty(VowlDatatypeProperty prop) {
		datatypePropertyMap.put(prop.getIri(), prop);
	}

	public Set<String> getLanguages() {
		return Collections.unmodifiableSet(languages);
	}

	public void addLanguage(String language) {
		languages.add(language);
	}

	public IRI getNewIri() {
		return iriGenerator.generate();
	}

	public void addIndividual(VowlIndividual individual) {
		individualMap.put(individual.getIri(), individual);
	}

	private class VowlIriGenerator {
		private String iriPrefix = "http://owl2vowl.de#";
		private int generations = 0;

		public IRI generate() {
			return IRI.create(iriPrefix + generations++);
		}
	}

	public Collection<AbstractProperty> getProperties() {
		Set<AbstractProperty> concat = new HashSet<>();
		concat.addAll(datatypePropertyMap.values());
		concat.addAll(objectPropertyMap.values());

		return Collections.unmodifiableSet(concat);
	}
}

class AllEntityMap<K, V extends AbstractEntity> extends HashMap<K, V> {
	private HashMap<K, V> mergeMap;

	public <Val extends AbstractEntity> AllEntityMap(Map<K, Val> mergeMap) {
		this.mergeMap = (HashMap<K, V>) mergeMap;
	}

	@Override
	public V put(K key, V value) {
		super.put(key, value);
		mergeMap.put(key, value);
		return value;
	}
}
