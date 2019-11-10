package com.example.chat_mobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {


    //Declarando variáveis que serão usadas posteriormente e importadas do layout

    private EditText mEditUserName;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mBtnInsert;
    private  Button mBtnSelectedPhoto;
    private ImageView mImgPhoto;

    private Uri mSelectUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Relacionando as variaves com o layout
        mEditUserName = findViewById(R.id.edit_nome);
        mEditEmail = findViewById(R.id.edit_email);
        mEditPassword = findViewById(R.id.edit_password);
        mBtnInsert = findViewById(R.id.btn_cadastrar);
        mImgPhoto = findViewById(R.id.img_photo);
        mBtnSelectedPhoto = findViewById(R.id.btn_Selected_photo); //--> Será usado para inserir foto


        //Ação do botão para adicionar foto
        mBtnSelectedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectPhoto();

            }
        });

        //Ação do botão de cadastrar
        mBtnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(); //---> Cria o usuário
            }


        });

    }

    //Método para adicionar foto
    private void selectPhoto() {
        //Pega a galeria
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*"); //--> Procura na galeria arquivos do tipo imagem
        startActivityForResult(intent, 0); //--> Abre a galeria do dispositivo para receber dado

    }


    //Método que retorna os dados da Galeria 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == 0){
            mSelectUri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectUri);
                mImgPhoto.setImageDrawable(new BitmapDrawable(bitmap));
                mBtnSelectedPhoto.setAlpha(0);

            } catch (IOException e) {

            }

        }
    }

    //Método para Criar/Registrar usuário
    private void createUser() {
        String email = mEditEmail.getText().toString();
        String senha = mEditPassword.getText().toString();
        String name = mEditUserName.getText().toString();

        //Valida se Nome, Email e senha foram preenchidos.
        if(email == null || email.isEmpty() || senha == null || senha.isEmpty() || name == null || name.isEmpty()){
            Toast.makeText(this, "Senha,E-mail ou Nome devem ser preenchidos",Toast.LENGTH_LONG).show();
            return;

        }
        //Caso a verificação não retorne erro o novo usuário é inserido no banco

        //Módulo do Firebase que Realiza o registro do e-mail e senha
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    //Caso a criação seja feita com sucesso
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.i("Teste", task.getResult().getUser().getUid());

                            //Método para salvar
                            saveUserInFirebase();

                        }
                    }
                })
                //Caso a criação falhe
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Teste",e.getMessage());

                    }
                });

    }

    //Método que salva arquivos no firebase ( Upload de fotos)
    private void saveUserInFirebase() {
        String filename = UUID.randomUUID().toString(); //Gera aleatorio o nome

        //Referencia que será criada e armazenada no firebase (imagem do usuário)
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images "+ filename);
        ref.putFile(mSelectUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { //Faz upload do arquivo para o fire base como jpeg
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("Teste", uri.toString());



                        /**Modificações Foram Feitas aqui !! Para gerar um user id no firebase para as mensagens
                          */


                        //Atributos do usuário
                        String uid = FirebaseAuth.getInstance().getUid();;
                        String username = mEditUserName.getText().toString();
                        String profileUrl = uri.toString();

                        //Criando Objeto User
                        User user = new User(uid,username,profileUrl);
                        //Salva o user no banco de dados como uma coleção no firebase
                        FirebaseFirestore.getInstance().collection("users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        //Criando Intente para redirecionamento de Tela Após cadastro de usuário
                                        Intent intent = new Intent(RegisterActivity.this, MessagesActivity.class);

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

            }
        })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Teste", e.getMessage());
                }
            });


    }
}
