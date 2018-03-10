package app.ys.quakeview;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main extends AppCompatActivity {


    protected XmlPullParserFactory xmlPullParserFactory;
    protected XmlPullParser parser;
    public String urls;
    String val;
    TextView tn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();

        urls = new String("https://circuitbreakers.000webhostapp.com/data.xml");
        try {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(false);
            parser = xmlPullParserFactory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        BackgroundAsyncTask backgroundAsyncTask = new BackgroundAsyncTask();
        backgroundAsyncTask.execute(urls);
    }

    private class BackgroundAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            String returnedResult = "";
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                parser.setInput(is, null);
                returnedResult = getLoadedXmlValues(parser);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return returnedResult;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!s.equals("")) {
                tn=(TextView)findViewById(R.id.textView4);
                tn.setText(val);



            }
        }
        private String getLoadedXmlValues(XmlPullParser parser) throws XmlPullParserException, IOException {
            //REAL PARSING
            int eventType = parser.getEventType();
            String name = null;
            Entity mEntity = new Entity();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    name = parser.getName();
                    if (name.equals("vibration")) {
                        mEntity.value = parser.nextText();
                        val=mEntity.value;
                    }
                }
                eventType = parser.next();
            }
            return mEntity.value;
        }
        public class Entity {
            public String value;
        }
    }
}
