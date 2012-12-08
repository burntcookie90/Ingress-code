package com.dinosaurwithakatana.ingress;

import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;


import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.app.Application;
import com.urbanairship.Logger;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;
import com.urbanairship.push.PushPreferences;
 

public class IngressCodeList extends ListActivity {
	
	private List<Message> messages;
	private ArrayList<String> titles;
	private ArrayList<Integer> positions;
	private ArrayList<URL> links;
	private int pushCount;
	
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        UAirship.takeOff(this.getApplication());
        
        pushCount = 1;

		String apid = PushManager.shared().getAPID();
		Logger.info("My Application onCreate - App APID: " + apid);
        setContentView(R.layout.main);
        loadFeed(ParserType.ANDROID_SAX);
        showNotification();
    }
    
	private void showNotification() {
		// TODO Auto-generated method stub
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(R.drawable.icon)
		        .setContentTitle("New codes!")
		        .setContentText(titles.size()+" new codes!");
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, IngressCodeList.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(IngressCodeList.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
//		menu.add(Menu.NONE, ParserType.ANDROID_SAX.ordinal(), 
//				ParserType.ANDROID_SAX.ordinal(), R.string.android_sax);
//		menu.add(Menu.NONE, ParserType.SAX.ordinal(), ParserType.SAX.ordinal(),
//				R.string.sax);
//		menu.add(Menu.NONE, ParserType.DOM.ordinal(), ParserType.DOM.ordinal(), 
//				R.string.dom);
//		menu.add(Menu.NONE, ParserType.XML_PULL.ordinal(), 
//				ParserType.XML_PULL.ordinal(), R.string.pull);
		menu.add(Menu.NONE,1,1,R.string.refresh);
		
		menu.add(Menu.NONE,2,1,"Push Enable");
		
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
//		ParserType type = ParserType.values()[item.getItemId()];
//		ArrayAdapter<String> adapter =
//			(ArrayAdapter<String>) this.getListAdapter();
//		if (adapter.getCount() > 0){
//			adapter.clear();
//		}
//		this.loadFeed(type);
		
		if(item.getItemId()==1){
			loadFeed(ParserType.ANDROID_SAX);
		}
		else if(item.getItemId() ==2 ){
			pushCount++;
			Log.v("Push Count",""+pushCount);
			if(pushCount%2==0){
				PushManager.enablePush();
				item.setTitle("Push Disable");
			}
			else{
				PushManager.disablePush();
				item.setTitle("Push Enable");
			}
		}
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent viewMessage = new Intent(Intent.ACTION_VIEW, 
				Uri.parse(links.get(position).toExternalForm()));
		this.startActivity(viewMessage);
	}

	private void loadFeed(ParserType type){
    	try{
    		Log.i("AndroidNews", "ParserType="+type.name());
	    	FeedParser parser = FeedParserFactory.getParser(type);
	    	long start = System.currentTimeMillis();
	    	messages = parser.parse();
	    	long duration = System.currentTimeMillis() - start;
	    	Log.i("AndroidNews", "Parser duration=" + duration);
	    	String xml = writeXml();
	    	Log.i("AndroidNews", xml);
	    	titles = new ArrayList<String>();
	    	links = new ArrayList<URL>();
	    	int count = 0;
	    	for (Message msg : messages){
	    		if(msg.getTitle().toLowerCase().contains("passcode") 
	    				|| msg.getTitle().toLowerCase().contains("new code") 
	    				|| (msg.getTitle().toLowerCase().contains("code") && !msg.getTitle().toLowerCase().contains("activation"))
	    				){
	    			Log.v("ingress",Boolean.toString(msg.getTitle().contains("passcode")));
		    		titles.add(msg.getTitle());
		    		links.add(msg.getLink());
		    		count++;
	    		}
	    	}
	    	ArrayAdapter<String> adapter = 
	    		new ArrayAdapter<String>(this, R.layout.row,titles);
	    	this.setListAdapter(adapter);
    	} catch (Throwable t){
    		Log.e("AndroidNews",t.getMessage(),t);
    	}
    }
    
	private String writeXml(){
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "messages");
			serializer.attribute("", "number", String.valueOf(messages.size()));
			for (Message msg: messages){
				serializer.startTag("", "message");
				serializer.attribute("", "date", msg.getDate());
				serializer.startTag("", "title");
				serializer.text(msg.getTitle());
				serializer.endTag("", "title");
				serializer.startTag("", "url");
				serializer.text(msg.getLink().toExternalForm());
				serializer.endTag("", "url");
				serializer.startTag("", "body");
				serializer.text(msg.getDescription());
				serializer.endTag("", "body");
				serializer.endTag("", "message");
			}
			serializer.endTag("", "messages");
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}