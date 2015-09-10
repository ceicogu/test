package com.qihao.toy.dal.domain.typehandlers;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import com.qihao.shared.base.DescedEnum;
import com.qihao.shared.base.IntEnum;
import com.qihao.shared.base.utils.IntEnumUtils;

/**
 * EnumTypeHandler
 *
 * @author 
 */
@MappedTypes({ DescedEnum.class })
public class IntEnumTypeHandler<E extends Enum<E> & IntEnum> extends
		BaseTypeHandler<E> {
	private Class<E> type;

	public IntEnumTypeHandler(Class<E> type) {
		if (type == null)
			throw new IllegalArgumentException("Type argument cannot be null");
		this.type = type;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter,
			JdbcType jdbcType) throws SQLException {
		if (jdbcType == null) {
			ps.setInt(i, parameter.intValue());
		} else {
			ps.setObject(i, parameter.intValue(), jdbcType.TYPE_CODE); 
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
        Integer s = rs.getInt(columnName);
        return s == null ? null : IntEnumUtils.valueOf(type, s);
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
        Integer s = rs.getInt(columnIndex);
        return s == null ? null : IntEnumUtils.valueOf(type, s);
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
        Integer s = cs.getInt(columnIndex);
        return s == null ? null : IntEnumUtils.valueOf(type, s);
	}

}