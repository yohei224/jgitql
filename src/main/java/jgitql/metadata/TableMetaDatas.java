package jgitql.metadata;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jsqlparser.statement.select.SelectItem;

public class TableMetaDatas {
  private static final Map<String, Class<? extends TableMetaData>> TABLES = new HashMap<String, Class<? extends TableMetaData>>();
  static {
    TABLES.put(Branches.TABLE_NAME, Branches.class);
    TABLES.put(Commits.TABLE_NAME, Commits.class);
    TABLES.put(Files.TABLE_NAME, Files.class);
    TABLES.put(Tags.TABLE_NAME, Tags.class);
  }
  
  public static Set<String> getAllNames() {
    return TABLES.keySet();
  }

  public static TableMetaData getTableMetaData(String name) throws SQLException {
    return getTableMetaData(name, null);
  }

  public static TableMetaData getTableMetaData(String name, List<SelectItem> selectItems) throws SQLException {
    try {
      if (!TABLES.containsKey(name))
        throw new SQLException("No such table: " + name);

      TableMetaData newInstance = TABLES.get(name).newInstance();
      newInstance.setSelectItems(selectItems);
      return newInstance;
    } catch (InstantiationException | IllegalAccessException e) {
      throw new SQLException("No such table: " + name);
    }
  }

  private TableMetaDatas() {

  }
}
