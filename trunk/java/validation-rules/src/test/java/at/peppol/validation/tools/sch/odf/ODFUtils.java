package at.peppol.validation.tools.sch.odf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Table;

@Immutable
public final class ODFUtils {
  private ODFUtils () {}

  @Nullable
  public static Cell getCell (@Nonnull final Table aSheet, final int nCol, final int nRow) {
    if (nRow >= aSheet.getRowCount () || nCol >= aSheet.getColumnCount ())
      return null;
    return aSheet.getRowByIndex (nRow).getCellByIndex (nCol);
  }

  public static boolean isEmpty (@Nonnull final Table aSheet, final int nCol, final int nRow) {
    final Cell aCell = getCell (aSheet, nCol, nRow);
    return aCell == null || aCell.getValueType () == null;
  }

  @Nullable
  public static String getText (@Nonnull final Table aSheet, final int nCol, final int nRow) {
    final Cell aCell = getCell (aSheet, nCol, nRow);
    return aCell == null ? null : SimpleTextExtractor.getText (aCell);
  }
}
