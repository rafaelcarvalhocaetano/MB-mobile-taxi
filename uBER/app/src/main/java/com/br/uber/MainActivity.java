package com.br.uber;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.br.uber.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private Button btnSingIn;
    private Button btnRegister;
    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DatabaseReference users;
    private RelativeLayout rootLayout;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath).build());

        //auth
        auth = FirebaseAuth.getInstance();
        //create data base
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        btnSingIn = (Button) findViewById(R.id.btn_SingIN);
        btnRegister = (Button) findViewById(R.id.btn_Register);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);

        //navigation
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDialog();
            }
        });
        btnSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
    }

    public void showRegisterDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("REGISTRAR");
        adb.setMessage("Por favor insira seu email para cadastrar");

        LayoutInflater inflater = LayoutInflater.from(this);

        View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText edtEmail = register_layout.findViewById(R.id.edt_email);
        final MaterialEditText edtPassword = register_layout.findViewById(R.id.edt_password);
        final MaterialEditText edtNome = register_layout.findViewById(R.id.edt_nome);
        final MaterialEditText edtPhone = register_layout.findViewById(R.id.edt_phone);

        adb.setView(register_layout);

        adb.setPositiveButton("REGISTRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor entre com e-mail", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPhone.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor entre com telefone", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor entre com a senha", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Verificar o tamanho da senha", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // fazendo a autenticação com firebase
                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //sanvando usuário no banco de dados
                                User user = new User();

                                user.setEmail(edtEmail.getText().toString());
                                user.setNome(edtNome.getText().toString());
                                user.setPassword(edtPassword.getText().toString());
                                user.setPhone(edtPhone.getText().toString());

                                //colocando o email como chave primária
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Registrado com sucesso", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Falha ao cadastrar" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Falha ao cadastrar" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                return;
                            }
                        });
            }
        });

        adb.setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.show();
    }

    public void showLoginDialog() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("SIGN IN");
        adb.setMessage("Entre com email para entrar");

        LayoutInflater inflater = LayoutInflater.from(this);

        View login_layout = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText edtEmail = login_layout.findViewById(R.id.edt_email);
        final MaterialEditText edtPassword = login_layout.findViewById(R.id.edt_password);


        adb.setView(login_layout);

        adb.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor entre com e-mail", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                    Snackbar.make(rootLayout, "Por favor entre com a senha", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (edtPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Verificar o tamanho da senha", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                //logando
                auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(MainActivity.this, Welcome.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Falha "+e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        adb.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        adb.show();


    }




}
