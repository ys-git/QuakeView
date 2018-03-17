package app.ys.quakeview;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main extends AppCompatActivity {

    protected XmlPullParserFactory xmlPullParserFactory;
    protected XmlPullParser parser;
    public String server_url;
    public String val;
    TextView t1;
    long period;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture beeperHandle;

    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();

            server_url = new String("https://circuitbreakers.000webhostapp.com/data.xml");
            try {
                xmlPullParserFactory = XmlPullParserFactory.newInstance();
                xmlPullParserFactory.setNamespaceAware(false);
                parser = xmlPullParserFactory.newPullParser();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        GraphView graph = (GraphView) findViewById(R.id.graph);
        // data
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        // customize a little bit viewport
        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(0);
        viewport.setMaxY(10);
        viewport.setScrollable(true);

            BackgroundAsyncTask backgroundAsyncTask = new BackgroundAsyncTask();
            backgroundAsyncTask.execute(server_url);
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

                    t1=(TextView) findViewById(R.id.textView4);
                    t1.setText(val);

                }
            }
            private String getLoadedXmlValues(XmlPullParser parser) throws XmlPullParserException, IOException {
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
    @Override
    protected void onResume() {
        super.onResume();
        // we're going to simulate real time with thread that append data to the graph
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(600);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    //Recurring Server calls
    public void tx(long periods) {
        beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, periods, TimeUnit.SECONDS);
        Log.i("MyTestService", "Service at tx");

    }

    final Runnable beeper = new Runnable() {

        public void run() {
            try {

                //code to repeat

                Log.i("MyTestService", "Recurring");
            } catch (Exception e) {
                Log.e("TAG", "error in executing: It will no longer be run!: " + e.getMessage());
                e.printStackTrace();
            }
        }

    };


    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), false, 10);
    }
}
