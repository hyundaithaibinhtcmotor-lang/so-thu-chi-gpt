package vn.tung.taichinh.ui
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vn.tung.taichinh.data.*
import java.text.NumberFormat
import java.util.Locale

private val Blue=Color(0xFF2864F0)
private val Pale=Color(0xFFEAF1FF)
private val Gray=Color(0xFF7C8798)
private fun vnd(n:Long)=NumberFormat.getIntegerInstance(Locale("vi","VN")).format(n)+" đ"

@Composable fun FinanceApp(db:FinanceDb) {
 var tab by remember { mutableStateOf("Tài khoản") }
 var version by remember { mutableIntStateOf(0) }
 val accounts=remember(version){db.accounts()}
 val txs=remember(version){db.transactions()}
 Scaffold(bottomBar={ NavigationBar(containerColor=Blue) {
  listOf("Tổng quan","Tài khoản","Ghi chép","Báo cáo","Khác").forEach { label ->
   NavigationBarItem(selected=tab==label,onClick={tab=label},icon={Icon(when(label){"Tổng quan"->Icons.Default.Home;"Tài khoản"->Icons.Default.AccountBalanceWallet;"Ghi chép"->Icons.Default.AddCircle;"Báo cáo"->Icons.Default.PieChart;else->Icons.Default.GridView},null)},label={Text(label,fontSize=10.sp)},colors=NavigationBarItemDefaults.colors(selectedIconColor=Color.White,selectedTextColor=Color.White,unselectedIconColor=Color.White.copy(.7f),unselectedTextColor=Color.White.copy(.7f),indicatorColor=Color(0xFF1749C4)))
  }
 }}) { pad ->
  when(tab) {
   "Tài khoản"->Accounts(accounts,Modifier.padding(pad),{n,g,b->db.addAccount(n,g,b);version++})
   "Ghi chép"->Entry(accounts,Modifier.padding(pad),{db.addTx(it);version++;tab="Tài khoản"})
   "Tổng quan"->Overview(accounts,txs,Modifier.padding(pad))
   "Báo cáo"->Reports(txs,Modifier.padding(pad))
   else->More(Modifier.padding(pad))
  }
 }
}

@Composable private fun Accounts(data:List<Account>,mod:Modifier,onAdd:(String,String,Long)->Unit) {
 var add by remember{mutableStateOf(false)}
 Column(mod.fillMaxSize()) {
  Column(Modifier.fillMaxWidth().background(Blue).padding(16.dp)) {
   Row(verticalAlignment=Alignment.CenterVertically){Text("Tài khoản của tôi",color=Color.White,fontSize=20.sp,fontWeight=FontWeight.SemiBold);Spacer(Modifier.weight(1f));Icon(Icons.Default.Search,null,tint=Color.White);Spacer(Modifier.width(16.dp));Icon(Icons.Default.Tune,null,tint=Color.White)}
   Spacer(Modifier.height(12.dp))
   Card(colors=CardDefaults.cardColors(containerColor=Color.White),shape=RoundedCornerShape(14.dp)){Column(Modifier.fillMaxWidth().padding(14.dp)){Text("Tổng tài sản",color=Gray);Text(vnd(data.sumOf{it.balance}),fontSize=23.sp,fontWeight=FontWeight.Bold,color=Color.DarkGray)}}
   Spacer(Modifier.height(12.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("Tài khoản","Sổ tiết kiệm","Tích lũy").forEach{Surface(color=Color.White.copy(.16f),shape=RoundedCornerShape(24.dp)){Text(it,color=Color.White,modifier=Modifier.padding(horizontal=12.dp,vertical=8.dp),fontSize=12.sp)}}}
  }
  Row(Modifier.fillMaxWidth().background(Color.White).padding(14.dp),horizontalArrangement=Arrangement.SpaceBetween){Text("Tổng tiền");Text(vnd(data.sumOf{it.balance}),fontWeight=FontWeight.Bold)}
  LazyColumn(Modifier.weight(1f).background(Color(0xFFF7F8FA)).padding(horizontal=12.dp)) {
   data.groupBy{it.group}.forEach{(group,items)->item{Text("$group (${vnd(items.sumOf{it.balance})})",color=Gray,fontSize=12.sp,modifier=Modifier.padding(top=12.dp,bottom=6.dp))}
    items(items){a->Card(Modifier.fillMaxWidth().padding(bottom=7.dp),shape=RoundedCornerShape(12.dp)){Row(Modifier.fillMaxWidth().padding(12.dp),verticalAlignment=Alignment.CenterVertically){Surface(color=Pale,shape=RoundedCornerShape(8.dp),modifier=Modifier.size(38.dp)){Box(contentAlignment=Alignment.Center){Icon(Icons.Default.AccountBalanceWallet,null,tint=Blue)}};Spacer(Modifier.width(12.dp));Column(Modifier.weight(1f)){Text(a.name,fontWeight=FontWeight.SemiBold);Text(vnd(a.balance),color=Gray,fontSize=12.sp)};Icon(Icons.Default.MoreVert,null,tint=Gray)}}}
   }
   item{Button(onClick={add=true},modifier=Modifier.fillMaxWidth().padding(vertical=12.dp)){Icon(Icons.Default.Add,null);Text("Thêm tài khoản")}}
  }
 }
 if(add) AddAccount({add=false},onAdd)
}
@Composable private fun AddAccount(close:()->Unit,save:(String,String,Long)->Unit) {
 var name by remember{mutableStateOf("")};var group by remember{mutableStateOf("Tiền mặt")};var balance by remember{mutableStateOf("0")}
 AlertDialog(onDismissRequest=close,title={Text("Thêm tài khoản")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(name,{name=it},label={Text("Tên tài khoản")});var expanded by remember{mutableStateOf(false)};Box{OutlinedButton(onClick={expanded=true}){Text(group)};DropdownMenu(expanded,{expanded=false}){listOf("Tiền mặt","Tài khoản ngân hàng","Thẻ tín dụng","Tài khoản đầu tư","Khác").forEach{DropdownMenuItem(text={Text(it)},onClick={group=it;expanded=false})}}};OutlinedTextField(balance,{balance=it.filter(Char::isDigit)},label={Text("Số dư ban đầu")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))}},confirmButton={TextButton(onClick={if(name.isNotBlank())save(name,group,balance.toLongOrNull()?:0)}){Text("Lưu")}},dismissButton={TextButton(onClick=close){Text("Hủy")}})
}
@Composable private fun Entry(accounts:List<Account>,mod:Modifier,save:(Tx)->Unit) {
 var type by remember{mutableStateOf("Chi tiền")};var amount by remember{mutableStateOf("")};var category by remember{mutableStateOf("")};var account by remember{mutableStateOf(accounts.firstOrNull()?.name?:"Ví tiền mặt")};var date by remember{mutableStateOf("25/11/2026")};var note by remember{mutableStateOf("")};var person by remember{mutableStateOf("")};var place by remember{mutableStateOf("")};var fee by remember{mutableStateOf("0")};var exclude by remember{mutableStateOf(false)}
 var typeMenu by remember{mutableStateOf(false)};var chooseCategory by remember{mutableStateOf(false)};var chooseAccount by remember{mutableStateOf(false)}
 val cats=if(type=="Thu tiền")listOf("Lương","Thưởng","Kinh doanh","Lãi tiền gửi","Khác") else listOf("Ăn uống","Khác","Con cái","Mua sắm","Đi lại, xăng xe","Sinh hoạt","Hiếu hỉ")
 Column(mod.fillMaxSize().background(Color(0xFFF1F5FF))) {
  Row(Modifier.fillMaxWidth().padding(10.dp),verticalAlignment=Alignment.CenterVertically){IconButton(onClick={}){Icon(Icons.Default.History,null)};Spacer(Modifier.weight(1f));Box{OutlinedButton(onClick={typeMenu=true}){Text(type);Icon(Icons.Default.ArrowDropDown,null)};DropdownMenu(typeMenu,{typeMenu=false}){listOf("Chi tiền","Thu tiền").forEach{DropdownMenuItem(text={Text(it)},onClick={type=it;category="";typeMenu=false})}}};Spacer(Modifier.weight(1f));IconButton(onClick={val n=amount.toLongOrNull()?:0;if(n>0)save(Tx(type=type,amount=n,category=category.ifBlank{"Chưa phân loại"},account=account,date=date,note=note,person=person,place=place,fee=fee.toLongOrNull()?:0,exclude=exclude))}){Icon(Icons.Default.Check,null,tint=Blue)}}
  Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal=14.dp),verticalArrangement=Arrangement.spacedBy(10.dp)) {
   Card(shape=RoundedCornerShape(14.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.fillMaxWidth().padding(14.dp),horizontalAlignment=Alignment.CenterHorizontally){Text("Số tiền",color=Gray);OutlinedTextField(amount,{amount=it.filter(Char::isDigit)},placeholder={Text("0 đ")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number),singleLine=true)}}
   Card(shape=RoundedCornerShape(14.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.padding(14.dp)){Text("Chọn hạng mục",color=Blue,modifier=Modifier.clickable{chooseCategory=true});Text("Mục hay dùng",color=Gray,modifier=Modifier.padding(vertical=10.dp));cats.chunked(4).forEach{row->Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){row.forEach{c->Surface(Modifier.weight(1f).clickable{category=c},color=if(category==c)Pale else Color.White,shape=RoundedCornerShape(8.dp)){Column(Modifier.padding(6.dp),horizontalAlignment=Alignment.CenterHorizontally){Icon(Icons.Default.Category,null,tint=Blue);Text(c,fontSize=10.sp)}}};repeat(4-row.size){Spacer(Modifier.weight(1f))}}};TextButton(onClick={chooseCategory=true}){Text("Tất cả  >")}}}
   Card(shape=RoundedCornerShape(14.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.padding(10.dp)){TextButton(onClick={chooseAccount=true}){Text("▣  $account  ›")};Field("Ngày giao dịch",date){date=it};Field("Diễn giải",note){note=it}}}
   Card(shape=RoundedCornerShape(14.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.padding(10.dp)){Field("Chuyến đi, sự kiện",""){};Field("Chi cho ai",person){person=it};Field("Địa điểm",place){place=it}}}
   Card(shape=RoundedCornerShape(14.dp),colors=CardDefaults.cardColors(containerColor=Color.White)){Column(Modifier.padding(12.dp)){Field("Phí (VND)",fee){fee=it.filter(Char::isDigit)};Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text("Không tính vào báo cáo");Text("Giao dịch không được thống kê trong báo cáo thông thường.",fontSize=11.sp,color=Gray)};Switch(exclude,{exclude=it})}}}
   Button(onClick={val n=amount.toLongOrNull()?:0;if(n>0)save(Tx(type=type,amount=n,category=category.ifBlank{"Chưa phân loại"},account=account,date=date,note=note,person=person,place=place,fee=fee.toLongOrNull()?:0,exclude=exclude))},modifier=Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(12.dp)){Text("Lưu lại")}
   Spacer(Modifier.height(16.dp))
  }
 }
 if(chooseCategory) Choice("Chọn hạng mục",cats,{category=it;chooseCategory=false},{chooseCategory=false})
 if(chooseAccount) Choice("Chọn tài khoản",accounts.map{it.name},{account=it;chooseAccount=false},{chooseAccount=false})
}
@Composable private fun Field(label:String,value:String,onChange:(String)->Unit){OutlinedTextField(value,onChange,label={Text(label)},modifier=Modifier.fillMaxWidth(),singleLine=true)}
@Composable private fun Choice(title:String,items:List<String>,pick:(String)->Unit,close:()->Unit){AlertDialog(onDismissRequest=close,title={Text(title)},text={Column(Modifier.heightIn(max=360.dp).verticalScroll(rememberScrollState())){items.forEach{TextButton(onClick={pick(it)},modifier=Modifier.fillMaxWidth()){Text(it,modifier=Modifier.fillMaxWidth())}}}},confirmButton={TextButton(onClick=close){Text("Đóng")}})}
@Composable private fun Overview(accounts:List<Account>,txs:List<Tx>,mod:Modifier){val inc=txs.filter{it.type=="Thu tiền"&&!it.exclude}.sumOf{it.amount};val out=txs.filter{it.type=="Chi tiền"&&!it.exclude}.sumOf{it.amount+it.fee};Column(mod.verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("Tổng quan",fontSize=22.sp,fontWeight=FontWeight.Bold);Metric("Tổng tài sản",vnd(accounts.sumOf{it.balance}));Metric("Tổng thu",vnd(inc));Metric("Tổng chi",vnd(out));Metric("Dòng tiền thuần",vnd(inc-out));Text("Giao dịch gần đây",fontWeight=FontWeight.Bold);txs.take(10).forEach{Text("${it.category} · ${it.type} · ${vnd(it.amount)}")}}}
@Composable private fun Reports(txs:List<Tx>,mod:Modifier){val x=txs.filter{!it.exclude};val inc=x.filter{it.type=="Thu tiền"}.sumOf{it.amount};val out=x.filter{it.type=="Chi tiền"}.sumOf{it.amount+it.fee};Column(mod.padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("Báo cáo",fontSize=22.sp,fontWeight=FontWeight.Bold);Metric("Tổng thu",vnd(inc));Metric("Tổng chi",vnd(out));Metric("Chênh lệch",vnd(inc-out));Text("Chi tiêu theo hạng mục",fontWeight=FontWeight.Bold);x.filter{it.type=="Chi tiền"}.groupBy{it.category}.forEach{(k,v)->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(k);Text(vnd(v.sumOf{it.amount+it.fee}))}}}}
@Composable private fun Metric(label:String,value:String){Card{Column(Modifier.fillMaxWidth().padding(14.dp)){Text(label,color=Gray,fontSize=12.sp);Text(value,fontWeight=FontWeight.Bold,fontSize=19.sp)}}}
@Composable private fun More(mod:Modifier){Column(mod.padding(16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){Text("Các phân hệ",fontSize=22.sp,fontWeight=FontWeight.Bold);listOf("Vay và nợ","Tiết kiệm","Ngân sách","Kế hoạch tài chính","Quản lý nhóm","Giao dịch định kỳ","Nhắc nhở","Xuất Excel / PDF / CSV","Sao lưu và khôi phục","Cài đặt").forEach{Card{Text(it,Modifier.fillMaxWidth().padding(16.dp))}};Text("Các mục này là danh mục mở rộng, nghiệp vụ sẽ được triển khai ở các bản tiếp theo.",color=Gray,fontSize=12.sp)}}
