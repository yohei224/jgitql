package jgitdbc.metadata;

import java.text.SimpleDateFormat;

public class ResultRow {
  private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private TableMetaData meta;
  private Object[] colValues;

  
  ResultRow(TableMetaData meta, Object ... colValues) {
    this.meta = meta;
    this.colValues = colValues;
  }

  private Object getColValue(int columnNumber) {
    //this.meta.getRetrieveColumnDefs()
    return this.colValues[columnNumber - 1];
  }
  
  public String getString(String columnName) {
    return this.getString(this.meta.findColumn(columnName) + 1);
  }
  
  public String getString(int columnNumber) {
    Object val = getColValue(columnNumber);
    
    if (val instanceof java.sql.Date){
      return formatter.format(val);
    }
    return String.valueOf(val);
  }
}
