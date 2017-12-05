package br.com.newidea.desafiotattooapp.remote.generator;

import android.text.TextUtils;

import br.com.newidea.desafiotattooapp.component.AuthenticationInterceptor;
import br.com.newidea.desafiotattooapp.utils.AppConstants;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by fabio on 03/05/17.
 */

public class RemoteServiceGenerator {
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(AppConstants.BASE_URL)
                    .addConverterFactory(JacksonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass) {

        String authToken = Credentials.basic("admin", "desafioTattooApp");
        return createService(serviceClass, authToken);
    }

    public static <S> S createService(
            Class<S> serviceClass, final String authToken) {
        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor interceptor =
                    new AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);

                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}
