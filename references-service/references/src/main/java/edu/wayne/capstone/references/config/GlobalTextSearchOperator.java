package edu.wayne.capstone.references.config;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

public class GlobalTextSearchOperator implements SQLFunction{
	@Override
	public boolean hasArguments() {
		return true;
	}

	@Override
	public boolean hasParenthesesIfNoArguments() {
		return false;
	}

	@Override
	public Type getReturnType(Type firstArgumentType, Mapping mapping) throws QueryException {
		return new BooleanType();
	}

	@Override
    public String render(Type firstArgumentType, List args, SessionFactoryImplementor factory)
            throws QueryException {
        if (args.size() < 2) {
            throw new IllegalArgumentException("The function must be passed an argument");
        }

        return String.format("reference.gen_ts_query(concatenated_columns_tsv, %s, %s, %s)", args.get(0), args.get(1), args.get(2));
    }
}
