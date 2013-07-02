MI-Chat
=======
The Android app uses the Apache v2 license. License text to be added later.

Macinsiders native Android chat app

<p align="center">
  <img src="/design/latest/messages_activity_phone.png" alt="Messages screen (Phone)" height="30%" width="30%"/>
  <img src="/design/latest/messages_activity_tablet.png" alt="Messages screen (Tablet)" height="60%" width="60%"/>
</p>


Testers
-------
**You must be registered on Macinsiders.com to be able to test this app, hence you need to be a McMasterU student**

If you would like to become a beta tester for this app:

1. Join [Macinsiders Chat Testers Google + community ![](http://ssl.gstatic.com/images/icons/gplus-16.png)](https://plus.google.com/communities/111130353234641996811)
2. Click "Become a tester" on [this page](https://play.google.com/apps/testing/com.afzaln.mi_chat/)
3. [Get the app from the Google Play Store page](https://play.google.com/store/apps/details?id=com.afzaln.mi_chat)

Note
----
I have completely removed support for ant build system, using gradle instead.
If you still want to build with ant, fork this project and import it to your IDE. From there, you're on your own.

Dependencies
------------
- Android Studio + knowledge of the gradle build system
- Android-async-http (latest version from source): https://github.com/loopj/android-async-http
- Crouton (needs Android support v4 library, latest version from source): https://github.com/keyboardsurfer/Crouton
- SlidingMenu (needs Android support v4 library, latest version from source): https://github.com/jfeinstein10/SlidingMenu
- We use ActionBarSherlock with this so in SlidingFragmentActivity.java, extend SherlockFragmentActivity instead of FragmentActivity
- ActionBarSherlock (needs Android support v4 library, latest version from source): https://github.com/JakeWharton/ActionBarSherlock

Soon, dependency on Crouton and Android-async-http will be removed, the latter being replaced with Volley.

Proposed app workflow
-----------------------------------
- User enters credentials and clicks the login button. Login form is posted and PersistentCookieStore is used to save the cookies in a SharedPreference (not sure of security at the moment)
- PageProcessor.getResource() is called to execute the GET request and a w3c Document is obtained through which a Page object is created.
- The result is saved in a local database (tables for Info, Users and Messages) using a ContentProvider/Resolver.
- Using the MessagesCursorAdapter, the messages information is retrieved and the list in MessagesActivity is populated with each message styled using the BBCode information.
- Using the UserCursorAdapter, the user list information is retrieved and the list in UserListFragment is populated
- This GET is called every 3 seconds. To conserve battery, it is only done while the user is within the app and the screen is on. More backoff scenarios will be added later.

- MessageProcessor.postResource() is called to post a message. The response obtained is the same as PageProcessor.getResource() so it is parsed in the same way.
