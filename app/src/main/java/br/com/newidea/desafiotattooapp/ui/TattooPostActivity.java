package br.com.newidea.desafiotattooapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;


import br.com.newidea.desafiotattooapp.R;
import br.com.newidea.desafiotattooapp.component.NumberTextWatcher;
import br.com.newidea.desafiotattooapp.dto.request.TattooRequestDTO;
import br.com.newidea.desafiotattooapp.remote.generator.RemoteServiceGenerator;
import br.com.newidea.desafiotattooapp.remote.service.TattooRemoteService;
import br.com.newidea.desafiotattooapp.utils.ImageUtils;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TattooPostActivity extends AppCompatActivity {

    private byte[] tattooPictureByteArray;
    private ProgressDialog progDailog;
    private Toolbar toolbar;
    private ImageView imgTattoo;
    private EditText edtDescricao;
    private EditText edtLocalCorpo;
    private EditText edtEstilo;
    private EditText edtNumeroSessoes;
    private EditText edtValor;
    private Button btnPostarTattoo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_tattoo);
        setUp();
    }

    public void setUp() {

        setUpImgJob();
        setUpToolbar();
        setUpEdtDescricao();
        setUpEdtEstilo();
        setUpEdtValor();
        setUpEdtNumeroSessoes();
        setUpEdtLocalCorpo();
        setUpBtnPostarTattoo();

    }

    private void setUpEdtNumeroSessoes() {
        edtNumeroSessoes = (EditText) findViewById(R.id.TattooPostActivity_numeroSessoes);
    }

    private void setUpEdtEstilo() {
        edtEstilo = (EditText) findViewById(R.id.TattooPostActivity_estilo);
    }

    private void setUpEdtValor() {
        edtValor = (EditText) findViewById(R.id.TattooPostActivity_valor);
        edtValor.addTextChangedListener(new NumberTextWatcher(edtValor));
    }

    private void setUpEdtDescricao() {
        edtDescricao = (EditText) findViewById(R.id.TattooPostActivity_descricao);
    }

    private void setUpEdtLocalCorpo(){
        edtLocalCorpo = (EditText) findViewById(R.id.TattooPostActivity_localCorpo);
    }

    private void setUpBtnPostarTattoo() {

        btnPostarTattoo = (Button) findViewById(R.id.TattooPostActivity_btnPostarTattoo);
        btnPostarTattoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidInput()) {
                    progDailog = ProgressDialog.show(TattooPostActivity.this, "Aguarde", "Efetuando postagem...", true);

                    TattooRequestDTO tattooRequestDTO = TattooRequestDTO.builder()
                            .id(null) //novo post
                            .descricao(edtDescricao.getText().toString())
                            .localCorpo(edtLocalCorpo.getText().toString())
                            .estilo(edtEstilo.getText().toString())
                            .numeroSessoes(Integer.parseInt(edtNumeroSessoes.getText().toString()))
                            .valor(getJobSuggestedValueAsBigDecimal())
                            .imageFileName("tattoo.bpm")
                            .base64ByteImagem(Base64.encodeToString(tattooPictureByteArray, Base64.DEFAULT))
                            .build();

                    TattooRemoteService trabalhoService = RemoteServiceGenerator.createService(TattooRemoteService.class);
                    Call<Void> call = trabalhoService.save(tattooRequestDTO);

                    call.enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                finish();
                            } else {
                            }
                            progDailog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            progDailog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private boolean isValidInput() {
        if (edtDescricao.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Informe a descrição da tatuagem !", Toast.LENGTH_SHORT).show();
            edtDescricao.requestFocus();
            return false;
        }

        if (edtEstilo.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Informe o estilo da tatuagem !", Toast.LENGTH_SHORT).show();
            edtEstilo.requestFocus();
            return false;
        }

        if (getJobSuggestedValueAsBigDecimal().compareTo(new BigDecimal(0)) <= 0) {
            Toast.makeText(this, "Informe um valor para a tatuagem !", Toast.LENGTH_SHORT).show();
            edtValor.requestFocus();
            return false;
        }

        if (edtNumeroSessoes.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Informe a quantidade de sessões para tatuar essa tatuagem !", Toast.LENGTH_SHORT).show();
            edtNumeroSessoes.requestFocus();
            return false;
        }

        return true;
    }

    private BigDecimal getJobSuggestedValueAsBigDecimal() {
        String cleanString = edtValor.getText().toString().replaceAll("[R,$,.]", "");
        return new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
    }

    private void setUpImgJob() {

        final Uri uri = getIntent().getParcelableExtra("tattoo");

        Bitmap myBitmap = null;
        try {
            myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            myBitmap = ImageUtils.rotateImageIfRequired(myBitmap, uri);
            myBitmap = ImageUtils.getResizedBitmap(myBitmap, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        tattooPictureByteArray = ImageUtils.getBitMapBytes(myBitmap);
        imgTattoo = (ImageView) findViewById(R.id.TattooPostActivity_imgTattoo);
        imgTattoo.setDrawingCacheEnabled(true);
        imgTattoo.setImageBitmap(myBitmap);
        imgTattoo.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void setUpToolbar() {

        toolbar = (Toolbar) findViewById(R.id.TattooPostActivity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setSubtitle("");

        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_material);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public static void call(@NonNull final Activity activity, @NonNull final Uri picUri) {

        final Intent intentPostTrabalho = new Intent(activity, TattooPostActivity.class);
        intentPostTrabalho.putExtra("tattoo", picUri);
        activity.startActivity(intentPostTrabalho);
    }
}
