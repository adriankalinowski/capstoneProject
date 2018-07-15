package edu.wayne.capstone.references.config;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonbUserType extends AbstractJsonbUserType {

	private static final long serialVersionUID = 1L;
	
	private Class<?> classType = ObjectNode.class;
	
	public static final String OBJECT_NODE = "ObjectNode";

	@Override
	public Class<?> returnedClass() {

		return this.classType;
	}
	
}