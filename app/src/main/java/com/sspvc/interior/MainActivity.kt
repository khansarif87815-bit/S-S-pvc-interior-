package com.sspvc.interior

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

private val Navy = Color(0xFF101827)
private val Gold = Color(0xFFD6AD60)
private val Cream = Color(0xFFF6F2EA)

data class Design(
    val id: Long, val name: String, val category: String,
    val description: String, val price: String = "ભાવ પૂછો"
)

private val categories = listOf(
    "બધા ડિઝાઇન", "PVC Wall Panel", "Fluted Panel", "Louvers Panel",
    "PVC Marble Sheet", "Charcoal Panel", "UV Sheet", "PVC Ceiling",
    "Wooden Panel", "3D Panel", "TV Unit", "PVC Partition",
    "PVC Door", "Modular Kitchen", "Wardrobe", "Furniture"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme(colorScheme = darkColorScheme(
            primary = Gold, background = Navy, surface = Color(0xFF1B2638),
            onPrimary = Navy, onBackground = Color.White, onSurface = Color.White
        )) { PVCApp() } }
    }
}

@Composable
fun PVCApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("ss_pvc_catalog", 0) }
    val seed = remember {
        listOf(
            Design(1,"Luxury Fluted TV Wall","Fluted Panel","Modern living room feature wall with warm wood finish"),
            Design(2,"White Marble Feature Wall","PVC Marble Sheet","Premium marble-look wall finish for living room"),
            Design(3,"Modern Ceiling Design","PVC Ceiling","Layered ceiling concept with elegant lighting"),
            Design(4,"Charcoal Louvers Wall","Charcoal Panel","Contemporary vertical louvers for accent walls"),
            Design(5,"Modular Kitchen","Modular Kitchen","Clean and practical PVC kitchen concept"),
            Design(6,"Designer Wardrobe","Wardrobe","Modern storage with clean panel finish"),
            Design(7,"3D Decorative Wall","3D Panel","Textured statement wall for bedroom"),
            Design(8,"PVC Room Partition","PVC Partition","Decorative partition for living spaces"),
            Design(9,"Designer PVC Door","PVC Door","Modern door panel concept"),
            Design(10,"TV Unit with Storage","TV Unit","Wall-mounted entertainment unit")
        )
    }
    fun loadDesigns(): List<Design> {
        val raw = prefs.getString("designs", null) ?: return seed
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Design(o.getLong("id"),o.getString("name"),o.getString("category"),
                    o.getString("description"),o.optString("price","ભાવ પૂછો"))
            }
        } catch (_: Exception) { seed }
    }
    var designs by remember { mutableStateOf(loadDesigns()) }
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    var search by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Design?>(null) }
    var adminMode by remember { mutableStateOf(false) }
    var showLogin by remember { mutableStateOf(false) }
    var showAdd by remember { mutableStateOf(false) }
    var favorites by remember { mutableStateOf(setOf<Long>()) }

    fun save() {
        val arr = JSONArray()
        designs.forEach { d -> arr.put(JSONObject().apply {
            put("id",d.id); put("name",d.name); put("category",d.category)
            put("description",d.description); put("price",d.price)
        }) }
        prefs.edit().putString("designs",arr.toString()).apply()
    }

    val filtered = designs.filter {
        (selectedCategory == categories.first() || it.category == selectedCategory) &&
        (it.name.contains(search,true) || it.category.contains(search,true))
    }

    Scaffold(
        containerColor = Navy,
        topBar = {
            Column(Modifier.background(Navy).padding(horizontal=18.dp, vertical=14.dp)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ss_pvc_logo),
                        contentDescription = "S S PVC INTERIOR official logo",
                        modifier = Modifier.size(width = 112.dp, height = 66.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("S S PVC INTERIOR", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White)
                        Text("AND FURNITURE", fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Gold)
                        Text("Dream Home... Our Work...", fontSize = 9.sp, color = Color.LightGray)
                    }
                    IconButton(onClick={ if(adminMode) adminMode=false else showLogin=true }) {
                        Icon(if(adminMode) Icons.Default.Close else Icons.Default.AdminPanelSettings,
                            contentDescription="Admin", tint=Gold)
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor=Color(0xFF1B2638), contentColor=Gold) {
                NavigationBarItem(selected=!adminMode && selected==null, onClick={adminMode=false;selected=null},
                    icon={Icon(Icons.Default.Home,null)}, label={Text("Home")})
                NavigationBarItem(selected=false, onClick={showLogin=true},
                    icon={Icon(Icons.Default.Lock,null)}, label={Text("Owner")})
                NavigationBarItem(selected=false, onClick={
                    val msg = "Namaste, mujhe S S PVC INTERIOR ke designs aur price ki jankari chahiye."
                    val intent=Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/?text="+Uri.encode(msg)))
                    context.startActivity(intent)
                }, icon={Icon(Icons.Default.Chat,null)}, label={Text("WhatsApp")})
            }
        }
    ) { padding ->
        if (adminMode) {
            AdminScreen(designs, onAdd={showAdd=true}, onDelete={d -> designs=designs.filterNot{it.id==d.id};save()})
        } else if (selected != null) {
            DetailScreen(selected!!, onBack={selected=null}, onEnquire={
                val msg="Namaste, mujhe ${selected!!.name} (${selected!!.category}) ke baare mein jankari chahiye."
                context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://wa.me/?text="+Uri.encode(msg))))
            })
        } else {
            HomeScreen(
                Modifier.padding(padding), search, {search=it}, selectedCategory,
                {selectedCategory=it}, filtered, favorites,
                {selected=it}, { id -> favorites=if(id in favorites) favorites-id else favorites+id }
            )
        }
    }

    if(showLogin) {
        var pin by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest={showLogin=false},
            title={Text("Owner Login")},
            text={Column { Text("Owner PIN દાખલ કરો"); OutlinedTextField(
                value=pin,onValueChange={pin=it},label={Text("PIN")},
                visualTransformation=PasswordVisualTransformation(),
                keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.NumberPassword)
            ) }},
            confirmButton={TextButton(onClick={
                if(pin=="2580") { adminMode=true;showLogin=false } else { pin="" }
            }) {Text("Login")}},
            dismissButton={TextButton(onClick={showLogin=false}){Text("Cancel")}}
        )
    }
    if(showAdd) {
        AddDesignDialog(onDismiss={showAdd=false}, onSave={d -> designs=designs+d;save();showAdd=false})
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier, search:String, onSearch:(String)->Unit,
    category:String, onCategory:(String)->Unit, designs:List<Design>,
    favorites:Set<Long>, onOpen:(Design)->Unit, onFavorite:(Long)->Unit
) {
    LazyColumn(modifier.fillMaxSize().background(Navy), contentPadding=PaddingValues(bottom=18.dp)) {
        item {
            Column(Modifier.padding(16.dp)) {
                Box(Modifier.fillMaxWidth().background(
                    Brush.linearGradient(listOf(Color(0xFF34445C),Color(0xFF151D2B))),
                    RoundedCornerShape(22.dp)).padding(20.dp)) {
                    Column {
                        Text("તમારા સપનાના ઇન્ટિરિયર માટે", color=Gold, fontSize=13.sp)
                        Spacer(Modifier.height(7.dp))
                        Text("Premium PVC
Interior Designs", fontSize=27.sp, lineHeight=32.sp,
                            fontWeight=FontWeight.Bold, color=Color.White)
                        Spacer(Modifier.height(8.dp))
                        Text("Explore panels, ceilings, TV units & more", color=Color.LightGray, fontSize=12.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Sarif Khan  •  6387351056", color=Gold, fontSize=11.sp)
                        Text("Jubar Khan  •  93528 15026", color=Color.White, fontSize=11.sp)
                        Text("Tumeer Khan  •  97939 09721", color=Color.White, fontSize=11.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value=search,onValueChange=onSearch,modifier=Modifier.fillMaxWidth(),
                    placeholder={Text("ડિઝાઇન શોધો...")},leadingIcon={Icon(Icons.Default.Search,null)},
                    singleLine=true,shape=RoundedCornerShape(14.dp))
                Spacer(Modifier.height(14.dp))
                Text("DESIGN CATEGORIES", color=Gold, fontWeight=FontWeight.Bold, fontSize=12.sp)
                Spacer(Modifier.height(9.dp))
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                    items(categories) { c ->
                        FilterChip(selected=category==c,onClick={onCategory(c)},label={Text(c)})
                    }
                }
                Spacer(Modifier.height(15.dp))
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Text("Popular Designs", fontSize=19.sp, fontWeight=FontWeight.Bold, modifier=Modifier.weight(1f))
                    Text("${designs.size} designs", color=Gold, fontSize=11.sp)
                }
                Spacer(Modifier.height(10.dp))
            }
        }
        if(designs.isEmpty()) item { Text("આ કેટેગરીમાં હજી ડિઝાઇન ઉમેરાઈ નથી.",Modifier.padding(20.dp),color=Color.LightGray) }
        items(designs.chunked(2).size) { index ->
            Row(Modifier.fillMaxWidth().padding(horizontal=14.dp), horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                designs.chunked(2)[index].forEach { d ->
                    DesignCard(d, d.id in favorites, {onOpen(d)}, {onFavorite(d.id)}, Modifier.weight(1f))
                }
                if(designs.chunked(2)[index].size==1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun DesignCard(d:Design, favorite:Boolean, onOpen:()->Unit, onFavorite:()->Unit, modifier:Modifier=Modifier) {
    Column(modifier.background(Color(0xFF1B2638),RoundedCornerShape(17.dp)).clickable{onOpen()}.padding(10.dp)) {
        Box(Modifier.fillMaxWidth().height(112.dp).background(
            Brush.linearGradient(listOf(Color(0xFF68758A),Color(0xFF29384C))),RoundedCornerShape(12.dp)),
            contentAlignment=Alignment.Center) {
            Icon(Icons.Default.Wallpaper, null, tint=Gold, modifier=Modifier.size(42.dp))
            IconButton(onClick=onFavorite,modifier=Modifier.align(Alignment.TopEnd)) {
                Icon(if(favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,null,
                    tint=if(favorite) Color.Red else Color.White)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(d.name,fontWeight=FontWeight.SemiBold,fontSize=13.sp,maxLines=2)
        Text(d.category,color=Gold,fontSize=10.sp)
        Text(d.price,color=Color.LightGray,fontSize=10.sp)
    }
}

@Composable
fun DetailScreen(d:Design,onBack:()->Unit,onEnquire:()->Unit) {
    Column(Modifier.fillMaxSize().background(Navy).padding(18.dp)) {
        TextButton(onClick=onBack){Icon(Icons.Default.ArrowBack,null);Text(" પાછા જાઓ")}
        Box(Modifier.fillMaxWidth().height(240.dp).background(
            Brush.linearGradient(listOf(Color(0xFF68758A),Color(0xFF29384C))),RoundedCornerShape(20.dp)),
            contentAlignment=Alignment.Center) { Icon(Icons.Default.Wallpaper,null,tint=Gold,modifier=Modifier.size(80.dp)) }
        Spacer(Modifier.height(18.dp))
        Text(d.name,fontSize=24.sp,fontWeight=FontWeight.Bold)
        Text(d.category,color=Gold)
        Spacer(Modifier.height(10.dp))
        Text(d.description,color=Color.LightGray)
        Spacer(Modifier.height(12.dp))
        Text("Price: ${d.price}",fontWeight=FontWeight.Bold)
        Spacer(Modifier.weight(1f))
        Button(onClick=onEnquire,modifier=Modifier.fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Navy)) {
            Icon(Icons.Default.Chat,null);Spacer(Modifier.width(8.dp));Text("WhatsApp પર પૂછપરછ કરો")
        }
    }
}

@Composable
fun AdminScreen(designs:List<Design>,onAdd:()->Unit,onDelete:(Design)->Unit) {
    Column(Modifier.fillMaxSize().background(Navy).padding(16.dp)) {
        Row(verticalAlignment=Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Owner Admin Panel",fontSize=22.sp,fontWeight=FontWeight.Bold)
                Text("તમારા ડિઝાઇન કેટલોગનું સંચાલન કરો",color=Gold,fontSize=12.sp)
            }
            Button(onClick=onAdd,colors=ButtonDefaults.buttonColors(containerColor=Gold,contentColor=Navy)) {
                Icon(Icons.Default.Add,null);Text(" Add")
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Total designs: ${designs.size}",color=Color.LightGray)
        LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)) {
            items(designs.size) { i ->
                val d=designs[i]
                Row(Modifier.fillMaxWidth().background(Color(0xFF1B2638),RoundedCornerShape(12.dp)).padding(12.dp),
                    verticalAlignment=Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(d.name,fontWeight=FontWeight.SemiBold)
                        Text(d.category,color=Gold,fontSize=11.sp)
                    }
                    IconButton(onClick={onDelete(d)}) { Icon(Icons.Default.Delete,null,tint=Color(0xFFFF7777)) }
                }
            }
        }
    }
}

@Composable
fun AddDesignDialog(onDismiss:()->Unit,onSave:(Design)->Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(categories[1]) }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("ભાવ પૂછો") }
    AlertDialog(onDismissRequest=onDismiss,
        title={Text("નવી ડિઝાઇન ઉમેરો")},
        text={
            Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name,{name=it},label={Text("ડિઝાઇનનું નામ")},singleLine=true)
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick={expanded=true},modifier=Modifier.fillMaxWidth()) { Text(category) }
                    DropdownMenu(expanded,onDismissRequest={expanded=false}) {
                        categories.drop(1).forEach { c -> DropdownMenuItem(text={Text(c)},onClick={category=c;expanded=false}) }
                    }
                }
                OutlinedTextField(description,{description=it},label={Text("વર્ણન")})
                OutlinedTextField(price,{price=it},label={Text("કિંમત / પૂછપરછ")},singleLine=true)
            }
        },
        confirmButton={TextButton(onClick={
            if(name.isNotBlank()) onSave(Design(System.currentTimeMillis(),name,category,description,price))
        }){Text("Save")}},
        dismissButton={TextButton(onClick=onDismiss){Text("Cancel")}}
    )
}
