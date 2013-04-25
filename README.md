MI-Chat
=======

The Android app uses the Apache v2 license. License text to be added later.

Macinsiders native Android chat app

Requirements
------------
- Android-async-http (latest version from source): https://github.com/loopj/android-async-http
- Crouton (needs Android support v4 library, latest version from source): https://github.com/keyboardsurfer/Crouton
- SlidingMenu (needs Android support v4 library, latest version from source): https://github.com/jfeinstein10/SlidingMenu
- We use ActionBarSherlock with this so in SlidingFragmentActivity.java, extend SherlockFragmentActivity instead of FragmentActivity
- ActionBarSherlock (needs Android support v4 library, latest version from source): https://github.com/JakeWharton/ActionBarSherlock

**Make sure the version of android-support-v4 is the same for all libraries**

Maybe one day I'll recursively include these libraries in this repo


The new app workflow (in progress)
-----------------------------------

- User enters credentials and clicks the login button. Login form is posted and PersistentCookieStore is used to save the cookies in a SharedPreference (not sure of security at the moment)
- A ServiceHelper is used to GET the chat web page and and parse it. The result is saved in a local database using updateContentProvider and ContentProvider.
- Using the MessagesAdapter, the messages information is retrieved and the list in MessagesActivity is populated
- Using the UserListAdapter, the user list information is retrieved and the list in UserListFragment is populated
- This GET is called very, very frequently....but maybe the frequency should change depending on if the user is in the app or not

- A ServiceHelper is used to POST a message to the chat web page. ContentProvider is used to obtain user information like user ID 
- The response determines if the message was posted or not
- GET is called on the chat web page to refresh the contents after posting

The Old app (workflow)
---------------

- MessagesActivity calls ServiceHelper to getMessages onResume (onResume is called after launch)
- ServiceHelper puts in some stuff in an Intent and calls startService, passing it the intent
- Service performs onHandleIntent on the passed intent. Gets the processor instance for the resource type and then calls getResource/postResource on the processor instance.
- MessagesProcessor.getResource() generates a GetMessagesRestMethod object, which essentially holds everything for the request. Params, headers, body, uri, everything. Same for the postResource() method. Then getResource() tries to call execute on the RestMethod object.
- This is the execute() method in AbstractRestMethod class. Calls doRequest() with the argument being the Request object created in GetMessagesRestMethod.buildRequest() method.
- doRequest() returns the server response which contains HTTP status, headers and body. buildResult() is called on this object. 
- buildResult() gets the response HTTP status and parses the response body and, in this case, will create a Messages object, which is basically a List\<Message\>.


- Now after this, updateContentProvider() is called to update the local database.
- This database has a few tables, one of them is called "messages" as defined in ProviderContract.
- The idea is that every message is stored in the database with the relevant info and each row has a status associated with it to keep track of requests.
- This is mainly for caching reasons and for the odd times when the response might get lost because it wasn't saved anywhere and wasn't displayed/used because the user closed the app before the request completed.


(Not sure about this part)
- After updateContentProvider(), the callback is sent to the service that called getResource.
- Service sends the result back to the Activity.
- The LoaderManager uses the ContentProvider to get items from the database as seen in onCreateLoader.

Dillon's Java app
-------------

- Logs in, uses a cookie manager to persist session.
- Uses XML response from the server to parse data into Message objects

(Haven't checked the post request yet)

And that's how the messages are loaded in the way recommended by Android developers.
More information on each component is available here: https://github.com/aug-mn/restful-android/blob/catpictures/README.markdown
