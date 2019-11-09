package com.example.chat_mobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


//Activity que irá controlar o login do usuário
public class MainActivity extends AppCompatActivity {

    //Declarando variáveis que serão usadas posteriormente e importadas do layout

    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mBtnEnter;
    private TextView mTxtAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Relacionando as variaves com o layout
        mEditEmail = findViewById(R.id.edit_email);
        mEditPassword = findViewById(R.id.edit_password);
        mBtnEnter = findViewById(R.id.btn_login);
        mTxtAccount = findViewById(R.id.txt_account);


        //Login
        mBtnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Refenciando Váriaveis para validar o Login
                String email = mEditEmail.getText().toString();
                String password = mEditPassword.getText().toString();

                Log.i("Teste", email); //   --> Verifica Entrada
                Log.i("Teste", password);//   --> Verifica Entrada


                //Valida se  Email e senha foram inseridos.
                if(email == null || email.isEmpty() || password == null || password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Senha,E-mail ou Nome devem ser preenchidos",Toast.LENGTH_LONG).show();
                return;

                }

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.i("Teste",task.getResult().getUser().getUid());

                                //Criando Intente para redirecionamento de Tela Após cadastro de usuário
                                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);

                                //Flags que fazem que as telas sejam movidas
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("Teste", e.getMessage());
                            }
                        });

            }
        });

        //Cadastrar
        mTxtAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
