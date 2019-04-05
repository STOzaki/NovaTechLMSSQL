package com.lms.dao.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CloseResources {
	private final static Logger LOGGER = Logger.getLogger(CloseResources.class.getName());
	
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
	        try {
	            rs.close();
	        } catch (SQLException e) {LOGGER.log(Level.WARNING, "Failed to close Result Set");}
	    }
	}

	public static void closePreparedStatement(PreparedStatement ps) {
	    if (ps != null) {
	        try {
	            ps.close();
	        } catch (SQLException e) {LOGGER.log(Level.WARNING, "Failed to close Prepared Statement");}
	    }
	}

}
