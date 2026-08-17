package com.animales.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private LinearLayout content;
    private SharedPreferences prefs;
    private final int TEAL = Color.rgb(24, 116, 108);
    private final int TEAL_DARK = Color.rgb(18, 83, 78);
    private final int CORAL = Color.rgb(238, 126, 96);
    private final int BG = Color.rgb(248, 250, 249);
    private final int TEXT = Color.rgb(35, 42, 45);
    private final int MUTED = Color.rgb(103, 114, 116);

    private final ActivityResultLauncher<Intent> photoPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), r -> {
                if (r.getResultCode() == Activity.RESULT_OK && r.getData() != null && r.getData().getData() != null) {
                    Uri uri = r.getData().getData();
                    try { getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION); } catch (Exception ignored) {}
                    prefs.edit().putString("photo", uri.toString()).apply();
                    Toast.makeText(this, "Foto guardada", Toast.LENGTH_SHORT).show();
                    showProfile();
                }
            });

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("cachorrito", MODE_PRIVATE);
        getWindow().setStatusBarColor(TEAL_DARK);
        showHome();
    }

    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    private GradientDrawable bg(int color, float radius) {
        GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(dp((int)radius)); return g;
    }
    private GradientDrawable strokeBg(int color, int stroke, int strokeColor, float radius) {
        GradientDrawable g = bg(color, radius); g.setStroke(dp(stroke), strokeColor); return g;
    }
    private TextView label(String s, float size, int color, boolean bold) {
        TextView v = new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(color);
        v.setTypeface(Typeface.DEFAULT, bold ? Typeface.BOLD : Typeface.NORMAL); v.setIncludeFontPadding(true); return v;
    }
    private void add(View v, int top, int bottom) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2); p.setMargins(0, dp(top), 0, dp(bottom)); content.addView(v,p);
    }
    private TextView card(String title, String body, String icon, View.OnClickListener click) {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.HORIZONTAL); box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(16),dp(15),dp(16),dp(15)); box.setBackground(strokeBg(Color.WHITE,1,Color.rgb(229,234,232),18));
        TextView ic=label(icon,25,TEXT,false); ic.setGravity(Gravity.CENTER); box.addView(ic,new LinearLayout.LayoutParams(dp(44),dp(44)));
        LinearLayout words=new LinearLayout(this); words.setOrientation(LinearLayout.VERTICAL); words.setPadding(dp(12),0,0,0);
        words.addView(label(title,17,TEXT,true)); TextView b=label(body,13,MUTED,false); b.setMaxLines(3); words.addView(b);
        box.addView(words,new LinearLayout.LayoutParams(0,-2,1));
        TextView arrow=label("›",30,TEAL,false); box.addView(arrow,new LinearLayout.LayoutParams(dp(25),-2));
        box.setOnClickListener(click); return box;
    }
    private Button primary(String text) {
        Button b=new Button(this); b.setText(text); b.setTextSize(15); b.setAllCaps(false); b.setTextColor(Color.WHITE); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setGravity(Gravity.CENTER); b.setPadding(dp(12),0,dp(12),0); b.setBackground(bg(TEAL,16));
        b.setMinHeight(dp(52)); b.setStateListAnimator(null); return b;
    }
    private Button secondary(String text) {
        Button b=primary(text); b.setTextColor(TEAL_DARK); b.setBackground(strokeBg(Color.WHITE,1,Color.rgb(202,218,214),16)); return b;
    }
    private void shell(String title, String subtitle) {
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(BG);
        LinearLayout top=new LinearLayout(this); top.setOrientation(LinearLayout.VERTICAL); top.setPadding(dp(20),dp(20),dp(20),dp(18)); top.setBackground(bg(TEAL_DARK,0));
        TextView t=label("🐾  "+title,24,Color.WHITE,true); top.addView(t);
        if(subtitle!=null && !subtitle.isEmpty()) { TextView st=label(subtitle,13,Color.rgb(218,239,235),false); st.setPadding(0,dp(4),0,0); top.addView(st); }
        root.addView(top);
        content=new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(dp(20),dp(18),dp(20),dp(30));
        ScrollView scroll=new ScrollView(this); scroll.setFillViewport(true); scroll.addView(content); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    private void showHome() {
        String name=prefs.getString("name",""); String breed=prefs.getString("breed","");
        shell("Cachorrito", name.isEmpty()?"Comprende sus señales. Cuídalo mejor.":"Perfil activo: "+name);
        LinearLayout hero=new LinearLayout(this); hero.setOrientation(LinearLayout.VERTICAL); hero.setPadding(dp(18),dp(20),dp(18),dp(20)); hero.setBackground(bg(Color.rgb(255,246,240),20));
        hero.addView(label(name.isEmpty()?"Tu compañero merece ser entendido.":"Hola, humano de "+name+".",23,TEXT,true));
        TextView h=label(name.isEmpty()?"Crea su perfil y empieza a observar patrones de conducta.":"Registra lo que ves y descubre qué puede estar comunicando.",14,MUTED,false); h.setPadding(0,dp(6),0,0); hero.addView(h);
        add(hero,0,14);
        add(card("¿Qué está comunicando?","Analiza una señal y descubre interpretaciones posibles.","🔎",v->showAnalyzer()),0,10);
        add(card("Mi perro","Perfil, foto, raza, edad, peso y necesidades.","🐶",v->showProfile()),0,10);
        add(card("Historial","Guarda observaciones para detectar patrones.","📖",v->showHistory()),0,10);
        add(card("Biblioteca canina","Lenguaje corporal, contexto y señales de alerta.","📚",v->showGuide()),0,18);
        TextView sec=label("Observa el conjunto, no una señal aislada",16,TEXT,true); add(sec,0,5);
        add(label("La postura, la cara, la cola, el entorno y lo que ocurrió antes cuentan una historia. Cachorrito te ayuda a ordenar esas pistas.",14,MUTED,false),0,0);
    }

    private void showProfile() {
        shell("Mi perro","Tu ficha personal queda guardada en este teléfono.");
        LinearLayout photoRow=new LinearLayout(this); photoRow.setGravity(Gravity.CENTER_VERTICAL); photoRow.setPadding(dp(16),dp(14),dp(16),dp(14)); photoRow.setBackground(bg(Color.WHITE,18));
        ImageView image=new ImageView(this); image.setScaleType(ImageView.ScaleType.CENTER_CROP); image.setBackground(bg(Color.rgb(235,242,239),50));
        String photo=prefs.getString("photo",""); if(!photo.isEmpty()) try{image.setImageURI(Uri.parse(photo));}catch(Exception ignored){}
        else image.setImageResource(android.R.drawable.ic_menu_camera);
        photoRow.addView(image,new LinearLayout.LayoutParams(dp(78),dp(78)));
        LinearLayout pwords=new LinearLayout(this); pwords.setOrientation(LinearLayout.VERTICAL); pwords.setPadding(dp(14),0,0,0);
        pwords.addView(label("Foto de tu compañero",16,TEXT,true)); pwords.addView(label("Puedes cambiarla cuando quieras.",13,MUTED,false));
        Button pick=secondary("Elegir foto"); pick.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("image/*"); i.addCategory(Intent.CATEGORY_OPENABLE); i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION); photoPicker.launch(i);}); pwords.addView(pick,new LinearLayout.LayoutParams(-1,dp(46)));
        photoRow.addView(pwords,new LinearLayout.LayoutParams(0,-2,1)); add(photoRow,0,16);
        EditText name=field("Nombre",prefs.getString("name","")); EditText breed=field("Raza",prefs.getString("breed","")); EditText sex=field("Sexo",prefs.getString("sex",""));
        EditText age=field("Edad (años)",prefs.getString("age","")); age.setInputType(2); EditText weight=field("Peso aproximado (kg)",prefs.getString("weight","")); weight.setInputType(2|8192);
        EditText conditions=field("Condiciones, alergias o necesidades especiales",prefs.getString("conditions","")); conditions.setMinLines(3);
        add(name,0,10); add(breed,0,10); add(sex,0,10); add(age,0,10); add(weight,0,10); add(conditions,0,16);
        Button save=primary("Guardar perfil"); save.setOnClickListener(v->{prefs.edit().putString("name",name.getText().toString().trim()).putString("breed",breed.getText().toString().trim()).putString("sex",sex.getText().toString().trim()).putString("age",age.getText().toString().trim()).putString("weight",weight.getText().toString().trim()).putString("conditions",conditions.getText().toString().trim()).apply(); Toast.makeText(this,"Perfil actualizado",Toast.LENGTH_SHORT).show(); showHome();}); add(save,0,10);
        Button back=secondary("Volver"); back.setOnClickListener(v->showHome()); add(back,0,0);
    }

    private EditText field(String hint,String value){ EditText e=new EditText(this); e.setHint(hint); e.setText(value); e.setTextSize(15); e.setTextColor(TEXT); e.setHintTextColor(MUTED); e.setSingleLine(false); e.setPadding(dp(14),0,dp(14),0); e.setBackground(strokeBg(Color.WHITE,1,Color.rgb(218,226,223),14)); e.setMinHeight(dp(52)); return e; }

    private void showAnalyzer() {
        shell("Lectura de conducta","Selecciona la señal que más se parece a lo que estás viendo.");
        add(label("¿Qué estás observando?",20,TEXT,true),0,5);
        String[][] items={{"Cola baja o entre las patas","Miedo, inseguridad o estrés","🐕"},{"Orejas hacia atrás","Incomodidad, miedo o sumisión","👂"},{"Jadeo sin ejercicio","Calor, excitación, estrés o malestar","💨"},{"Ladrido repetido","Alerta, excitación, frustración o atención","🔊"},{"Se esconde o evita contacto","Miedo, estrés, dolor o necesidad de espacio","🏠"},{"Cuerpo rígido o inmóvil","Tensión o posible respuesta defensiva","⚠️"},{"Bosteza o lame el hocico","Posible tensión o conducta de apaciguamiento","👅"},{"Cuerpo relajado","Comodidad probable; confirma con el contexto","💚"}};
        for(String[] it:items) add(card(it[0],it[1],it[2],v->showResult(it[0],it[1])),0,9);
        TextView note=label("Consejo: anota qué ocurrió justo antes y después. El contexto cambia la interpretación.",13,MUTED,false); add(note,8,0);
    }

    private void showResult(String signal,String shortText) {
        shell("Lectura","Una señal es una pista, no un diagnóstico.");
        add(label(signal,24,TEXT,true),0,6); add(label(shortText,15,TEAL_DARK,false),0,16);
        String detail=""; String action=""; String risk="";
        if(signal.startsWith("Cola")){detail="Una cola baja puede aparecer con miedo, inseguridad o estrés. Mira también la postura, las orejas, la mirada y la distancia con el estímulo.";action="Dale espacio, evita forzar la interacción y observa qué desencadenó el cambio.";risk="Si el cambio es repentino o hay dolor, consulta a un veterinario.";}
        else if(signal.startsWith("Orejas")){detail="Las orejas hacia atrás pueden acompañar miedo, incomodidad o una actitud de apaciguamiento. En algunas razas la forma de las orejas limita esta lectura.";action="Reduce la presión y observa el resto del cuerpo.";risk="No uses una sola señal para etiquetar al perro como agresivo o sumiso.";}
        else if(signal.startsWith("Jadeo")){detail="El jadeo es normal después de ejercicio o con calor. Sin una causa evidente también puede acompañar excitación, estrés o malestar.";action="Comprueba temperatura, actividad reciente, agua disponible y contexto.";risk="Jadeo intenso persistente, dificultad respiratoria, debilidad o colapso requieren atención veterinaria.";}
        else if(signal.startsWith("Ladrido")){detail="El ladrido puede cumplir funciones distintas: alerta, excitación, frustración, miedo o búsqueda de atención.";action="Registra qué ocurrió antes del ladrido y qué hizo el perro después.";risk="Evita castigos físicos o gritos; pueden aumentar la activación.";}
        else if(signal.startsWith("Se esconde")){detail="Evitar personas o lugares puede relacionarse con miedo, estrés, dolor o una experiencia negativa.";action="No lo obligues a salir. Ofrece un lugar seguro y elimina presión.";risk="Si es nuevo, intenso o acompañado de cambios físicos, conviene una evaluación veterinaria.";}
        else if(signal.startsWith("Cuerpo rígido")){detail="La rigidez es una señal importante de tensión. Puede aparecer ante miedo, conflicto, dolor o una situación que el perro percibe como amenaza.";action="Aumenta la distancia y evita tocarlo o arrinconarlo.";risk="Si gruñe, muestra dientes o intenta morder, prioriza seguridad y distancia.";}
        else if(signal.startsWith("Bosteja")){detail="Bostezar o lamerse el hocico puede aparecer en contextos de tensión, pero también tiene funciones normales.";action="Busca otras señales y el contexto antes de sacar conclusiones.";risk="Si aparece junto a signos físicos anormales, consulta.";}
        else {detail="Un cuerpo suelto, movimientos suaves y una expresión relajada suelen ser compatibles con comodidad.";action="Observa si esa conducta es habitual para tu perro y qué ocurre alrededor.";risk="Un perro puede pasar rápidamente de relajado a incómodo; sigue observando.";}
        add(section("¿Qué puede significar?",detail,"🧠"),0,10); add(section("¿Qué puedes hacer ahora?",action,"✓"),0,10); add(section("Cuándo prestar más atención",risk,"⚠"),0,16);
        LinearLayout buttons=new LinearLayout(this); buttons.setOrientation(LinearLayout.HORIZONTAL);
        Button save=primary("Guardar observación"); Button again=secondary("Otra señal");
        save.setOnClickListener(v->saveObservation(signal,detail)); again.setOnClickListener(v->showAnalyzer());
        buttons.addView(save,new LinearLayout.LayoutParams(0,dp(52),1)); LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(0,dp(52),1); ap.setMargins(dp(8),0,0,0); buttons.addView(again,ap); add(buttons,0,0);
    }

    private LinearLayout section(String title,String body,String icon){ LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(16),dp(14),dp(16),dp(14)); box.setBackground(strokeBg(Color.WHITE,1,Color.rgb(224,231,228),18)); LinearLayout head=new LinearLayout(this); head.setGravity(Gravity.CENTER_VERTICAL); head.addView(label(icon,20,TEXT,false)); TextView t=label(title,16,TEXT,true); t.setPadding(dp(10),0,0,0); head.addView(t); box.addView(head); TextView b=label(body,14,MUTED,false); b.setPadding(0,dp(7),0,0); box.addView(b); return box; }

    private void saveObservation(String signal,String detail){ String now=new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(new Date()); String old=prefs.getString("history",""); String item=now+"\n"+signal+"\n"+detail; String all=item+(old.isEmpty()?"":"\n\n"+old); String[] parts=all.split("\\n\\n"); StringBuilder limited=new StringBuilder(); int count=0; for(String p:parts){if(p.trim().isEmpty())continue; if(count++>=30)break; if(limited.length()>0)limited.append("\n\n"); limited.append(p);} prefs.edit().putString("history",limited.toString()).apply(); Toast.makeText(this,"Observación guardada",Toast.LENGTH_SHORT).show(); showHistory(); }

    private void showHistory(){ shell("Historial","Tus observaciones quedan almacenadas localmente."); String h=prefs.getString("history",""); if(h.isEmpty()){ add(label("Aún no hay observaciones",21,TEXT,true),0,6); add(label("Cuando analices una conducta, guárdala aquí para comparar cambios con el tiempo.",14,MUTED,false),0,18); } else { String[] arr=h.split("\\n\\n"); for(String s:arr){ LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(dp(15),dp(13),dp(15),dp(13)); box.setBackground(bg(Color.WHITE,16)); String[] lines=s.split("\\n",3); if(lines.length>0)addTo(box,label(lines[0],12,MUTED,false)); if(lines.length>1)addTo(box,label(lines[1],16,TEXT,true)); if(lines.length>2)addTo(box,label(lines[2],13,MUTED,false)); add(box,0,9); } } Button clear=secondary("Borrar historial"); clear.setOnClickListener(v->{prefs.edit().remove("history").apply(); showHistory();}); add(clear,8,0); }
    private void addTo(LinearLayout box,View v){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,0,0,dp(4)); box.addView(v,p); }

    private void showGuide(){ shell("Biblioteca","Una guía breve para interpretar mejor a tu perro."); add(label("La regla de oro",21,TEXT,true),0,5); add(label("Nunca interpretes una señal corporal de forma aislada. Combina postura, cara, movimiento, contexto y conducta habitual.",14,MUTED,false),0,16); add(section("Cola","Mira altura, tensión y movimiento. Una cola moviéndose no significa automáticamente felicidad.","🐕"),0,9); add(section("Orejas y ojos","Observa orientación, tensión, mirada y parpadeo junto al resto del cuerpo.","👀"),0,9); add(section("Boca","Jadeo, lamido del hocico, bostezos y tensión de la boca pueden aportar información contextual.","👄"),0,9); add(section("Cuerpo","Un cuerpo encogido puede sugerir inseguridad; uno rígido puede indicar tensión. Siempre considera la raza y la situación.","🧍"),0,9); add(section("Contexto","Ruido, personas, otros animales, comida, calor, dolor y cambios recientes pueden modificar la conducta.","🌿"),0,16); add(label("Señales de alerta",19,TEXT,true),0,5); add(label("Dificultad respiratoria, colapso, convulsiones, sangrado, intoxicación, dolor intenso o vómitos persistentes no deben manejarse solo con esta aplicación.",14,Color.rgb(155,72,57),false),0,0); }
}
