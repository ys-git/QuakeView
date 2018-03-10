package app.ys.quakeview;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class Main extends AppCompatActivity {


    protected XmlPullParserFactory xmlPullParserFactory;
    protected XmlPullParser parser;
    public String xmlPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActionBar myActionBar = getSupportActionBar();
        myActionBar.hide();

        xmlPath = new String("https://circuitbreakers.000webhostapp.com/data.xml");
        try {
            xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(false);
            parser = xmlPullParserFactory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }
}
