package com.animales.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout root, content;
    private SharedPreferences prefs;
    private TextView title;
    private final ActivityResultLauncher<Intent> photoPicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), r -> {
                if (r.getResultCode() == Activity.RESULT_OK && r.getData() != null) {
                    Uri uri = r.getData().getData();
                    if (uri != null) {
                        prefs.edit().putString("photo", uri.toString()).apply();
                        Toast.makeText(this, "Foto guardada en el perfil", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("cachorrito", MODE_PRIVATE);
        showHome();
    }

    private TextView text(String s, float size) {
        TextView v = new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(Color.rgb(38,42,48));
        v.setPadding(0, 8, 0, 8); return v;
    }
    private Button button(String s) {
        Button b = new Button(this); b.setText(s); b.setTextSize(15); b.setAllCaps(false); b.setTextColor(Color.WHITE);
        b.setBackgroundColor(Color.rgb(35,110,105)); LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, 56); p.setMargins(0,8,0,8); b.setLayoutParams(p); return b;
    }
    private void base(String heading) {
        root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setBackgroundColor(Color.rgb(248,250,249));
        LinearLayout bar = new LinearLayout(this); bar.setPadding(20,18,20,12); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setBackgroundColor(Color.WHITE);
        title = text("🐾  " + heading, 24); title.setTypeface(Typeface.DEFAULT_BOLD); bar.addView(title, new LinearLayout.LayoutParams(0,-2,1));
        TextView home = text("Inicio", 15); home.setTextColor(Color.rgb(35,110,105)); home.setOnClickListener(v -> showHome()); bar.addView(home);
        root.addView(bar); content = new LinearLayout(this); content.setOrientation(LinearLayout.VERTICAL); content.setPadding(22,18,22,22);
        ScrollView scroll = new ScrollView(this); scroll.addView(content); root.addView(scroll, new LinearLayout.LayoutParams(-1,0,1)); setContentView(root);
    }
    private void showHome() {
        base("Cachorrito");
        TextView hero=text("Entiende mejor a tu perro.\nObserva, registra y aprende.",27); hero.setTypeface(Typeface.DEFAULT_BOLD); content.addView(hero);
        content.addView(text("Una guía práctica para interpretar señales de conducta y llevar un historial de tu compañero. No reemplaza la evaluación veterinaria.",16));
        Button profile=button("🐶  Mi perro"); profile.setOnClickListener(v->showProfile()); content.addView(profile);
        Button analyze=button("🔎  ¿Qué significa esta conducta?"); analyze.setOnClickListener(v->showAnalyzer()); content.addView(analyze);
        Button history=button("📋  Historial y observaciones"); history.setOnClickListener(v->showHistory()); content.addView(history);
        Button guide=button("📚  Guía de lenguaje canino"); guide.setOnClickListener(v->showGuide()); content.addView(guide);
        content.addView(text("Primeros pasos",20));
        content.addView(text("1. Crea el perfil de tu perro.\n2. Describe lo que estás observando.\n3. Compara las señales.\n4. Guarda la observación para detectar patrones.",16));
    }
    private void showProfile() {
        base("Mi perro");
        EditText name=new EditText(this); name.setHint("Nombre"); name.setText(prefs.getString("name","")); content.addView(name);
        EditText breed=new EditText(this); breed.setHint("Raza (ej. Bulldog Inglés)"); breed.setText(prefs.getString("breed","")); content.addView(breed);
        EditText age=new EditText(this); age.setHint("Edad"); age.setInputType(2); age.setText(prefs.getString("age","")); content.addView(age);
        EditText weight=new EditText(this); weight.setHint("Peso aproximado (kg)"); weight.setInputType(2|8192); weight.setText(prefs.getString("weight","")); content.addView(weight);
        EditText conditions=new EditText(this); conditions.setHint("Condiciones o necesidades especiales"); conditions.setMinLines(2); conditions.setText(prefs.getString("conditions","")); content.addView(conditions);
        Button photo=button("📷  Seleccionar foto"); photo.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); photoPicker.launch(i);}); content.addView(photo);
        Button save=button("Guardar perfil"); save.setOnClickListener(v->{prefs.edit().putString("name",name.getText().toString()).putString("breed",breed.getText().toString()).putString("age",age.getText().toString()).putString("weight",weight.getText().toString()).putString("conditions",conditions.getText().toString()).apply(); Toast.makeText(this,"Perfil guardado",Toast.LENGTH_SHORT).show();}); content.addView(save);
    }
    private void showAnalyzer() {
        base("Analizador de conducta"); content.addView(text("Selecciona lo que estás viendo",21));
        String[] options={"Cola baja o entre las patas","Orejas hacia atrás","Jadeo sin ejercicio","Ladrido repetido","Se esconde o evita contacto","Se queda rígido / inmóvil","Bosteza o se lame el hocico","Cuerpo relajado y movimientos suaves"};
        for(String s:options){Button b=button(s); b.setOnClickListener(v->showResult(s)); content.addView(b);}
    }
    private void showResult(String signal) {
        base("Lectura de señal"); content.addView(text(signal,23)); String result="";
        if(signal.contains("Cola baja")) result="Puede indicar inseguridad, miedo o estrés. Observa el contexto, distancia y otras señales corporales.";
        else if(signal.contains("Orejas")) result="Las orejas hacia atrás pueden aparecer con miedo, incomodidad o actitud sumisa. No interpretes una señal aislada.";
        else if(signal.contains("Jadeo")) result="El jadeo puede ser normal por calor o actividad, pero también acompañar estrés o malestar. Si es intenso o persistente, consulta a un veterinario.";
        else if(signal.contains("Ladrido")) result="El ladrido comunica diferentes estados: alerta, excitación, frustración o búsqueda de atención. Mira qué ocurre justo antes y después.";
        else if(signal.contains("esconde")) result="Evitar contacto puede ser una señal de miedo, estrés, dolor o necesidad de espacio. No lo fuerces a interactuar.";
        else if(signal.contains("rígido")) result="La rigidez corporal merece atención: puede preceder una reacción defensiva o reflejar tensión. Aumenta la distancia con calma.";
        else if(signal.contains("Bosteza")) result="Bostezar o lamerse el hocico puede aparecer durante situaciones de tensión. Observa el conjunto de señales.";
        else result="Una postura relajada suele ser compatible con comodidad, pero el contexto siempre importa.";
        content.addView(text(result,17)); content.addView(text("Importante: esta lectura es orientativa y no constituye un diagnóstico veterinario.",14));
        Button save=button("Guardar observación"); save.setOnClickListener(v->{prefs.edit().putString("last",signal+" — "+result).apply(); Toast.makeText(this,"Observación guardada",Toast.LENGTH_SHORT).show();}); content.addView(save);
    }
    private void showHistory() { base("Historial"); String h=prefs.getString("last",""); content.addView(text(h.isEmpty()?"Todavía no tienes observaciones guardadas.":"Última observación:\n\n"+h,17)); }
    private void showGuide() {
        base("Guía canina");
        content.addView(text("No existe una traducción exacta de una sola postura. La interpretación mejora al combinar cuerpo, entorno, antecedentes y cambios respecto de la conducta habitual.",16));
        content.addView(text("Señales que conviene observar",21));
        content.addView(text("• Cola: posición y movimiento.\n• Orejas: orientación y tensión.\n• Ojos: mirada, parpadeo y expresión.\n• Boca: jadeo, lamido, tensión.\n• Cuerpo: relajado, inclinado, rígido o encogido.\n• Contexto: personas, otros animales, ruido, comida, calor y cambios recientes.",16));
        content.addView(text("Regla de seguridad",21)); content.addView(text("Si hay dolor aparente, dificultad respiratoria, desmayo, convulsiones, sangrado, intoxicación, vómitos persistentes u otro signo grave, la aplicación no sustituye atención veterinaria.",16));
    }
}
