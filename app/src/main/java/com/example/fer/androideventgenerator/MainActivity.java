package com.example.fer.androideventgenerator;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.lambdainvoker.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText et1; //Declaracion de un objeto EditText llamado et1
    EditText et2; //Declaracion de un objeto EditText llamado et2
    Button btn1;  //Declaracion de un objeto Button llamado btn1
    TextView tv1; //Declaracion de un objeto TextView llamado tv1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* Asociacion de los objetos en Java con sus correspondientes
            elementos XML mediante el metodo findViewById
        */
        et1 = (EditText)findViewById(R.id.editText);
        et2 = (EditText)findViewById(R.id.editText2);
        btn1 = (Button)findViewById(R.id.button);
        tv1 = (TextView)findViewById(R.id.textView);

        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
                this.getApplicationContext(), "us-east-1:03737c81-b05b-4b29-8bf2-9571f1243652", Regions.US_EAST_1);
        LambdaInvokerFactory factory = new LambdaInvokerFactory(this.getApplicationContext(),
                Regions.US_EAST_1, cognitoProvider);

        final MyInterface myInterface = factory.build(MyInterface.class);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dato = et1.getText().toString();
                String dato2 = et2.getText().toString();


                RequestClass request = new RequestClass(dato, dato2);
// The Lambda function invocation results in a network call.
// Make sure it is not called from the main thread.
                new AsyncTask<RequestClass, Void, ResponseClass>() {
                    @Override
                    protected ResponseClass doInBackground(RequestClass... params) {
                        // invoke "echo" method. In case it fails, it will throw a
                        // LambdaFunctionException.
                        try {
                            return myInterface.AndroidBackendLambdaFunction(params[0]);
                        } catch (LambdaFunctionException lfe) {
                            Log.e("Tag", "Failed to invoke echo", lfe);
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ResponseClass result) {
                        if (result == null) {
                            return;
                        }

                        // Do a toast
                        Toast.makeText(MainActivity.this, result.getGreetings(), Toast.LENGTH_LONG).show();
                    }
                }.execute(request);
                Intent intent = new Intent (view.getContext(), Main2Activity.class);
                intent.putExtra("dato",dato);
                intent.putExtra("dato2",dato2);
                startActivityForResult(intent, 0);


            }
        });

    }
}
