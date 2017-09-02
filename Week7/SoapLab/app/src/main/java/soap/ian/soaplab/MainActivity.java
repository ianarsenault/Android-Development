package soap.ian.soaplab;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

//import static android.provider.UserDictionary.Words.WORD;

public class MainActivity extends AppCompatActivity {

    private static final String SOAP_ACTION = "http://services.aonaware.com/webservices/Define";
    private static final String METHOD_NAME = "Define";
    private static final String NAMESPACE = "http://services.aonaware.com/webservices/";
    private static final String URL = "http://services.aonaware.com/DictService/DictService.asmx";

    private EditText word;
    private Button getDefinition;
    private TextView requestedWord;
    private TextView definition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        word = (EditText) findViewById(R.id.etWord);
        getDefinition = (Button) findViewById(R.id.btnGetDefinitoin);
        definition = (TextView) findViewById(R.id.tvResults);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class WebOperation  extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String resultOfCall = "test";
            try {

                String Word;
                Word = params[0];

                resultOfCall = GetWordDefinition(Word);
            } catch (IOException e) {
                Log.e("Allied Error", "Foo didn't work: " + e.getMessage());
                Log.e("Allied Error2", "Full stack track:" + Log.getStackTraceString(e));
            } catch (XmlPullParserException e) {
                Log.e("Allied Error", "Foo didn't work: " + e.getMessage());
                Log.e("Allied Error2", "Full stack track:" + Log.getStackTraceString(e));
            }
            //    result.setText(resultOfCall);

            return resultOfCall;
        }
        protected void onPostExecute(String ResultOfCall) {
            definition.setText(ResultOfCall);
        }
    }


    public void GetWord(View view) {
        // Use AsyncTask execute method to Prevent ANR problem
        String requestedWord;

        requestedWord = word.getText().toString();

        new WebOperation().execute(requestedWord);
    }

    private String GetWordDefinition(String word) throws IOException, XmlPullParserException {

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo WordProp = new PropertyInfo();
        WordProp.type = PropertyInfo.STRING_CLASS;
        WordProp.name = "word";
        WordProp.setNamespace(NAMESPACE);
        WordProp.setValue(word);
        request.addProperty(WordProp);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE ht;
        System.out.println(envelope.bodyOut.toString());
        ht = new HttpTransportSE(URL);

        ht.debug = true;
        ht.call(SOAP_ACTION, envelope);

        String RequestedWord;
        String Definitions = "";

        //final  SoapPrimitive response = (SoapPrimitive)envelope.getResponse();

        SoapObject response = (SoapObject)envelope.getResponse();
        SoapPrimitive requestedWord = (SoapPrimitive)response.getProperty(0);
        RequestedWord = requestedWord.toString();

        SoapObject definitions = (SoapObject)response.getProperty("Definitions");
        Definitions += "Requested Word = " + RequestedWord;

        for (int i = 0; i < definitions.getPropertyCount(); i++) {
            SoapObject def = (SoapObject)definitions.getProperty(i);
            Definitions += "\n\n " + def.getPropertyAsString("WordDefinition");
        }

        return Definitions;


    }






}
