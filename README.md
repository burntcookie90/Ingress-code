Simple notifier for ingress codes

uses urban airship for push notifications

MUST ENABLE PUSH FROM THE MENU


    <uses-permission android:name="android.permission.INTERNET" />  For receiving messages
	<uses-permission android:name="com.dinosaurwithakatana.ingress.permission.C2D_MESSAGE" /> GCM
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/> Messages
    <uses-permission android:name="android.permission.VIBRATE"/> Message notification
	<uses-permission android:name="com.google.android.c2dm.permission.RECEIVE" /> Messages
	<uses-permission android:name="android.permission.GET_ACCOUNTS" /> GCM
	<uses-permission android:name="android.permission.WAKE_LOCK" /> To receive message from broadcat receiver
