package com.example.examencorte2;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DatabaseProductos extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sistema.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PRODUCTOS = "productos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CODIGO = "codigo";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_PRECIO = "precio";
    private static final String COLUMN_MARCA = "marca";

    public DatabaseProductos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crea la tabla de productos en la base de datos
        String CREATE_PRODUCTOS_TABLE = "CREATE TABLE " + TABLE_PRODUCTOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CODIGO + " TEXT,"
                + COLUMN_NOMBRE + " TEXT,"
                + COLUMN_PRECIO + " REAL,"
                + COLUMN_MARCA + " TEXT"
                + ")";
        db.execSQL(CREATE_PRODUCTOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Elimina la tabla de productos si existe y la vuelve a crear
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTOS);
        onCreate(db);
    }

    public void guardarProducto(String codigo, String nombre, double precio, String marca) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CODIGO, codigo);
        values.put(COLUMN_NOMBRE, nombre);
        values.put(COLUMN_PRECIO, precio);
        values.put(COLUMN_MARCA, marca);
        db.insert(TABLE_PRODUCTOS, null, values);
        db.close();
    }

    public Producto buscarProductoPorCodigo(String codigo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_NOMBRE, COLUMN_PRECIO, COLUMN_MARCA};
        String selection = COLUMN_CODIGO + " = ?";
        String[] selectionArgs = {codigo};
        Cursor cursor = db.query(TABLE_PRODUCTOS, columns, selection, selectionArgs, null, null, null);

        Producto producto = null;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndexId = cursor.getColumnIndex(COLUMN_ID);
            int columnIndexNombre = cursor.getColumnIndex(COLUMN_NOMBRE);
            int columnIndexPrecio = cursor.getColumnIndex(COLUMN_PRECIO);
            int columnIndexMarca = cursor.getColumnIndex(COLUMN_MARCA);

            producto = new Producto();
            producto.setId(cursor.getInt(columnIndexId));
            producto.setCodigo(codigo);

            // Verificar que las columnas existan antes de obtener los valores
            if (columnIndexNombre != -1) {
                producto.setNombre(cursor.getString(columnIndexNombre));
            }
            if (columnIndexPrecio != -1) {
                producto.setPrecio(cursor.getDouble(columnIndexPrecio));
            }
            if (columnIndexMarca != -1) {
                producto.setMarca(cursor.getString(columnIndexMarca));
            }

            cursor.close();
        }

        db.close();
        return producto;
    }

    public void actualizarProducto(Producto producto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CODIGO, producto.getCodigo());
        values.put(COLUMN_NOMBRE, producto.getNombre());
        values.put(COLUMN_PRECIO, producto.getPrecio());
        values.put(COLUMN_MARCA, producto.getMarca());
        db.update(TABLE_PRODUCTOS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(producto.getId())});
        db.close();
    }

    public void borrarProductoPorCodigo(String codigo) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTOS, COLUMN_CODIGO + " = ?", new String[]{codigo});
        db.close();
    }

    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> listaProductos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRODUCTOS, null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Producto producto = new Producto();
                producto.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                producto.setCodigo(cursor.getString(cursor.getColumnIndex(COLUMN_CODIGO)));
                producto.setNombre(cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE)));
                producto.setPrecio(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRECIO)));
                producto.setMarca(cursor.getString(cursor.getColumnIndex(COLUMN_MARCA)));

                listaProductos.add(producto);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return listaProductos;
    }
}
