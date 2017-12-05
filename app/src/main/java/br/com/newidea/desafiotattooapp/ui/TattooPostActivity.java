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
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TattooPostActivity extends AppCompatActivity {

    private ProgressDialog progDailog;
    private EditText descricao;
    private Spinner localCorpo;
    private EditText estilo;
    private EditText numeroSessoes;
    private EditText valor;
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
        setUpEstilo();
        setUpValor();
        setUpNumeroSessoes();
        setUpSpnBodyLocation();
        setUpBtnPostarTattoo();
    }

    private void setUpNumeroSessoes() {
        numeroSessoes = (EditText) findViewById(R.id.TattooPostActivity_numeroSessoes);
    }

    private void setUpEstilo() {
        estilo = (EditText) findViewById(R.id.TattooPostActivity_estilo);
    }

    private void setUpValor() {
        valor = (EditText) findViewById(R.id.TattooPostActivity_valor);
        valor.addTextChangedListener(new NumberTextWatcher(valor));
    }

    private void setUpEdtDescricao() {
        descricao = (EditText) findViewById(R.id.TattooPostActivity_descricao);
    }

    private void setUpBtnPostarTattoo() {

        btnPostarTattoo = (Button) findViewById(R.id.TattooPostActivity_btnPostarTattoo);
        btnPostarTattoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isValidInput()) {
                    progDailog = ProgressDialog.show(TattooPostActivity.this, "Aguarde", "Efetuando postagem...", true);

                    JobRequestDTO jobRequestDTO = JobRequestDTO.builder()
                            .userId(LoggedUser.getInstance().getUserId())
                            .itemId(null) //novo post
                            .bodyLocationId(spnBodyLocation.getSelectedItemPosition() + 1L)
                            .suggestedValue(getJobSuggestedValueAsBigDecimal())
                            .suggestedSessionsNumber(Integer.parseInt(edtSessionsNumber.getText().toString()))
                            .description(edtDescription.getText().toString())
                            .visible(chkJobIsVisible.isChecked())
                            .active(true)
                            .style(edtStyle.getText().toString())
                            .imageFileName("job.bpm")
                            .base64ByteImagem(Base64.encodeToString(jobPictureByteArray, Base64.DEFAULT))
                            .build();

                    JobRemoteService trabalhoService = RemoteServiceGenerator.createService(JobRemoteService.class);
                    Call<Void> call = trabalhoService.save(jobRequestDTO);

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
        if (edtDescription.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Informe a descrição da tatuagem !", Toast.LENGTH_SHORT).show();
            edtDescription.requestFocus();
            return false;
        }

        if (edtStyle.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Informe o estilo da tatuagem !", Toast.LENGTH_SHORT).show();
            edtStyle.requestFocus();
            return false;
        }

        if (getJobSuggestedValueAsBigDecimal().compareTo(new BigDecimal(0)) <= 0) {
            Toast.makeText(this, "Informe um valor para a tatuagem !", Toast.LENGTH_SHORT).show();
            edtJobValue.requestFocus();
            return false;
        }

        if (edtSessionsNumber.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Informe a quantidade de sessões para tatuar essa tatuagem !", Toast.LENGTH_SHORT).show();
            edtSessionsNumber.requestFocus();
            return false;
        }

        return true;
    }

    private BigDecimal getJobSuggestedValueAsBigDecimal() {
        String cleanString = edtJobValue.getText().toString().replaceAll("[R,$,.]", "");
        return new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
    }

    private void setUpImgJob() {

        final Uri uri = getIntent().getParcelableExtra("job");

        Bitmap myBitmap = null;
        try {
            myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            myBitmap = ImageUtils.rotateImageIfRequired(myBitmap, uri);
            myBitmap = ImageUtils.getResizedBitmap(myBitmap, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        jobPictureByteArray = ImageUtils.getBitMapBytes(myBitmap);
        imgJob = (ImageView) findViewById(R.id.jobPostActivity_imgJob);
        imgJob.setDrawingCacheEnabled(true);
        imgJob.setImageBitmap(myBitmap);
        imgJob.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    private void setUpSpnBodyLocation() {
        spnBodyLocation = (Spinner) findViewById(R.id.jobPostActivity_spnBodyLocation);

        final ArrayList<String> locaisCorpo = new ArrayList<String>();

        final TattooPostActivity _this = this;

        BodyLocationRemoteService bodyLocationService = RemoteServiceGenerator.createService(BodyLocationRemoteService.class);
        Call<List<BodyLocationResponseDTO>> call = bodyLocationService.listar();

        call.enqueue(new Callback<List<BodyLocationResponseDTO>>() {
            @Override
            public void onResponse(Call<List<BodyLocationResponseDTO>> call, Response<List<BodyLocationResponseDTO>> response) {
                if (response.isSuccessful()) {

                    List<BodyLocationResponseDTO> bodyLocationList = response.body();

                    for (BodyLocationResponseDTO location : bodyLocationList) {
                        locaisCorpo.add(location.getName());
                    }

                    ArrayAdapter<String> adp = new ArrayAdapter<String>(_this, android.R.layout.simple_spinner_dropdown_item, locaisCorpo);
                    spnBodyLocation.setAdapter(adp);
                    spnBodyLocation.setVisibility(View.VISIBLE);
                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<List<BodyLocationResponseDTO>> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }

    public void setUpToolbar() {

        toolbar = (Toolbar) findViewById(R.id.jobPostActivity_toolbar);
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
        intentPostTrabalho.putExtra("job", picUri);
        activity.startActivity(intentPostTrabalho);
    }
}