package webapp.persistence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Creates a customer row gateway by finding it from the database.
 *	
 * @author fmartins
 * @Version 1.0 (03/02/2015)
 *
 */
public class SaleFinder {
	
	/**
	 * The select customer by VAT SQL statement
	 */
	private static final String GET_SALE_BY_ID = 
			   "select * from sale where id = ?";
	
	/**
	 * Gets a customer by its VAT number 
	 * 
	 * @param vat The VAT number of the customer to search for
	 * @return The result set of the query
	 * @throws PersistenceException When there is an error getting the customer
	 * from the database.
	 */
	public SaleRowDataGateway getSaleById (int id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_SALE_BY_ID)){
			statement.setInt(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				rs.next();
				return new SaleRowDataGateway(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting a sale by its id number", e);
		}
	}

}
