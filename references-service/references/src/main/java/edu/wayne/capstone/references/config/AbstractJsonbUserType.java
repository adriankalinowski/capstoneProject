package edu.wayne.capstone.references.config;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;

public abstract class AbstractJsonbUserType implements UserType, Serializable {

private static final long serialVersionUID = 1L;
	
	private static final int[] SQL_TYPES = new int[] { Types.JAVA_OBJECT };
	
	private int sqlType = Types.OTHER;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	
	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return this.deepCopy(cached);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		
		Object copy = null;
		
		if(value != null) {
			
			try {
				return MAPPER.readValue(MAPPER.writeValueAsString(value), returnedClass());
			} catch (IOException e) {
				throw new HibernateException("unable to deep copy object", e);
			}
			
		}
		
		return copy;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		
		try {
			return MAPPER.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new HibernateException("unable to disassemble object", e);
		}
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equal(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return Objects.hashCode(x);
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		Object obj = null;
		
		String jsonValueString = rs.getString(names[0]);

		if (!rs.wasNull()) {
		 
			try {
				obj = MAPPER.readValue(jsonValueString, returnedClass());
			} catch (IOException e) {
				throw new HibernateException("unable to read object from result set", e);
			}
		}
				
		return obj;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		
		if (value == null) {
		
			st.setNull(index, this.sqlType);
		
		} else {
			
			try {
				st.setObject(index, MAPPER.writeValueAsString(value), this.sqlType);
			
			} catch (JsonProcessingException e) {
				throw new HibernateException("unable to set object to result set", e);
			}
		}

	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return this.deepCopy(original);
	}

	@Override
	public abstract Class<?> returnedClass();

	@Override
	public int[] sqlTypes() {
		return SQL_TYPES;
	}
}
