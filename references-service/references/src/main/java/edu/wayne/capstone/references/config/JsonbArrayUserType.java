package edu.wayne.capstone.references.config;

import com.fasterxml.jackson.databind.node.ArrayNode;

public class JsonbArrayUserType extends AbstractJsonbUserType {
	
	private static final long serialVersionUID = 1L;
	
	private Class<?> classType = ArrayNode.class;
	
	public static final String ARRAY_NODE = "ArrayNode";

	@Override
	public Class<?> returnedClass() {

		return this.classType;
	}
}
