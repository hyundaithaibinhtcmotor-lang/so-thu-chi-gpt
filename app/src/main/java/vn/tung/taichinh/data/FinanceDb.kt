package vn.tung.taichinh.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Account(val id: Long, val name: String, val group: String, val balance: Long)
data class Tx(val id: Long = 0, val type: String, val amount: Long, val category: String, val account: String, val date: String, val note: String, val person: String, val place: String, val fee: Long, val exclude: Boolean)

class FinanceDb(context: Context): SQLiteOpenHelper(context, "so_tai_chinh.db", null, 1) {
 override fun onCreate(db: SQLiteDatabase) {
  db.execSQL("CREATE TABLE accounts(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,grp TEXT NOT NULL,balance INTEGER NOT NULL)")
  db.execSQL("CREATE TABLE tx(id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT,amount INTEGER,category TEXT,account TEXT,date TEXT,note TEXT,person TEXT,place TEXT,fee INTEGER,exclude_reports INTEGER)")
  listOf(Triple("Ví chồng","Tiền mặt",683000L),Triple("Ví vợ","Tiền mặt",38000L),Triple("Ví chung","Tiền mặt",4000000L),Triple("TK vợ VCB","Tài khoản ngân hàng",0L),Triple("TK vợ Vietin","Tài khoản ngân hàng",85425L),Triple("Tạm","Tiền mặt",1000000L),Triple("Thẻ VCB","Thẻ tín dụng",-1964363L),Triple("Mua vàng","Tài khoản đầu tư",247600000L),Triple("Tú - ô tô","Khác",1000000L),Triple("Đổi tiền","Khác",3250000L)).forEach { (n,g,b) ->
   db.insert("accounts",null,ContentValues().apply { put("name",n);put("grp",g);put("balance",b) })
  }
 }
 override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
 fun accounts(): List<Account> = buildList {
  readableDatabase.rawQuery("SELECT id,name,grp,balance FROM accounts ORDER BY id",null).use { c -> while(c.moveToNext()) add(Account(c.getLong(0),c.getString(1),c.getString(2),c.getLong(3))) }
 }
 fun addAccount(n:String,g:String,b:Long) { writableDatabase.insert("accounts",null,ContentValues().apply { put("name",n);put("grp",g);put("balance",b) }) }
 fun addTx(t:Tx) {
  val db=writableDatabase; db.beginTransaction()
  try {
   db.insertOrThrow("tx",null,ContentValues().apply { put("type",t.type);put("amount",t.amount);put("category",t.category);put("account",t.account);put("date",t.date);put("note",t.note);put("person",t.person);put("place",t.place);put("fee",t.fee);put("exclude_reports",if(t.exclude)1 else 0) })
   val delta=if(t.type=="Thu tiền") t.amount else -(t.amount+t.fee)
   db.execSQL("UPDATE accounts SET balance=balance+? WHERE name=?",arrayOf(delta,t.account))
   db.setTransactionSuccessful()
  } finally { db.endTransaction() }
 }
 fun transactions(): List<Tx> = buildList {
  readableDatabase.rawQuery("SELECT id,type,amount,category,account,date,note,person,place,fee,exclude_reports FROM tx ORDER BY id DESC",null).use { c -> while(c.moveToNext()) add(Tx(c.getLong(0),c.getString(1),c.getLong(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6),c.getString(7),c.getString(8),c.getLong(9),c.getInt(10)==1)) }
 }
}
