package jgitdbc.metadata;

import gristle.GitRepository;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.statement.select.SelectItem;

public abstract class TableMetaData implements ResultSetMetaData {

  public final String tableName;
  private List<SelectItem> selectItems;

  private ColumnMetaData[] columnDefs;
  private Map<String, ColumnMetaData> mapOfAllColumnDefByName;

  public abstract ColumnMetaData[] getAllColumnDefs();

  public TableMetaData(String tableName) {
    this.tableName = tableName;
  }

  public void setSelectItems(List<SelectItem> selectItems) {
    this.selectItems = selectItems;
  }

  public ColumnMetaData[] getRetrieveColumnDefs() {
    if (this.columnDefs == null) {
      this.columnDefs = this.getColumnDefsImpl();
    }
    return this.columnDefs;
  }

  private ColumnMetaData[] getColumnDefsImpl() {
    List<SelectItem> selectItems = this.selectItems;
    int size = selectItems.size();

    boolean allCols = (size == 1 && selectItems.get(0).toString().equals("*"));
    if (allCols) {
      return this.getAllColumnDefs();
    }

    Map<String, ColumnMetaData> mapOfColumnDefByName = this.getMapOfAllColumnDefByName();

    List<ColumnMetaData> cols = new ArrayList<ColumnMetaData>(size);
    for (SelectItem selectItem : selectItems) {
      cols.add(mapOfColumnDefByName.get(selectItem.toString()));
    }

    return cols.toArray(new ColumnMetaData[size]);
  }

  public ColumnMetaData getColumnDef(int column) {
    return this.getRetrieveColumnDefs()[column - 1];
  }

  public int findColumn(String columnLabel) throws SQLException {
    ColumnMetaData[] columnDefs = getRetrieveColumnDefs();
    for (int i = 0; i < columnDefs.length; i++) {
      if (columnDefs[i].getName().equals(columnLabel)) {
        return i + 1;
      }
    }
    throw new SQLException("No such column: " + columnLabel);
  }

  private List<Integer> colIndexes;

  protected List<Integer> getColIndexes() {

    if (this.colIndexes == null) {
      List<Integer> ret = new ArrayList<Integer>();

      ColumnMetaData[] retrieveColumnDefs = this.getRetrieveColumnDefs();
      for (ColumnMetaData retrieveColumnMetaData : retrieveColumnDefs) {
        int i = 0;
        for (ColumnMetaData columnMetaData : this.getAllColumnDefs()) {
          if (retrieveColumnMetaData == columnMetaData) {
            ret.add(Integer.valueOf(i));
            break;
          }
          i++;
        }
      }

      this.colIndexes = ret;
    }

    return this.colIndexes;
  }

  protected Object[] filterColumns(Object[] allVals) {
    Object[] vals = new Object[this.getColIndexes().size()];

    int i = 0;
    for (Integer idx : this.getColIndexes()) {
      vals[i++] = allVals[idx];
    }
    return vals;
  }

  public Map<String, ColumnMetaData> getMapOfAllColumnDefByName() {
    if (this.mapOfAllColumnDefByName == null) {
      Map<String, ColumnMetaData> map = new HashMap<String, ColumnMetaData>();
      for (ColumnMetaData def : this.getAllColumnDefs()) {
        map.put(def.getName(), def);
      }
      this.mapOfAllColumnDefByName = map;
    }
    return this.mapOfAllColumnDefByName;
  }

  public abstract List<ResultRow> getRows(GitRepository repo) throws IOException;

  @Override
  public String getTableName(int i) {
    return tableName;
  }

  @Override
  public int getColumnCount() throws SQLException {
    return this.getRetrieveColumnDefs().length;
  }

  @Override
  public String getColumnLabel(int column) throws SQLException {
    return this.getColumnDef(column).getName();
  }

  @Override
  public String getColumnName(int column) throws SQLException {
    return this.getColumnDef(column).getName();
  }

  @Override
  public int isNullable(int column) throws SQLException {
    return this.getColumnDef(column).getNullable();
  }

  @Override
  public int getColumnType(int column) throws SQLException {
    return this.getColumnDef(column).getType();
  }

  @Override
  public String getColumnTypeName(int column) throws SQLException {
    return this.getColumnDef(column).getTypeName();
  }

  @Override
  public String getColumnClassName(int column) throws SQLException {
    return this.getColumnDef(column).getClassName();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  @Override
  public boolean isAutoIncrement(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isCaseSensitive(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isSearchable(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isCurrency(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isSigned(int column) throws SQLException {
    return false;
  }

  @Override
  public int getColumnDisplaySize(int column) throws SQLException {
    return 0;
  }

  @Override
  public String getSchemaName(int column) throws SQLException {
    return null;
  }

  @Override
  public int getPrecision(int column) throws SQLException {
    return 0;
  }

  @Override
  public int getScale(int column) throws SQLException {
    return 0;
  }

  @Override
  public String getCatalogName(int column) throws SQLException {
    return null;
  }

  @Override
  public boolean isReadOnly(int column) throws SQLException {
    return true;
  }

  @Override
  public boolean isWritable(int column) throws SQLException {
    return false;
  }

  @Override
  public boolean isDefinitelyWritable(int column) throws SQLException {
    return false;
  }

}
