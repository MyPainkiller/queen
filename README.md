1.Introduction

Queen is a novel approach to sense user's interaction with your APP. In this module, user's interaction data is obtained as soon as the screen is clicked or slided. Besides, to better understand the situation that the user using APP, this module collect environmental data based on the device. To protect user's private data and give no effect to user when using APP, this module send the user's data quietly as well as safely with AES and RSA Encryption.

With Queen, you can obtain important data to improve user's experience and keep your APP safe.

In the left of this README, we will give a brief guide to Queen. To better understand this module, we recommend you to download the example and try it.

2.Guide To Queen

2.1. Instance

Queen is a single instance in one time using. Thus, the only way to get Queen instance is:

Queen.getInstance();

2.2. Initiation

Without initiation, some parts of this module can not be work normally. Thus, you should initiate it as soon as your APP starts using 

init("type your domain session", getApplication());

2.3. Server's URL

You should notice that if you do not set the server's url which used to receive data, queen would discard the data obtained. So, you should set URL using:

setUrl("type your server's URL");

2.4. Activity Data Obtaining

To follow the using of your APP, this module provide a method to catch activity status. Here two status defined, which is open and close, based on whether the view is showed to user or not. To use this method, you should add activityDataCollect in onResume and onDestroy in the activity's lifecycle.

2.5. Exit

In some situations, it is hard to send the last data to server at the end time you follow the using. Thus we give method appExitSend(Context) to collect the last data.

2.6. Touch Event

Take the method recognizeViewEvent(Event ev, View v, Context context) to collect touch event data. Till now, we only provide method to sense clicking.

2.7. Watcher


If you want to watch the data or status from Queen, you can register watcher using registerObserver(IQueenWatcher), and cancel it using unregisterObserver(IQueenWatcher).

2.8. Others

To use sercurity feature of Queen, you should set RSA Public Key with setRSAPublicKey(String), so that Queen can encrypt raw data with it.

setSessionId(List<HttpCookie>) allows you to get cookie from your APP's traffic so that you can match Queen's data with your business data.


setDomain(String) can filter cookie based on the single domain.

addAvoidView(View) can add view to the avoidence list so that Queen can ignore this view. 
