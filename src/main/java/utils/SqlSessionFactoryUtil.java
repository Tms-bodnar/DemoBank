package main.java.utils;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* A statikus SqlSessionFactory példány létrehozása a MyBatisConfig.xml fájl alapján
* 
*/

public class SqlSessionFactoryUtil {

	private static final SqlSessionFactory SQLSESSIONFACTORY;

	static {
		Reader reader = null;
		try {
			reader = Resources.getResourceAsReader("MyBatisConfig.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		SQLSESSIONFACTORY = new SqlSessionFactoryBuilder().build(reader);

	}

	public static SqlSessionFactory getSqlsessionFactory() {
		return SQLSESSIONFACTORY;
	}

}
