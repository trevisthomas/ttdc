<b># ttdc</b>
TTDC.us.  My personal website.

Where my friends and i hang out online. 

Tech: gwt, hibernate, json

//Samples for the new REST interface. Still beta, but it's in production.

<p>Login.  Used to get a token.</p>
URL:http://ttdc.us/restful/login

Request:
<pre>
{
	"username": "Trav",
	"password": "pa$$word"
}
</pre>
Response:

<p>Person object and a token</p>


<p>Validate.  Used to validate a token.</p>
URL:http://ttdc.us/restful/validate

Request:
<pre>
{
	"token":"rO0ABXNy..."
}
</pre>
Response:

<p>Person object and a token</p>


<p>Get a collection of the latest posts</p>
URL: http://ttdc.us/restful/latestposts

Request:
<pre>
{
	"action": "LATEST_GROUPED",
	"pageNumber": 1
}
</pre>
Response:

<p>A jason object with with some details about the page size, page number requested and a 'list' argument which contains a 1 level deep hierarchy of the latest posts visbile to all users of TTDC sorted by the conversation with the most recient reply</p>

Request:
<pre>
{
	"action": "LATEST_FLAT",
	"pageNumber": 1
}
</pre>

Response: 

<p>Same as above except the list contains no hierarchy.  Just the posts sorted in reverse creation order.</p>

Request:
<pre>
{
	"action": "LATEST_FLAT",
	"pageNumber": 1,
	"token": "token=rO0ABXNy......."
}
</pre>

Response: 

<p>Same as above except now all post visibile to a user with the given security token are in the result list. (Private and Muted filters are applied)</p>


<p>Get a thread (topic)</p>
URL: http://ttdc.us/restful/topic

Request:
<pre>
{
	"postId": "7F1A7436-DDF6-4980-AC1A-F3AD0A5E4171",
	"pageNumber": 1,
	"type": "NESTED_THREAD_SUMMARY",
	"token": "rO0ABXN...(Optional)"
}
</pre>
Response:
<p>Returns a results json object with a format similar to request the latest comments grouped.</p>

Request:
<pre>
{
	"postId": "15807D62-5841-43FA-A004-292F6820283F",
	"pageNumber": 1,
	"type": "CONVERSATION"
}
</pre>
Response:
<p>Returns the list of replies to a conversation. </p>

<p>Post CRUD operations</p>
URL: http://ttdc.us/restful/post

/* Create a post with on the fly login
Request:
<pre>{
	"parentId": "7F1A7436-DDF6-4980-AC1A-F3AD0A5E4171",
	"action": "CREATE",
	"body": "Bla bla bla bla bla, yada yada",
	"login": "Trav",
	"password": "pa$$word"
}
</pre>
Response: 
<p>Response contains the newly created post object</p>

/* Create a post with a token
Request:
<pre>
{
	"parentId": "7F1A7436-DDF6-4980-AC1A-F3AD0A5E4171",
	"action": "CREATE",
	"body": "Bla bla bla bla bla",
	"token": "rO0ABXN...."
}
</pre>

Response: 
<p> Response contains the newly created post object</p>


/* Create a post on a new topic
Request: 
<pre>
{	
	"action":"CREATE",
	"body":"Conversation from json with new topic!",
	"forumId":"293C8189-44B9-41BD-BC75-F3DFD7CF670B",
	"title":"First Thread From Json",
	"topicDescription":"TTDC is going mobile. For that we need JSON.  I am the body of the first topic created this way.",
	"token":"rO0ABXN..."
}
</pre>

/* Read a single post
Request
<pre>
{
	"postId":"977E6A57-AE9E-461C-BFD6-2D4D337F6C69",
	"action":"READ"
}
</pre>

Response: 
<p>Response contains the post object</p>

/* Search

Request:
URL: https://ttdc.us/restful/latestConversations

<pre>
{
	"postSearchType": "CONVERSATIONS",
	"pageNumber": 1,
	"sortOrder": "BY_DATE",
	"sortDirection": "DESC"
}
</pre>

Response:
<p>Results with a list of posts. Note: This command has a lot more functionalty that isnt documented yet.</p>

Request: 
URL: https://ttdc.us/restful/autocomplete
<pre>
{
	"query": "ta",
	"token": ""
}
</pre>
Response:
<p>List of conversations that contain the query string</p>

Request:
URL: https://ttdc.us/restful/forum
<pre>
{
	"action":"LOAD_FORUMS"
}
</pre>
Response:
<p>List of forums</p>

Request:
URL: https://ttdc.us/restful/register
<pre>
{
	"deviceToken":"iOS Device Token String",
	"token": "rO0ABXNy..."
}
</pre>
Response:
<p>HTTP: 202</p>


Request:
URL: https://ttdc.us/restful/like
<pre>
{
	"postId": "8BEA35AA-5CFD-4401-858C-C0D35C5ABB72",
	"token": "rO0ABXNy..."
}
</pre>
Response:
<p>HTTP: 200</p>

Request:
URL: https://ttdc.us/restful/unlike
<pre>
{
	"postId": "8BEA35AA-5CFD-4401-858C-C0D35C5ABB72",
	"token": "rO0ABXNy..."
}
</pre>
Response:
<p>HTTP: 202</p>

Request:
URL: https://ttdc.us/restful/connect
<pre>
{
	"token": "rO0ABXNy..." (Tokens are optional here!)
}
</pre>
Response:
<p>A connection id</p>


Request:
URL: https://ttdc.us/restful/connect
<pre>
{
	"connectionId":"CONN ID GOES HERE",
	"token": "rO0ABXNy..." (Optional here too!)
}
</pre>
Response:
<p>An object with the person who made the request (or anonymous) and the list of events that have occured.</p>
<p>Note, here are the event types that i plan to test, but there are a lot more:
TRAFFIC - Person
NEW, EDIT, DELETE - Post
RESET_SERVER_BROADCAST - null?
</p>


