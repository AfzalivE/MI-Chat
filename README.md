MI-Chat
=======

The Android app uses the Apache v2 license. License text to be added later.

Macinsiders native Android chat app

I have completely removed support for ant build system, using gradle instead.
If you still want to build with ant, fork this project and import it to your IDE. From there, you're on your own.

Requirements
------------
- Android Studio + knowledge of the gradle build system
- Android-async-http (latest version from source): https://github.com/loopj/android-async-http
- Crouton (needs Android support v4 library, latest version from source): https://github.com/keyboardsurfer/Crouton
- SlidingMenu (needs Android support v4 library, latest version from source): https://github.com/jfeinstein10/SlidingMenu
- We use ActionBarSherlock with this so in SlidingFragmentActivity.java, extend SherlockFragmentActivity instead of FragmentActivity
- ActionBarSherlock (needs Android support v4 library, latest version from source): https://github.com/JakeWharton/ActionBarSherlock

Maybe one day I'll recursively include these libraries in this repo

The new app workflow (in progress)
-----------------------------------

- User enters credentials and clicks the login button. Login form is posted and PersistentCookieStore is used to save the cookies in a SharedPreference (not sure of security at the moment)
- ~~A ServiceHelper is used to GET the chat web page and and parse it.~~ (ServiceHelper not implemented yet)
- PageProcesser.getResource() is called to execute the GET request and a w3c Document is obtained through which a Page object is created.
- The result is saved in a local database (tables for Users and Messages) using updateContentProvider and ContentProvider.
- Using the MessagesAdapter, the messages information is retrieved and the list in MessagesActivity is populated
- Using the UserListAdapter, the user list information is retrieved and the list in UserListFragment is populated
- This GET is called very, very frequently....but maybe the frequency should change depending on if the user is in the app or not

- ~~A ServiceHelper is used to POST a message to the chat web page~~. ContentProvider is used to obtain user information like user ID
- The response determines if the message was posted or not
- GET is called on the chat web page to refresh the contents after postinge as seen in onCreateLoader.
