package com.dinosaurwithakatana.ingress;

import com.urbanairship.UAirship;
import com.urbanairship.push.CustomPushNotificationBuilder;
import com.urbanairship.push.PushManager;

import android.app.Application;

public class IngressCodeApplication extends Application{

//	public MyApplication() {
//		// TODO Auto-generated constructor stub
//	}
	
	public void onCreate(){
		super.onCreate();
		
		UAirship.takeOff(this);
		
//		CustomPushNotificationBuilder nb = new CustomPushNotificationBuilder();
//		
//		PushManager.shared().setNotificationBuilder(nb);
//		PushManager.shared().setIntentReceiver(null);
	}

}
